package com.yjy.wallet.activity.other

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.*
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ripple.crypto.ecdsa.K256KeyPair
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.util.AesUtils
import com.weiyu.baselib.util.BLog
import com.weiyu.baselib.widget.DecimalInputTextWatcher
import com.weiyu.baselib.widget.NameLengthFilter
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.AddrListActivity
import com.yjy.wallet.activity.ScanActivity
import com.yjy.wallet.activity.SuccessActivity
import com.yjy.wallet.api.*
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.bean.htdf.AccountInfo
import com.yjy.wallet.bean.htdf.Send
import com.yjy.wallet.bean.neo.NEOBalance
import com.yjy.wallet.bean.neo.NeoSend
import com.yjy.wallet.bean.params.BroadCast
import com.yjy.wallet.bean.trx.rpc.RpcTRXBalance
import com.yjy.wallet.bean.trx.rpc.RpcTRXSend
import com.yjy.wallet.bean.xrp.XRPBalance
import com.yjy.wallet.bean.xrp.XRPSend
import com.yjy.wallet.chainutils.neo.ECKeyPair
import com.yjy.wallet.chainutils.neo.NEOSign
import com.yjy.wallet.chainutils.trx.TRXUtils
import com.yjy.wallet.chainutils.usdp.USDPSign
import com.yjy.wallet.chainutils.usdp.UsdpTransaction
import com.yjy.wallet.chainutils.xlm.XlmUtils
import com.yjy.wallet.chainutils.xrp.XrpUtils
import com.yjy.wallet.utils.CoinUtils
import com.yjy.wallet.utils.Error
import com.yjy.wallet.wallet.KeyStoneUtil
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import com.zhl.cbdialog.CBDialogBuilder
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_send.*
import org.bitcoinj.core.Coin
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Utils
import org.json.JSONObject
import org.web3j.crypto.CipherException
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.utils.Numeric
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.regex.Pattern

