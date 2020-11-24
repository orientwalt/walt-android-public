package com.yjy.wallet.activity.utxo

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
import android.widget.SeekBar
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaeger.library.StatusBarUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.net.BaseResult
import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.util.AesUtils
import com.weiyu.baselib.util.Utils
import com.weiyu.baselib.widget.DecimalInputTextWatcher
import com.weiyu.baselib.widget.NameLengthFilter
import com.yjy.wallet.Constant
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.R
import com.yjy.wallet.activity.AddrListActivity
import com.yjy.wallet.activity.ScanActivity
import com.yjy.wallet.activity.SuccessActivity
import com.yjy.wallet.api.CXCService
import com.yjy.wallet.api.QtumService
import com.yjy.wallet.api.TokenSendService
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.bean.cxc.CUtxo
import com.yjy.wallet.bean.cxc.CXCSendResult
import com.yjy.wallet.bean.qtum.QtumSend
import com.yjy.wallet.bean.qtum.QtumUtxo
import com.yjy.wallet.bean.tokenview.TUtxo
import com.yjy.wallet.bean.waltbean.InfoItem2
import com.yjy.wallet.chainutils.bch.BCHUtils
import com.yjy.wallet.chainutils.btc.BTCUtils
import com.yjy.wallet.chainutils.cxc.CXCUtils
import com.yjy.wallet.chainutils.dash.DashUtils
import com.yjy.wallet.chainutils.ltc.LTCUtils
import com.yjy.wallet.chainutils.qtum.QTUMUtils
import com.yjy.wallet.utils.CoinUtils
import com.yjy.wallet.utils.NetworkUtil
import com.yjy.wallet.wallet.KeyStoneUtil
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import com.zhl.cbdialog.CBDialogBuilder
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_send_btc.*
import org.bitcoinj.core.*
import org.bitcoinj.script.Script
import org.bouncycastle.util.encoders.Hex
import org.web3j.crypto.CipherException
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.utils.Numeric
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class SendBTCActivity : BaseActivity() {
    var fee = 0L
    lateinit var wallet: WInfo
    override fun getContentLayoutResId(): Int = R.layout.activity_send_btc
    val list: MutableList<UTXO> = arrayListOf()
    var minFee = 1
    var max = 199

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initializeContentViews() {
        if (intent.getIntExtra("hasAddress", 0) == 1) {
            et_address.setText(intent.getStringExtra("data1"))
        }
        title_tb.setTitleTextColor(R.color.txt_333333)
        title_tb.setLeftLayoutClickListener(View.OnClickListener { finish() })
        title_tb.setRightLayoutClickListener(View.OnClickListener {
            if (RxPermissions(this).isGranted(Manifest.permission.CAMERA)) {
                val intent = Intent(this@SendBTCActivity, ScanActivity::class.java)
                startActivityForResult(intent, 0x01)
            } else {
                RxPermissions(this).request(Manifest.permission.CAMERA)
                        .subscribe {
                            if (it) {
                                val intent = Intent(this@SendBTCActivity, ScanActivity::class.java)
                                startActivityForResult(intent, 0x01)
                            } else {
                                toast(resources.getString(R.string.request_permissions))
                            }
                        }
            }
        })
        wallet = intent.getSerializableExtra("data") as WInfo
        val waltType = WaltType.valueOf(wallet.unit)
        rl_getaddr.setOnClickListener {
            val intent = Intent(this@SendBTCActivity, AddrListActivity::class.java)
            intent.putExtra("type", 0)
            intent.putExtra("unit", waltType)
            startActivityForResult(intent, 100)
        }
        val i = object : InputFilter {
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
                val p = Pattern.compile("[a-zA-Z|\u4e00-\u9fa5|。.,，！!?？1234567890]+")
                val m = p.matcher(source.toString())
                if (!m.matches()) return ""
                return null
            }
        }
        et_remark.filters = arrayOf(i, NameLengthFilter(100))
        val t = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setback(wallet)
            }

        }
        et_address.addTextChangedListener(t)
        et_amount.addTextChangedListener(
                DecimalInputTextWatcher(
                        et_amount,
                        10,
                        Constant.priceP,
                        DecimalInputTextWatcher.Back {
                            setback(wallet)
                        })
        )
        et_remark.addTextChangedListener(t)
        sb_fee.max = max
        tv_price.text =
                resources.getString(R.string.send_price) + Utils.toSubStringDegistForChart(
                        wallet.balance.toDouble(),
                        Constant.priceP,
                        false
                ) + " " + waltType.name.toUpperCase()
        btn_sure.setOnClickListener {
            if (com.weiyu.baselib.update.SysUtils.getNetWorkState(this@SendBTCActivity) == -1) {
                toast(resources.getString(R.string.not_net))
            } else {
                create(
                        et_address.text.toString().trim(),
                        et_amount.text.toString(),
                        et_remark.text.toString(),
                        wallet
                )
            }
        }
        when (waltType) {
            WaltType.QTUM -> {
                ll_fee.visibility = View.GONE
                tv_fee_hint.visibility = View.VISIBLE
                QtumService.instance.utxo(wallet.address)
                        .compose(bindToLifecycle())
                        .subscribe(object : ResourceSubscriber<List<QtumUtxo>>() {
                            override fun onComplete() {
                            }

                            override fun onNext(t: List<QtumUtxo>) {
                                var fee = (t.size.toLong() * 148 + 2 * 34 + 10) * 500
                                tv_fee_hint.text = String.format(resources.getString(R.string.send_fee), "${Coin.valueOf(fee).toPlainString()} ${wallet.unit}")
                            }

                            override fun onError(t: Throwable?) {
                            }
                        })
                tv_fee_hint.text = String.format(resources.getString(R.string.send_fee), "0.00113 ${wallet.unit}")
            }
            WaltType.CXC -> {
                ll_fee.visibility = View.GONE
                tv_fee_hint.visibility = View.VISIBLE
                tv_fee_hint.text = String.format(resources.getString(R.string.send_fee), "0.0001 CXC")
            }
            else -> {
                sb_fee.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        val p = (progress + minFee)
                        tv_wei.text = "$p sat/b"
                        set(waltType, p)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })

                when (wallet.unit) {
                    WaltType.LTC.name, WaltType.BSV.name, WaltType.DASH.name,WaltType.BCH.name, WaltType.BTC.name, WaltType.USDT.name -> {
                        showProgressDialog("")
                        TokenSendService().utxo(wallet.address, if (wallet.unit == WaltType.USDT.name) WaltType.BTC else WaltType.valueOf(wallet.unit))
                                .compose(bindToLifecycle())
                                .subscribe({
                                    dismissProgressDialog()
                                    it.data?.forEach {
                                        if (it.confirmations >= 6)
                                            list.add(UTXO(
                                                    Sha256Hash.wrap(it.txid),
                                                    it.output_no,
                                                    Coin.parseCoin(it.value),
                                                    it.block_no,
                                                    false,
                                                    Script(Hex.decode(BTCUtils().getAddressHax(wallet.address)))))
                                    }
                                }, {
                                    dismissProgressDialog()
                                })
                    }
                }
                rl_fee.visibility = View.VISIBLE
                getFee(true)
                Flowable.interval(10 * 1000, TimeUnit.MILLISECONDS)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (!isPause)
                                getFee(false)
                        }, {

                        })
            }
        }
    }

    var isPause = false
    override fun onResume() {
        super.onResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    fun set(waltType: WaltType, p: Int) {
        var btcPrice = "0.0"
        if (!TextUtils.isEmpty(Constant.mainChainRMB)) {
            var mainChainRMB = Gson().fromJson<MutableList<InfoItem2>>(Constant.mainChainRMB, object : TypeToken<MutableList<InfoItem2>>() {}.type)
            var item = mainChainRMB.find { it.symbol == WaltType.BTC.name }
            if (item != null) {
                btcPrice = if (TextUtils.isEmpty(item.price_cny)) "0" else item.price_cny
            }
        }
        var price = 546L
        when (waltType) {
            WaltType.USDT, WaltType.BTC -> {
                if (waltType == WaltType.BTC) {
                    if (!TextUtils.isEmpty(et_amount.text) && et_amount.text.toString().toDouble() > 0) {
                        price = Coin.parseCoin(et_amount.text.toString()).longValue()
                    }
                }
                fee = BTCUtils().getFee(price, p.toLong(), list)
                val str = Coin.valueOf(fee).toFriendlyString()
                val rmb = Coin.valueOf(fee).toPlainString().toDouble().times(btcPrice.toDouble())
                tv_btc.text = "$str ≈${Utils.toSubStringDegistForChart(
                        rmb,
                        Constant.rmbP,
                        true
                ) + Constant.unit}"
            }
            else -> {
                if (!TextUtils.isEmpty(et_amount.text) && et_amount.text.toString().toDouble() > 0) {
                    price = Coin.parseCoin(et_amount.text.toString()).longValue()
                }
                fee = BCHUtils().getFee(price, p.toLong(), list)
                val str = Coin.valueOf(fee).toPlainString()
                val rmb = str.toDouble().times(wallet.rmb)
                tv_btc.text = "$str${wallet.unit} ≈${Utils.toSubStringDegistForChart(
                        rmb,
                        Constant.rmbP,
                        true
                ) + Constant.unit}"
            }
        }
    }

    @SuppressLint("CheckResult")
    fun getFee(isfirst: Boolean) {
        TokenSendService().getfee(if (wallet.unit == WaltType.USDT.name) WaltType.BTC else WaltType.valueOf(wallet.unit))
                .compose(bindToLifecycle())
                .subscribe({
                    if (it.code == 1 && it.data != null) {
                        var satoshi = Coin.parseCoin(it.data?.bestTxFee).value
                        var s = satoshi / it.data!!.mediumTxByte
                        if (s <= minFee) {
                            s = 2
                        }
                        tv_wei2.text = resources.getString(R.string.fee_recommend) + s + " sat/b"
                        if (isfirst) {
                            when {
                                s.toInt() > max -> {
                                    max = s.toInt() - minFee
                                    sb_fee.max = s.toInt() - s.toInt() - minFee
                                }
                                else -> {
                                    sb_fee.progress = s.toInt() - minFee
                                }
                            }

                        }
                    }
                }, {

                })
    }

    @SuppressLint("SetTextI18n")
    fun setback(w: WInfo) {
        if (!TextUtils.isEmpty(et_amount.text)) {
            val rmb = Coin.parseCoin(et_amount.text.toString()).toPlainString().toDouble().times(w.rmb)
            tv_rmb_price.text = "≈${Utils.toSubStringDegistForChart(
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
    fun create(to: String, amount1: String, remark: String, wallet: WInfo) {
        val waltType = WaltType.valueOf(wallet.unit)
        if (TextUtils.isEmpty(to)) {
            toast(resources.getString(R.string.send_not_to_hint))
            return
        }
        if (!CoinUtils.checkAddress(waltType, to)) {
            toast(resources.getString(R.string.send_address_error))
            return
        }
        if (to == wallet.address) {
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
            WaltType.BTC -> if (amount1.toDouble() < 0.00000546) {
                toast(resources.getString(R.string.send_amout_hint) + "(≥0.00000546)")
                return
            }
            else -> {
                if (amount1.toDouble() < 0.0001) {
                    toast(resources.getString(R.string.send_amout_hint) + "(≥0.0001)")
                    return
                }
            }
        }
        show(resources.getString(R.string.send_type), to, amount1, wallet)
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

    fun show(s1: String, s2: String, s4: String, wallet: WInfo) {
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
            findViewById<TextView>(R.id.tv_m3).text = wallet.address
            findViewById<TextView>(R.id.tv_m4).text = s4 + wallet.unit.toUpperCase()
            findViewById<TextView>(R.id.btn_sure).setOnClickListener {
                show2(wallet, s4, s2)
                dismiss()
            }
            show()
        }
    }

    fun show2(wallet: WInfo, price: String, to: String) {
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
                if (!NetworkUtil.getNetWorkState(this@SendBTCActivity)) {
                    toast(resources.getString(R.string.not_net))
                    return@setOnClickListener
                }
                if (TextUtils.isEmpty(pwd.text)) {
                    toast(resources.getString(R.string.send_input_pwd))
                    return@setOnClickListener
                }
                showProgressDialog(resources.getString(R.string.send_commit))
                val fl = Flowable.just("")
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
                val list: MutableList<UTXO> = arrayListOf()
                when (waltType) {
                    WaltType.DASH, WaltType.LTC, WaltType.BCH, WaltType.BTC, WaltType.BSV -> {
                        Flowable.zip(fl,
                                TokenSendService().utxo(wallet.address, waltType),
                                BiFunction<ECKey, BaseResult<List<TUtxo>>, String> { t1, t2 ->
                                    t2.data?.forEach {
                                        if (it.confirmations >= 6) {
                                            list.add(UTXO(
                                                    Sha256Hash.wrap(it.txid),
                                                    it.output_no,
                                                    Coin.parseCoin(it.value),
                                                    it.block_no,
                                                    false,
                                                    Script(Hex.decode(it.hex))))
                                        }
                                    }
                                    val tx: Transaction = when (waltType) {
                                        WaltType.BCH, WaltType.BSV -> {
                                            BCHUtils().sign(
                                                    wallet.address,
                                                    to,
                                                    t1,
                                                    Coin.parseCoin(price).longValue(),
                                                    (this@SendBTCActivity.sb_fee.progress + minFee).toLong(),
                                                    list, this@SendBTCActivity.et_remark.text.toString()
                                            )
                                        }
                                        WaltType.LTC -> {
                                            LTCUtils.sign(
                                                    wallet.address,
                                                    to,
                                                    t1,
                                                    Coin.parseCoin(price).longValue(),
                                                    (this@SendBTCActivity.sb_fee.progress + minFee).toLong(),
                                                    list, this@SendBTCActivity.et_remark.text.toString())
                                        }
                                        WaltType.DASH -> {
                                            DashUtils.sign(
                                                    wallet.address,
                                                    to,
                                                    t1,
                                                    Coin.parseCoin(price).longValue(),
                                                    (this@SendBTCActivity.sb_fee.progress + minFee).toLong(),
                                                    list, this@SendBTCActivity.et_remark.text.toString())
                                        }
                                        else -> {
                                            BTCUtils().sign(
                                                    wallet.address,
                                                    to,
                                                    t1,
                                                    Coin.parseCoin(price).longValue(),
                                                    (this@SendBTCActivity.sb_fee.progress + minFee).toLong(),
                                                    list, this@SendBTCActivity.et_remark.text.toString())
                                        }
                                    }

                                    val hash = Hex.toHexString(tx.bitcoinSerialize())
                                    hash
                                })
                                .flatMap { TokenSendService().send(it, waltType) }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    dismissProgressDialog()
                                    if (TextUtils.isEmpty(it.result)) {
                                        if (it.error != null) {
                                            toast(it.error!!.message)
                                        }
                                    } else {
                                        val intent = Intent(this@SendBTCActivity, SuccessActivity::class.java)
                                        val tx1 = TxItem(
                                                "0",
                                                it.result.toLowerCase(),
                                                wallet.address,
                                                to,
                                                price,
                                                waltType.name,
                                                this@SendBTCActivity.et_remark.text.toString(),
                                                System.currentTimeMillis(),
                                                main
                                        )
                                        tx1.saveOrUpdateAsync("Hash = ? and denom = ?", it.result.toLowerCase(), waltType.name)
                                                .listen {
                                                    intent.putExtra("data3", wallet)
                                                    intent.putExtra("tx", tx1)
                                                    startActivity(intent)
                                                    dismiss()
                                                    finish()
                                                }
                                    }
                                }, {
                                    dismissProgressDialog()
                                    err(it)
                                })
                    }
                    WaltType.CXC -> {
                        Flowable.zip(fl,
                                CXCService().getutxo(wallet.address),
                                BiFunction<ECKey, BaseResult<List<CUtxo>>, String> { t1, t2 ->
                                    var balance = 0.0
                                    t2.data?.forEach {
                                        balance += it.value.toDouble()
                                        var value = (it.value.toDouble() * 1000000).toLong()
                                        list.add(UTXO(Sha256Hash.wrap(it.txhash),
                                                it.n.toLong(),
                                                Coin.valueOf(value),
                                                0,
                                                false,
                                                Script(Hex.decode(it.hex)))
                                        )
                                    }
                                    if (balance >= (price.toDouble() + "0.0001".toDouble())) {
                                        val tx = CXCUtils().sign(
                                                wallet.address,
                                                to,
                                                t1,
                                                Coin.valueOf((price.toDouble() * 1000000).toLong()).longValue(),
                                                list, this@SendBTCActivity.et_remark.text.toString()
                                        )
                                        val hash = Hex.toHexString(tx.bitcoinSerialize())
                                        hash
                                    } else {
                                        ""
                                    }
                                }).flatMap { if (TextUtils.isEmpty(it)) Flowable.error(BackErrorException(resources.getString(R.string.coininfo_no_balance))) else CXCService().sendrawdeal(it) }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : ResourceSubscriber<CXCSendResult>() {
                                    override fun onComplete() {
                                    }

                                    override fun onNext(t: CXCSendResult) {
                                        dismissProgressDialog()
                                        if (!TextUtils.isEmpty(t.result)) {
                                            val intent = Intent(this@SendBTCActivity, SuccessActivity::class.java)
                                            val tx1 = TxItem(
                                                    "0",
                                                    t.result,
                                                    wallet.address,
                                                    to,
                                                    price,
                                                    waltType.name,
                                                    this@SendBTCActivity.et_remark.text.toString(),
                                                    System.currentTimeMillis(),
                                                    Constant.main
                                            )
                                            tx1.saveOrUpdateAsync("Hash = ? and denom = ?", t.result, waltType.name)
                                                    .listen {
                                                        intent.putExtra("data3", wallet)
                                                        intent.putExtra("tx", tx1)
                                                        startActivity(intent)
                                                        dismiss()
                                                        finish()
                                                    }
                                        }
                                    }

                                    override fun onError(e: Throwable?) {
                                        dismissProgressDialog()
                                        err(e)
                                    }
                                })
                    }
                    WaltType.QTUM -> {
                        Flowable.zip(fl,
                                QtumService.instance.utxo(wallet.address),
                                BiFunction<ECKey, List<QtumUtxo>, String> { t1, t2 ->
                                    t2.forEach {
                                        list.add(UTXO(
                                                Sha256Hash.wrap(it.txid),
                                                it.vout.toLong(),
                                                Coin.valueOf(it.satoshis),
                                                it.height,
                                                false,
                                                Script(Hex.decode(it.scriptPubKey))))
                                    }
                                    var tx = QTUMUtils.sign(wallet.address, to, t1, Coin.parseCoin(price).longValue(), list, this@SendBTCActivity.et_remark.text.toString())
                                    val hash = Hex.toHexString(tx.bitcoinSerialize())
                                    hash
                                })
                                .flatMap { QtumService.instance.send(it) }
                                .subscribe(object : ResourceSubscriber<QtumSend>() {
                                    override fun onComplete() {
                                    }

                                    override fun onNext(qsend: QtumSend) {
                                        dismissProgressDialog()
                                        val intent = Intent(this@SendBTCActivity, SuccessActivity::class.java)
                                        val tx1 = TxItem(
                                                "0",
                                                qsend.txid,
                                                wallet.address,
                                                to,
                                                price,
                                                waltType.name,
                                                this@SendBTCActivity.et_remark.text.toString(),
                                                System.currentTimeMillis(),
                                                Constant.main
                                        )
                                        tx1.saveOrUpdateAsync("Hash = ? and denom = ?", qsend.txid, waltType.name)
                                                .listen {
                                                    intent.putExtra("data3", wallet)
                                                    intent.putExtra("tx", tx1)
                                                    startActivity(intent)
                                                    dismiss()
                                                    finish()
                                                }
                                    }

                                    override fun onError(e: Throwable?) {
                                        dismissProgressDialog()
                                        err(e)
                                    }
                                })
                    }
                    WaltType.USDT -> {
                        TokenSendService().balance(wallet.address, WaltType.valueOf(wallet.unit))
                                .compose(bindToLifecycle())
                                .flatMap {
                                    if (it.code == 1) {
                                        wallet.balance = it.data!!
                                    }
                                    if (wallet.balance.toDouble() < price.toDouble()) {
                                        Flowable.error(BackErrorException(resources.getString(R.string.send_amout_not_balance)))
                                    } else
                                        Flowable.zip(fl,
                                                TokenSendService().utxo(wallet.address, WaltType.BTC),
                                                BiFunction<ECKey, BaseResult<List<TUtxo>>, String> { t1, t2 ->
                                                    t2.data?.forEach {
                                                        if (it.confirmations >= 6) {
                                                            list.add(UTXO(
                                                                    Sha256Hash.wrap(it.txid),
                                                                    it.output_no,
                                                                    Coin.parseCoin(it.value),
                                                                    it.block_no,
                                                                    false,
                                                                    Script(Hex.decode(it.hex))))
                                                        }
                                                    }
                                                    var tx = BTCUtils().omniSign(
                                                            wallet.address,
                                                            to,
                                                            t1,
                                                            Coin.parseCoin(price).longValue(),
                                                            (this@SendBTCActivity.sb_fee.progress + minFee).toLong(),
                                                            if (main) 31 else 2,
                                                            list, this@SendBTCActivity.et_remark.text.toString())
                                                    val hash = Hex.toHexString(tx.bitcoinSerialize())
                                                    hash
                                                })
                                }
                                .flatMap { TokenSendService().send(it, waltType) }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    dismissProgressDialog()
                                    if (TextUtils.isEmpty(it.result)) {
                                        if (it.error != null) {
                                            toast(it.error!!.message)
                                        }
                                    } else {
                                        val intent = Intent(this@SendBTCActivity, SuccessActivity::class.java)
                                        val tx1 = TxItem(
                                                "0",
                                                it.result.toLowerCase(),
                                                wallet.address,
                                                to,
                                                price,
                                                waltType.name,
                                                this@SendBTCActivity.et_remark.text.toString(),
                                                System.currentTimeMillis(),
                                                Constant.main
                                        )
                                        if (waltType == WaltType.USDT) {
                                            val tx2 = TxItem(
                                                    "0",
                                                    it.result.toLowerCase(),
                                                    wallet.address,
                                                    to,
                                                    Coin.valueOf(546L).toPlainString(),
                                                    WaltType.BTC.name,
                                                    this@SendBTCActivity.et_remark.text.toString(),
                                                    System.currentTimeMillis(),
                                                    Constant.main)
                                            tx2.saveOrUpdateAsync("Hash = ? and denom = ?", it.result.toLowerCase(), WaltType.BTC.name)
                                        }
                                        tx1.saveOrUpdateAsync("Hash = ? and denom = ?", it.result.toLowerCase(), waltType.name).listen {
                                            intent.putExtra("data3", wallet)
                                            intent.putExtra("tx", tx1)
                                            startActivity(intent)
                                            dismiss()
                                            finish()
                                        }
                                    }
                                }, {
                                    dismissProgressDialog()
                                    err(it)
                                })

                    }
                }
            }
            show()
        }
    }

    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setLightMode(this)
        StatusBarUtil.setColor(this, resources.getColor(R.color.white), 0)
    }

    fun err(e: Throwable?) {
        if (e is CipherException) {
            toast(resources.getString(R.string.send_err_pwd))
        } else if (e is HttpException) {     //   HTTP错误
            if (e.code() == 500) {
                toast(resources.getString(R.string.send_fail))
            } else
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
}