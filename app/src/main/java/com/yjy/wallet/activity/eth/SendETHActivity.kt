package com.yjy.wallet.activity.eth

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
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.AesUtils
import com.weiyu.baselib.util.Utils
import com.weiyu.baselib.widget.DecimalInputTextWatcher
import com.weiyu.baselib.widget.NameLengthFilter
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.AddrListActivity
import com.yjy.wallet.activity.ScanActivity
import com.yjy.wallet.activity.SuccessActivity
import com.yjy.wallet.api.TokenSendService
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.bean.waltbean.InfoItem2
import com.yjy.wallet.chainutils.eth.ETHUtils
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
import kotlinx.android.synthetic.main.activity_send_eth.*
import org.bitcoinj.core.Coin
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class SendETHActivity : BaseActivity() {
    var min = 1
    override fun getContentLayoutResId(): Int = R.layout.activity_send_eth
    var limit = 50000
    var n = 0
    var wallet: WInfo? = null
    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initializeContentViews() {
        if (intent.getIntExtra("hasAddress", 0) == 1) {
            et_address.setText(intent.getStringExtra("data1"))
        }
        title_tb.setLeftLayoutClickListener(View.OnClickListener { finish() })
        title_tb.setRightLayoutClickListener(View.OnClickListener {
            if (RxPermissions(this).isGranted(Manifest.permission.CAMERA)) {
                val intent = Intent(this@SendETHActivity, ScanActivity::class.java)
                startActivityForResult(intent, 0x01)
            } else {
                RxPermissions(this).request(Manifest.permission.CAMERA)
                        .subscribe {
                            if (it) {
                                val intent = Intent(this@SendETHActivity, ScanActivity::class.java)
                                startActivityForResult(intent, 0x01)
                            } else {
                                toast(resources.getString(R.string.request_permissions))
                            }
                        }
            }
        })
        tv_gas_setting.setOnClickListener {
            var i = Intent(this, ETHFeeSettingActivity::class.java)
            i.putExtra("gas", sb_fee.progress + min)
            i.putExtra("gaslimit", limit)
            i.putExtra("min", min)
            i.putExtra("nonce", n)
            startActivityForResult(i, 2)
        }
        wallet = intent.getSerializableExtra("data") as WInfo
        if (wallet!!.unit != WaltType.ETH.name && WaltType.ETC.name != wallet!!.unit) {
            limit = 90000
        }
        rl_getaddr.setOnClickListener {
            var intent = Intent(this@SendETHActivity, AddrListActivity::class.java)
            intent.putExtra("type", 0)
            intent.putExtra("unit", if (wallet!!.unit == WaltType.ETC.name) WaltType.ETC else WaltType.ETH)
            startActivityForResult(intent, 100)
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
                setback(wallet!!)
            }

        }
        et_address.addTextChangedListener(t)


        var d = Constant.priceP
        if (wallet!!.unit != WaltType.ETH.name && wallet!!.unit != WaltType.ETC.name) {
            d = if (wallet!!.decimal <= 4) {
                wallet!!.decimal
            } else {
                4
            }
        }
        et_amount.addTextChangedListener(
                DecimalInputTextWatcher(
                        et_amount,
                        10,
                        d,
                        DecimalInputTextWatcher.Back {
                            setback(wallet!!)
                        })
        )
        et_remark.addTextChangedListener(t)
        tv_price.text = resources.getString(R.string.send_price) + Utils.toSubStringDegistForChart(
                wallet!!.balance!!.toDouble(),
                Constant.priceP,
                false
        ) + " " + wallet!!.unit.toUpperCase()
        btn_sure.setOnClickListener {
            if (com.weiyu.baselib.update.SysUtils.getNetWorkState(this@SendETHActivity) == -1) {
                toast(resources.getString(R.string.not_net))
            } else {
                create(
                        et_address.text.toString().trim(),
                        wallet!!.address,
                        et_amount.text.toString(),
                        et_remark.text.toString(),
                        wallet!!
                )
            }
        }
        sb_fee.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var p = (progress + min)
                setPrice(p)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        sb_fee.max = 199
        getFee(true)
        Flowable.interval(10 * 1000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribe({
                    if (!isPause)
                        getFee(false)
                }, {

                })
    }

    fun setPrice(p: Int) {
        var ethPrice = "0.0"
        if (!TextUtils.isEmpty(Constant.mainChainRMB)) {
            var mainChainRMB = Gson().fromJson<MutableList<InfoItem2>>(Constant.mainChainRMB, object : TypeToken<MutableList<InfoItem2>>() {}.type)
            var item = mainChainRMB.find { it.symbol == WaltType.ETH.name }
            if (item != null) {
                ethPrice = if (TextUtils.isEmpty(item.price_cny)) "0" else item.price_cny
            }
        }
        val eth = limit.toBigInteger().times(p.toBigInteger())
        var str = Convert.fromWei(eth.toString(), Convert.Unit.GWEI).toString()
        val rmb = str.toDouble().times(ethPrice.toDouble())
        tv_btc.text = "$str ${if (wallet!!.unit == WaltType.ETC.name) WaltType.ETC.name else WaltType.ETH.name}≈${Utils.toSubStringDegistForChart(
                rmb,
                Constant.rmbP,
                true
        ) + Constant.unit}"
        tv_wei.text = "$p Gwei"
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

    @SuppressLint("CheckResult")
    fun getFee(isfirst: Boolean) {
        TokenSendService().getfee(if (wallet!!.unit == WaltType.ETC.name) WaltType.ETC else WaltType.ETH)
                .compose(bindToLifecycle())
                .subscribe({
                    if (it.code == 1) {
                        var p = Convert.fromWei(it.data!!.bestGasPrice, Convert.Unit.GWEI).toInt()
                        if (p < 1) {
                            p = 1
                        }
                        tv_wei2.text = resources.getString(R.string.fee_recommend) + p + " Gwei"
                        if (isfirst) {
//                            var s = Convert.fromWei(it.data!!.minGasPrice, Convert.Unit.GWEI).toInt()
                            var s = p
                            when {
                                s < min -> {
                                    sb_fee.progress = 0
                                }
                                s > 199 -> {
                                    sb_fee.max = s - min
                                    sb_fee.progress = s - min
                                }
                                else -> {
                                    sb_fee.progress = s - min
                                }
                            }
                        }

                    }
                }, {

                })
    }

    fun setback(w: WInfo) {
        if (w.unit == WaltType.ETH.name) {
            tv_rmb_price.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(et_amount.text)) {
                var rmb = Coin.parseCoin(et_amount.text.toString()).toPlainString().toDouble().times(w.rmb)
                tv_rmb_price.text = "≈${Utils.toSubStringDegistForChart(
                        rmb,
                        Constant.rmbP,
                        true
                ) + Constant.unit}"
            }
        } else {
            tv_rmb_price.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(et_address.text) && !TextUtils.isEmpty(et_amount.text)) {
            btn_sure.isEnabled = true
            btn_sure.setBackgroundResource(R.drawable.btn_5f8def_selector)
        } else {
            btn_sure.isEnabled = false
            btn_sure.setBackgroundResource(R.drawable.btn_cccccc_selector)
        }
    }

    var fee = BigInteger.ZERO
    @SuppressLint("NewApi", "CheckResult")
    fun create(to: String, from: String, amount1: String, remark: String, wallet: WInfo) {
        if (TextUtils.isEmpty(to)) {
            toast(resources.getString(R.string.send_not_to_hint))
            return
        }
        if (!CoinUtils.checkAddress(WaltType.ETH, to)) {
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
        if (amount1.toDouble() < 0.0001) {
            toast(resources.getString(R.string.send_amout_hint) + "(≥0.0001)")
            return
        }

        fee = limit.toBigInteger().times((sb_fee.progress + min).toBigInteger())
        var fees = Convert.fromWei(fee.toString(), Convert.Unit.GWEI).toString()
        showProgressDialog("")
        when (wallet.unit) {
            WaltType.ETC.name -> {
                TokenSendService().nonce(wallet.address, WaltType.ETC)
                        .compose(bindToLifecycle())
                        .subscribe({
                            dismissProgressDialog()
                            if (it.code == 1) {
                                var data = it.data
                                wallet.balance = data!!.balance
                                var nonce = data.nonce + 1
                                MyWalletUtils.instance.updateCheckInfo(wallet)
                                if (wallet.balance.toDouble() < amount1.toDouble().plus(fees.toDouble())) {
                                    toast(resources.getString(R.string.send_amout_not_balance))
                                    return@subscribe
                                }
                                show(resources.getString(R.string.send_type), to, from, amount1, remark, wallet, nonce)
                            } else {
                                toast(it.msg!!)
                            }
                        }, {
                            Error.error(it, this@SendETHActivity)
                        })
            }
            else -> {
                TokenSendService().nonce(wallet.address, WaltType.ETH)
                        .compose(bindToLifecycle())
                        .subscribe({
                            if (it.code == 1) {
                                dismissProgressDialog()
                                var data = it.data
                                var nonce = data!!.nonce + 1
                                if (wallet.unit == WaltType.ETH.name) {
                                    wallet.balance = data.balance
                                    MyWalletUtils.instance.updateCheckInfo(wallet)
                                    if (wallet.balance.toDouble() < amount1.toDouble().plus(fees.toDouble())) {
                                        toast(resources.getString(R.string.send_amout_not_balance))
                                        return@subscribe
                                    }
                                    show(resources.getString(R.string.send_type), to, from, amount1, remark, wallet, nonce)
                                } else {
                                    if (data.balance.toDouble() < fees.toDouble()) {
                                        toast(resources.getString(R.string.send_amout_not_balance1))
                                    } else {
                                        showProgressDialog("")
                                        TokenSendService().tokenbalance(wallet.address, WaltType.ETH)
                                                .compose(bindToLifecycle())
                                                .subscribe({
                                                    dismissProgressDialog()
                                                    if (it.code == 1) {
                                                        it.data?.forEach {
                                                            if (wallet.unit == it.tokenInfo.s && wallet.contract_address == it.tokenInfo.h) {
                                                                val tokenDecimal = BigDecimal.TEN.pow(wallet.decimal)
                                                                val bigDecimal = BigDecimal(it.balance).divide(tokenDecimal)
                                                                wallet.balance = bigDecimal.toString()
                                                            }
                                                        }
                                                        MyWalletUtils.instance.updateCheckInfo(wallet)
                                                    }
                                                    if (wallet.balance.toDouble() < amount1.toDouble()) {
                                                        toast(resources.getString(R.string.send_amout_not_balance))
                                                    } else {
                                                        show(resources.getString(R.string.send_type), to, from, amount1, remark, wallet, nonce)
                                                    }
                                                }, {
                                                    Error.error(it, this@SendETHActivity)
                                                })
                                    }
                                }
                            } else {
                                toast(it.msg!!)
                            }
                        }, {
                            Error.error(it, this@SendETHActivity)
                        })
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
            if (requestCode == 2) {
                if (data != null) {
                    runOnUiThread {
                        n = data.getIntExtra("nonce", 0)
                        limit = data.getIntExtra("gaslimit", 20000)
                        var gas = data.getIntExtra("gas", 10)
                        if (gas > 200) {
                            sb_fee.max = gas - min
                            sb_fee.progress = gas - min
                        } else {
                            sb_fee.progress = gas - min
                        }
                        setPrice(sb_fee.progress + min)
                    }
                }
            }
            if (requestCode == 100) {
                runOnUiThread {
                    et_address.setText(data?.getStringExtra("sacanurl"))
                }
            }
        }
    }

    fun show(s1: String, s2: String, s3: String, s4: String, remark: String, wallet: WInfo, nonce: Int) {
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
                show2(wallet, s4, s2, remark, nonce)
                dismiss()
            }
            show()
        }
    }

    fun show2(wallet: WInfo, price: String, to: String, memo: String, nonce: Int) {
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
                Flowable.just("")
                        .map {
                            if (wallet.keystone.startsWith("{")) {
                                val walletfile = KeyStoneUtil.getWalletFile(wallet.keystone)
                                Credentials.create(Wallet.decrypt(pwd.text.toString(), walletfile))
                            } else {
                                var keyHax = AesUtils.decrypt(wallet.keystone, pwd.text.toString())
                                Credentials.create(keyHax)
                            }

                        }
                        .flatMap {
                            var sign = when (wallet.unit) {
                                WaltType.ETH.name, WaltType.ETC.name -> {
                                    ETHUtils().sign(to, price.toDouble(), it, this@SendETHActivity.sb_fee.progress + min,
                                            memo, limit.toBigInteger(), if (n > 0) n.toBigInteger() else nonce.toBigInteger())
                                }
                                else -> {
                                    ETHUtils().signERC(to, price.toDouble(), it, this@SendETHActivity.sb_fee.progress + min,
                                            wallet.contract_address, wallet.decimal, limit.toBigInteger(), if (n > 0) n.toBigInteger() else nonce.toBigInteger())
                                }
                            }
                            TokenSendService().send(sign, if (WaltType.ETC.name == wallet.unit) WaltType.ETC else WaltType.ETH)
                        }
                        .compose(bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            dismissProgressDialog()
                            if (TextUtils.isEmpty(it.result)) {
                                if (it.error != null) {
                                    toast(it.error!!.message)
                                }
                            } else {
                                val intent = Intent(this@SendETHActivity, SuccessActivity::class.java)
                                val tx1 = TxItem(
                                        "0",
                                        it.result.toLowerCase(),
                                        wallet.address,
                                        to,
                                        price,
                                        wallet.unit,
                                        this@SendETHActivity.et_remark.text.toString(),
                                        System.currentTimeMillis(),
                                        Constant.main
                                )
                                if (wallet.unit == WaltType.ETH.name || WaltType.ETC.name == wallet.unit)
                                    tx1.saveOrUpdateAsync("Hash = ? and denom = ?", it.result.toLowerCase(), wallet.unit)
                                            .listen {
                                                intent.putExtra("data3", wallet)
                                                intent.putExtra("tx", tx1)
                                                startActivity(intent)
                                                dismiss()
                                                finish()
                                            }
                                else
                                    tx1.saveOrUpdateAsync("Hash = ? and token = ?", it.result.toLowerCase(), wallet.contract_address)
                                            .listen {
                                                intent.putExtra("data3", wallet)
                                                intent.putExtra("tx", tx1)
                                                startActivity(intent)
                                                dismiss()
                                                finish()
                                            }
                            }
                        }, {
                            Error.error(it, this@SendETHActivity)
                        })

            }
            show()
        }
    }

}