/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class SendActivity : BaseActivity() {

    override fun getContentLayoutResId(): Int = R.layout.activity_send

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initializeContentViews() {
        title_tb.setLeftLayoutClickListener(View.OnClickListener { finish() })
        title_tb.setRightLayoutClickListener(View.OnClickListener {
            if (RxPermissions(this).isGranted(Manifest.permission.CAMERA)) {
                val intent = Intent(this@SendActivity, ScanActivity::class.java)
                startActivityForResult(intent, 0x01)
            } else {
                RxPermissions(this).request(Manifest.permission.CAMERA)
                        .subscribe {
                            if (it) {
                                val intent = Intent(this@SendActivity, ScanActivity::class.java)
                                startActivityForResult(intent, 0x01)
                            } else {
                                toast(resources.getString(R.string.request_permissions))
                            }
                        }
            }
        })
        var wallet = intent.getSerializableExtra("data") as WInfo
        val waltType = WaltType.valueOf(wallet.unit)
        rl_getaddr.setOnClickListener {
            var intent = Intent(this@SendActivity, AddrListActivity::class.java)
            intent.putExtra("type", 0)
            intent.putExtra("unit", waltType)
            startActivityForResult(intent, 100)
        }
        if (waltType == WaltType.XRP || waltType == WaltType.NEO || waltType == WaltType.TRX) {
            et_remark.visibility = View.GONE
        }
        if (waltType == WaltType.XLM) {
            tv_gas1.visibility = View.VISIBLE
        }
        var i = object : InputFilter {
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
                var p = Pattern.compile("[a-zA-Z|\u4e00-\u9fa5|。.,，！!?？1234567890]+")
                var m = p.matcher(source.toString())
                if (!m.matches()) return ""
                return null
            }
        }
        et_remark.filters = arrayOf(i, NameLengthFilter(100))
        var t = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setback(wallet)
            }

        }
        et_address.addTextChangedListener(t)
        if (wallet.unit == WaltType.NEO.name) {
            et_amount.inputType = InputType.TYPE_CLASS_NUMBER
        }
        et_amount.addTextChangedListener(
                DecimalInputTextWatcher(
                        et_amount,
                        10,
                        when (wallet.unit) {
                            WaltType.NEO.name -> 0
                            WaltType.EOS.name -> 4
                            else -> Constant.priceP
                        },
                        DecimalInputTextWatcher.Back {
                            setback(wallet)
                        })
        )
        et_remark.addTextChangedListener(t)

        tv_price.text =
                resources.getString(R.string.send_price) + com.weiyu.baselib.util.Utils.toSubStringDegistForChart(
                        wallet.balance!!.toDouble(),
                        Constant.priceP,
                        false
                ) + " " + waltType.name.toUpperCase()
        btn_sure.setOnClickListener {
            if (com.weiyu.baselib.update.SysUtils.getNetWorkState(this@SendActivity) == -1) {
                toast(resources.getString(R.string.not_net))
            } else {
                create(
                        et_address.text.toString().trim(),
                        wallet.address,
                        et_amount.text.toString(),
                        et_remark.text.toString(),
                        wallet
                )
            }
        }
        if (intent.getIntExtra("hasAddress", 0) == 1) {
            et_address.setText(intent.getStringExtra("data1"))
        }
    }

    fun setback(w: WInfo) {
        if (!TextUtils.isEmpty(et_amount.text)) {
            var rmb = Coin.parseCoin(et_amount.text.toString()).toPlainString().toDouble().times(w.rmb)
            tv_rmb_price.text = "≈${com.weiyu.baselib.util.Utils.toSubStringDegistForChart(
                    rmb,
                    Constant.rmbP,
                    true
            ) + Constant.unit}"
        }
        if (!TextUtils.isEmpty(et_address.text) && !TextUtils.isEmpty(et_amount.text)) {
            btn_sure.isEnabled = true
            btn_sure.setBackgroundResource(R.drawable.btn_5f8def_selector)
        } else {
            btn_sure.isEnabled = false
            btn_sure.setBackgroundResource(R.drawable.btn_cccccc_selector)
        }
    }

    @SuppressLint("NewApi")
    fun create(to: String, from: String, amount1: String, remark: String, wallet: WInfo) {
        val waltType = WaltType.valueOf(wallet.unit)
        if (TextUtils.isEmpty(to)) {
            toast(resources.getString(R.string.send_not_to_hint))
            return
        }
        if (!CoinUtils.checkAddress(waltType, to)) {
            toast(resources.getString(R.string.send_address_error))
            return
        }
        if (to == from) {
            toast(resources.getString(R.string.send_to_e_from_hint))
            return
        }
        if (TextUtils.isEmpty(amount1)) {
            toast(resources.getString(R.string.send_not_amount_hint))
            return
        }
        if (amount1.toDouble() <= 0.0) {
            toast(resources.getString(R.string.send_not_amount_hint2))
            return
        }
        when (waltType) {
            WaltType.BTC -> if (amount1.toDouble() < 0.000001) {
                toast(resources.getString(R.string.send_amout_hint) + "(≥0.000001)")
                return
            }

            else -> {
                if (amount1.toDouble() < 0.0001) {
                    toast(resources.getString(R.string.send_amout_hint) + "(≥0.0001)")
                    return
                }
            }
        }
        when (waltType) {
            WaltType.htdf, WaltType.usdp, WaltType.HET -> {
                if (amount1.toDouble() + Constant.feeLimit.toDouble() / 100000000 > wallet.balance.toDouble()) {
                    toast(resources.getString(R.string.send_amout_not_balance))
                    return
                }
            }
        }
        val notSign = USDPSign.getNotSignTransaction(
                from,
                to,
                amount1,
                "satoshi",
                remark,
                Constant.feeLimit, waltType == WaltType.HET
        )
        when (waltType) {
            WaltType.usdp, WaltType.HET -> {
                showProgressDialog("")
                USDPService(waltType.name)
                        .getAccount(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe(object : ResourceSubscriber<AccountInfo>() {
                            override fun onComplete() {
                            }

                            override fun onNext(data: AccountInfo) {
                                dismissProgressDialog()
                                if (data.value.coins != null && data.value.coins.isNotEmpty()) {
                                    var amount = 0.0
                                    for (b in data.value.coins) {
                                        amount = amount.plus(b.amount.toDouble())
                                    }
                                    wallet.balance = amount.toString()
                                    wallet.sequence = data.value.sequence
                                    wallet.account_number = data.value.account_number
                                    MyWalletUtils.instance.updateCheckInfo(wallet)
                                    if ((amount1.toDouble() * 100000000 + Constant.feeLimit.toDouble() / 100000000) <= (wallet.balance.toDouble() * 100000000)) {
                                        show(resources.getString(R.string.send_type), to, from, amount1, notSign, wallet)
                                    } else {
                                        toast(resources.getString(R.string.send_amout_not_balance))
                                    }
                                } else {
                                    toast(resources.getString(R.string.recharge_hint_no_price))
                                }

                            }

                            override fun onError(t: Throwable) {
                                dismissProgressDialog()
                                BLog.d(t.message.toString())
                                if ((amount1.toDouble() * 100000000 + Constant.feeLimit.toDouble() / 100000000) <= (wallet.balance.toDouble() * 100000000)) {
                                    show(resources.getString(R.string.send_type), to, from, amount1, notSign, wallet)
                                } else {
                                    toast(resources.getString(R.string.send_amout_not_balance))
                                }
                            }
                        })
            }
            WaltType.NEO -> {
                NEOService.instance.balance(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe(object : ResourceSubscriber<NEOBalance>() {
                            override fun onComplete() {
                            }

                            override fun onNext(t: NEOBalance) {
                                dismissProgressDialog()
                                var balance = 0.0
                                if (t.balance.isNotEmpty()) {
                                    t.balance.forEach {
                                        if (it.asset_symbol == wallet.unit) {
                                            balance += it.amount
                                            wallet.contract_address = it.asset_hash
                                        }
                                    }
                                }

                                wallet.balance = balance.toString()
                                MyWalletUtils.instance.updateCheckInfo(wallet)
                                if (amount1.toDouble() <= wallet.balance.toDouble()) {
                                    show(resources.getString(R.string.send_type), to, from, amount1, notSign, wallet)
                                } else {
                                    toast(resources.getString(R.string.send_amout_not_balance))
                                }
                            }

                            override fun onError(e: Throwable) {
                                Error.error(e, this@SendActivity)
                            }
                        })
            }
            WaltType.TRX -> {
                showProgressDialog("")
                TRXService.instance.balance(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe(object : ResourceSubscriber<RpcTRXBalance>() {
                            override fun onComplete() {
                            }

                            override fun onNext(t: RpcTRXBalance) {
                                dismissProgressDialog()
                                if (t.success && t.data.isNotEmpty()) {
                                    var data = t.data[0]
                                    wallet.balance = (data.balance.toDouble() / 1000000).toString()
                                } else {
                                    wallet.balance = "0"
                                }
                                MyWalletUtils.instance.updateCheckInfo(wallet)
                                if ((amount1.toDouble() * 1000000 + Constant.feeLimit.toDouble() / 1000000) <= (wallet.balance.toDouble() * 1000000)) {
                                    show(resources.getString(R.string.send_type), to, from, amount1, notSign, wallet)
                                } else {
                                    toast(resources.getString(R.string.send_amout_not_balance))
                                }
                            }

                            override fun onError(e: Throwable) {
                                Error.error(e, this@SendActivity)
                            }
                        })
            }
            WaltType.XRP -> {
                showProgressDialog("")
                XRPService.instance.balance(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe(object : ResourceSubscriber<XRPBalance>() {
                            override fun onComplete() {
                            }

                            override fun onNext(t: XRPBalance) {
                                dismissProgressDialog()
                                if (t.result.status == "success") {
                                    wallet.balance = (t.result.account_data.Balance.toDouble() / 1000000).toString()
                                    wallet.sequence = t.result.account_data.Sequence.toString()
                                    wallet.account_number = t.result.ledger_current_index.toString()
                                } else {
                                    wallet.balance = "0"
                                }
                                MyWalletUtils.instance.updateCheckInfo(wallet)
                                if ((amount1.toDouble() * 1000000 + Constant.feeLimit.toDouble() / 1000000) <= (wallet.balance.toDouble() * 1000000)) {
                                    show(resources.getString(R.string.send_type), to, from, amount1, notSign, wallet)
                                } else {
                                    toast(resources.getString(R.string.send_amout_not_balance))
                                }
                            }

                            override fun onError(e: Throwable) {
                                Error.error(e, this@SendActivity)
                            }
                        })
            }
            WaltType.XLM -> {
                showProgressDialog("")
                XLMService().getAccounts(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            dismissProgressDialog()
                            if (it.balances.isNotEmpty()) {
                                wallet.balance = it.balances[0].balance
                                wallet.sequence = it.sequenceNumber.toString()
                                MyWalletUtils.instance.updateCheckInfo(wallet)
                            }
                            if (amount1.toDouble() <= wallet.balance.toDouble()) {
                                show(resources.getString(R.string.send_type), to, from, amount1, notSign, wallet)
                            } else {
                                toast(resources.getString(R.string.send_amout_not_balance))
                            }
                        }, {
                            Error.error(it, this@SendActivity)
                        })
            }
            else -> {
                show(resources.getString(R.string.send_type), to, from, amount1, notSign, wallet)
            }
        }

    }

    // Get the results:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x01)
                if (data != null) {
                    runOnUiThread {
                        et_address.setText(data.getStringExtra("sacanurl"))
                    }
                }
            if (requestCode == 100) {
                runOnUiThread {
                    et_address.setText(data?.getStringExtra("sacanurl"))
                }
            }
        }
    }

    fun show(s1: String, s2: String, s3: String, s4: String, notSign: UsdpTransaction, wallet: WInfo) {
        val dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 1f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_BOTTOM)
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setView(R.layout.dialog_send_one)
                .create()
        with(dialog) {
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.right_layout).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.tv_m1).text = s1
            findViewById<TextView>(R.id.tv_m2).text = s2
            findViewById<TextView>(R.id.tv_m3).text = s3
            findViewById<TextView>(R.id.tv_m4).text = s4 + wallet.unit.toUpperCase()
            findViewById<TextView>(R.id.btn_sure).setOnClickListener {
                show2(notSign, wallet, s4)
                dismiss()
            }
            show()
        }
    }

    fun show2(notSign: UsdpTransaction, wallet: WInfo, price: String) {
        val waltType = WaltType.valueOf(wallet.unit)
        val dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 1f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_BOTTOM)
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setView(R.layout.dialog_send_two)
                .create()
        with(dialog) {
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            val pwd = findViewById<EditText>(R.id.et_pwd)
            setEdittxtForHw(pwd)
            findViewById<RelativeLayout>(R.id.right_layout).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_sure).setOnClickListener {
                if (TextUtils.isEmpty(pwd.text)) {
                    toast(resources.getString(R.string.send_input_pwd))
                    return@setOnClickListener
                }
                showProgressDialog(resources.getString(R.string.send_commit))
                when (wallet.unit) {
                    WaltType.XLM.name -> {
                        Flowable.just("")
                                .compose(bindToLifecycle())
                                .map {
                                    var keyHax = AesUtils.decrypt(wallet.keystone, pwd.text.toString())
                                    ECKey.fromPrivate(Numeric.hexStringToByteArray(keyHax))
                                }
                                .flatMap {
                                    var t = XlmUtils.sign(com.weiyu.baselib.util.Utils.toSubStringDegistForChartStr(price,
                                            7, true), it, this@SendActivity.et_address.text.toString(), wallet.sequence.toLong(),
                                            this@SendActivity.et_remark.text.toString())
                                    XLMService().send(t)
                                }
                                .subscribe({
                                    if (it.hash != null) {
                                        dismissProgressDialog()
                                        val tx1 = TxItem(
                                                "0",
                                                it.hash.toLowerCase(),
                                                wallet.address,
                                                this@SendActivity.et_address.text.toString(),
                                                price,
                                                wallet.unit,
                                                "",
                                                System.currentTimeMillis(),
                                                Constant.main
                                        )
                                        tx1.saveOrUpdateAsync("Hash = ?", it.hash.toLowerCase()).listen {
                                            val intent = Intent(this@SendActivity, SuccessActivity::class.java)
                                            intent.putExtra("data3", wallet)
                                            intent.putExtra("tx", tx1)
                                            startActivity(intent)
                                            dismiss()
                                            finish()
                                        }
                                    } else {
                                        toast(resources.getString(R.string.success_title3))
                                    }
                                }, {
                                    Error.error(it, this@SendActivity)
                                })
                    }
                    WaltType.NEO.name -> {
                        NEOService.instance.balance(wallet.address)
                                .compose(bindToLifecycle())
                                .map {
                                    var key = if (wallet.keystone.startsWith("{")) {
                                        val walletfile = KeyStoneUtil.getWalletFile(wallet.keystone)
                                        var c = Credentials.create(Wallet.decrypt(pwd.text.toString(), walletfile))
                                        ECKey.fromPrivate(c.ecKeyPair.privateKey)
                                    } else {
                                        var keyHax = AesUtils.decrypt(wallet.keystone, pwd.text.toString())
                                        ECKey.fromPrivate(Numeric.hexStringToByteArray(keyHax))
                                    }
                                    val utxo = it.balance
                                    val list = utxo.filter { it.asset_hash == wallet.contract_address }.toMutableList()
                                    NEOSign.sign(ECKeyPair.create(key.privKey), this@SendActivity.et_address.text.toString(), price, wallet.contract_address, list)
                                }
                                .subscribeOn(Schedulers.io())
                                .flatMap {
                                    if (TextUtils.isEmpty(it)) {
                                        Flowable.error(BackErrorException("签名出错"))
                                    } else
                                        NEOService.instance.send(it)
                                }.subscribe(object : ResourceSubscriber<NeoSend>() {
                                    override fun onComplete() {

                                    }

                                    override fun onNext(t: NeoSend) {
                                        if (t.result.isNotEmpty()) {
                                            if (t.result[0].sendrawtransactionresult) {
                                                val tx1 = TxItem(
                                                        "0",
                                                        t.result[0].txid.substring(2).toLowerCase(),
                                                        wallet.address,
                                                        this@SendActivity.et_address.text.toString(),
                                                        price,
                                                        wallet.unit,
                                                        "",
                                                        System.currentTimeMillis(),
                                                        Constant.main
                                                )
                                                tx1.saveOrUpdate("Hash = ?", t.result[0].txid.substring(2).toLowerCase())
                                                val intent = Intent(this@SendActivity, SuccessActivity::class.java)
                                                intent.putExtra("data3", wallet)
                                                intent.putExtra("tx", tx1)
                                                startActivity(intent)
                                                dismiss()
                                                finish()
                                            } else {
                                                toast(t.result[0].errorMessage)
                                            }
                                        }
                                        dismissProgressDialog()
                                    }

                                    override fun onError(e: Throwable) {
                                        Error.error(e, this@SendActivity)
                                    }
                                })

                    }
                    WaltType.TRX.name -> {
                        var jsonObject: JSONObject? = null
                        TRXService.instance.getLastBlock()
                                .compose(bindToLifecycle())
                                .map {
                                    var key = if (wallet.keystone.startsWith("{")) {
                                        val walletfile = KeyStoneUtil.getWalletFile(wallet.keystone)
                                        var c = Credentials.create(Wallet.decrypt(pwd.text.toString(), walletfile))
                                        ECKey.fromPrivate(c.ecKeyPair.privateKey)
                                    } else {
                                        var keyHax = AesUtils.decrypt(wallet.keystone, pwd.text.toString())
                                        ECKey.fromPrivate(Numeric.hexStringToByteArray(keyHax))
                                    }
                                    var block = it.block_header.raw_data
                                    var jsonstring = TRXUtils.sign(key.privKey,
                                            this@SendActivity.et_address.text.toString(),
                                            (price.toDouble() * 1000000).toLong(),
                                            block.timestamp,
                                            block.number,
                                            it.blockID)
                                    jsonObject = JSONObject(jsonstring)
                                    jsonstring
                                }
                                .subscribeOn(Schedulers.io())
                                .flatMap {
                                    if (TextUtils.isEmpty(it)) {
                                        Flowable.error(BackErrorException("签名出错"))
                                    } else
                                        TRXService.instance.send(it)
                                }
                                .subscribe(object : ResourceSubscriber<RpcTRXSend>() {
                                    override fun onComplete() {
                                    }

                                    override fun onNext(t: RpcTRXSend) {
                                        dismissProgressDialog()
                                        if (t.result) {
                                            val tx1 = TxItem(
                                                    "0",
                                                    jsonObject?.getString("txID")?.toLowerCase(),
                                                    wallet.address,
                                                    this@SendActivity.et_address.text.toString(),
                                                    price,
                                                    wallet.unit,
                                                    "",
                                                    System.currentTimeMillis(),
                                                    Constant.main
                                            )
                                            tx1.saveOrUpdate("Hash = ?", jsonObject?.getString("txID")?.toLowerCase())
                                            val intent = Intent(this@SendActivity, SuccessActivity::class.java)
                                            intent.putExtra("data3", wallet)
                                            intent.putExtra("tx", tx1)
                                            startActivity(intent)
                                            dismiss()
                                            finish()
                                        } else {
                                            toast(resources.getString(R.string.success_title3))
                                        }
                                    }

                                    override fun onError(e: Throwable) {
                                        Error.error(e, this@SendActivity)
                                    }
                                })
                    }
                    WaltType.XRP.name -> {
                        Flowable.just("")
                                .compose(bindToLifecycle())
                                .map {
                                    if (wallet.keystone.startsWith("{")) {
                                        val walletfile = KeyStoneUtil.getWalletFile(wallet.keystone)
                                        var c = Credentials.create(Wallet.decrypt(pwd.text.toString(), walletfile))
                                        ECKey.fromPrivate(c.ecKeyPair.privateKey)
                                    } else {
                                        var keyHax = AesUtils.decrypt(wallet.keystone, pwd.text.toString())
                                        ECKey.fromPrivate(Numeric.hexStringToByteArray(keyHax))
                                    }
                                }
                                .map {
                                    var key = K256KeyPair(it.privKey, Numeric.toBigInt(it.pubKey))
                                    XrpUtils().sign(wallet.address, key, this@SendActivity.et_address.text.toString(), (price.toDouble() * 1000000).toLong(), 12, wallet.sequence, wallet.account_number)
                                }
                                .flatMap {
                                    if (TextUtils.isEmpty(it)) {
                                        Flowable.error(BackErrorException("签名出错"))
                                    } else
                                        XRPService.instance.send(it)
                                }
                                .subscribe(object : ResourceSubscriber<XRPSend>() {
                                    override fun onComplete() {
                                    }

                                    override fun onNext(t: XRPSend) {
                                        dismissProgressDialog()
                                        if (t.result.status == "success" && t.result.engine_result_code == 0) {
                                            val tx1 = TxItem(
                                                    "0",
                                                    t.result.tx_json.hash.toLowerCase(),
                                                    wallet.address,
                                                    this@SendActivity.et_address.text.toString(),
                                                    price,
                                                    wallet.unit,
                                                    "",
                                                    System.currentTimeMillis(),
                                                    Constant.main
                                            )
                                            tx1.saveOrUpdateAsync("Hash = ?", t.result.tx_json.hash.toLowerCase())
                                                    .listen {
                                                        val intent = Intent(this@SendActivity, SuccessActivity::class.java)
                                                        intent.putExtra("data3", wallet)
                                                        intent.putExtra("tx", tx1)
                                                        startActivity(intent)
                                                        dismiss()
                                                        finish()
                                                    }
                                        } else {
                                            toast(t.result.engine_result_message)
                                        }
                                    }

                                    override fun onError(e: Throwable) {
                                        Error.error(e, this@SendActivity)
                                    }
                                })
                    }
                    else -> {
                        var sign: UsdpTransaction? = null
                        Flowable.just("")
                                .compose(bindToLifecycle())
                                .map {
                                    if (wallet.keystone.startsWith("{")) {
                                        val walletfile = KeyStoneUtil.getWalletFile(wallet.keystone)
                                        var c = Credentials.create(Wallet.decrypt(pwd.text.toString(), walletfile))
                                        ECKey.fromPrivate(c.ecKeyPair.privateKey)
                                    } else {
                                        var keyHax = AesUtils.decrypt(wallet.keystone, pwd.text.toString())
                                        ECKey.fromPrivate(Numeric.hexStringToByteArray(keyHax))
                                    }
                                }.map {
                                    sign = USDPSign.getSignTransaction(wallet, it, notSign)
                                    val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().create()
                                    Utils.HEX.encode(gson.toJson(sign).toByteArray())
                                }
                                .flatMap { USDPService(waltType.name).broadcast(BroadCast(it)) }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : ResourceSubscriber<Send>() {
                                    override fun onComplete() {

                                    }

                                    override fun onNext(t: Send) {
                                        dismissProgressDialog()
                                        if (!TextUtils.isEmpty(t.raw_log)) {
                                            val json = JSONObject(t.raw_log.replace("\\", ""))
                                            if (json.has("message")) {
                                                toast(json.getString("message"))
                                            } else {
                                                toast(t.raw_log)
                                            }
                                        } else {
                                            val intent = Intent(this@SendActivity, SuccessActivity::class.java)
                                            intent.putExtra("data1", Gson().toJson(sign).toString())
                                            intent.putExtra("data2", t)
                                            intent.putExtra("data3", wallet)
                                            intent.putExtra("price", price)
                                            startActivity(intent)
                                            dismiss()
                                            finish()
                                        }
                                    }

                                    override fun onError(e: Throwable) {
                                        dismissProgressDialog()
                                        if (e is CipherException) {
                                            toast(resources.getString(R.string.send_err_pwd))
                                        } else if (e is HttpException) {     //   HTTP错误
                                            toast("Network connection error")
                                        } else if (e is ConnectException || e is UnknownHostException) {   //   连接错误
                                            toast("connection error")
                                        } else if (e is InterruptedIOException) {   //  连接超时
                                            toast("Connection timed out")
                                        } else if (e is BackErrorException) {
                                            toast(e.errMsg)
                                        } else {
                                            toast("unknown error")
                                        }
                                    }
                                })


                    }
                }
            }
            show()
        }
    }

}