package com.yjy.wallet.activity.htdf

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
import com.google.gson.GsonBuilder
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.AesUtils
import com.weiyu.baselib.widget.DecimalInputTextWatcher
import com.weiyu.baselib.widget.NameLengthFilter
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.AddrListActivity
import com.yjy.wallet.activity.ScanActivity
import com.yjy.wallet.api.HTDFService
import com.yjy.wallet.api.TxService
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.bean.htdf.Send
import com.yjy.wallet.bean.params.BroadCast
import com.yjy.wallet.chainutils.htdf.HtdfSign
import com.yjy.wallet.chainutils.htdf.HtdfTransaction
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
import kotlinx.android.synthetic.main.activity_send_htdf.*
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Utils
import org.json.JSONObject
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.util.regex.Pattern

/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class SendHtdfActivity : BaseActivity() {

    override fun getContentLayoutResId(): Int = R.layout.activity_send_htdf

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initializeContentViews() {
        title_tb.setLeftLayoutClickListener(View.OnClickListener { finish() })
        title_tb.setRightLayoutClickListener(View.OnClickListener {
            if (RxPermissions(this).isGranted(Manifest.permission.CAMERA)) {
                val intent = Intent(this@SendHtdfActivity, ScanActivity::class.java)
                startActivityForResult(intent, 0x01)
            } else {
                RxPermissions(this).request(Manifest.permission.CAMERA)
                        .subscribe {
                            if (it) {
                                val intent = Intent(this@SendHtdfActivity, ScanActivity::class.java)
                                startActivityForResult(intent, 0x01)
                            } else {
                                toast(resources.getString(R.string.request_permissions))
                            }
                        }
            }
        })
        var wallet = intent.getSerializableExtra("data") as WInfo
        if (wallet.unit == WaltType.htdf.name) {
            tv_gas1.visibility = View.VISIBLE
            ll_fee.visibility = View.GONE
        } else {
            tv_gas1.visibility = View.GONE
            ll_fee.visibility = View.VISIBLE
            tv_gas2.text = String.format(resources.getString(R.string.commission_node_hint21), "0.09")
            sb_fee.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    var gas = (progress + 3).times(3000000).toDouble().div(100000000)
                    var gasStr = com.weiyu.baselib.util.Utils.toSubStringDegistForChart(gas, Constant.priceP, false)
                    tv_gas2.text = String.format(resources.getString(R.string.commission_node_hint21), gasStr)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            sb_fee.max = 8
            sb_fee.progress = 1
        }
        rl_getaddr.setOnClickListener {
            var intent = Intent(this@SendHtdfActivity, AddrListActivity::class.java)
            intent.putExtra("type", 0)
            intent.putExtra("unit", WaltType.htdf)
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
                setback(wallet)
            }

        }
        et_address.addTextChangedListener(t)
        if (wallet.unit == WaltType.NEO.name) {
            et_amount.inputType = InputType.TYPE_CLASS_NUMBER
        }
        var d = Constant.priceP
        if (wallet.unit != WaltType.htdf.name) {
            d = if (wallet.decimal <= 4) {
                wallet.decimal
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
                            setback(wallet)
                        })
        )
        et_remark.addTextChangedListener(t)

        tv_price.text =
                resources.getString(R.string.send_price) + com.weiyu.baselib.util.Utils.toSubStringDegistForChart(
                        wallet.balance!!.toDouble(),
                        Constant.priceP,
                        false
                ) + " " + wallet.unit.toUpperCase()
        btn_sure.setOnClickListener {
            if (com.weiyu.baselib.update.SysUtils.getNetWorkState(this@SendHtdfActivity) == -1) {
                toast(resources.getString(R.string.not_net))
            } else {
                create(
                        et_address.text.toString().trim(),
                        et_amount.text.toString(),
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
            var rmb = BigDecimal(et_amount.text.toString()).times(w.rmb.toBigDecimal())
            tv_rmb_price.text = "≈${com.weiyu.baselib.util.Utils.toSubStringDegistForChartStr(
                    rmb.toString(),
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

    @SuppressLint("NewApi", "CheckResult")
    fun create(to: String, amount1: String, wallet: WInfo) {
        if (TextUtils.isEmpty(to)) {
            toast(resources.getString(R.string.send_not_to_hint))
            return
        }
        if (!CoinUtils.checkAddress(WaltType.htdf, to)) {
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
        if (amount1.toDouble() < 0.0001) {
            toast(resources.getString(R.string.send_amout_hint) + "(≥0.0001)")
            return
        }
        showProgressDialog("")
        TxService(WaltType.htdf.name).getAccount(wallet.address)
                .compose(bindToLifecycle())
                .subscribe({
                    dismissProgressDialog()
                    var data = it.data
                    if (it.err_code == 200 && data != null) {
                        if (!data.value.coins.isNullOrEmpty()) {
                            var amount = 0.0
                            for (b in data.value.coins!!) {
                                amount = amount.plus(b.amount.toDouble())
                            }
                            wallet.sequence = data.value.sequence
                            wallet.account_number = data.value.account_number
                            if (wallet.unit == WaltType.htdf.name) {
                                wallet.balance = amount.toString()
                                MyWalletUtils.instance.updateCheckInfo(wallet)
                                if ((amount1.toDouble() * 100000000 + 3000000) <= (wallet.balance.toDouble() * 100000000)) {
                                    show(resources.getString(R.string.send_type), to, amount1, wallet)
                                } else {
                                    toast(resources.getString(R.string.send_amout_not_balance))
                                }
                            } else {
                                if ((this@SendHtdfActivity.sb_fee.progress + 3) * 3000000 <= amount * 100000000) {
                                    showProgressDialog("")
                                    HTDFService().tokenBalance(wallet.contract_address, wallet.address)
                                            .compose(bindToLifecycle())
                                            .subscribe({
                                                dismissProgressDialog()
                                                var price = BigInteger(it.replace("\"", ""), 16)
                                                val tokenDecimal = BigDecimal.TEN.pow(wallet.decimal)
                                                val bigDecimal = BigDecimal(price).divide(tokenDecimal)
                                                wallet.balance = bigDecimal.toString()
                                                MyWalletUtils.instance.updateCheckInfo(wallet)
                                                if (amount1.toDouble() <= wallet.balance.toDouble()) {
                                                    show(resources.getString(R.string.send_type), to, amount1, wallet)
                                                } else {
                                                    toast(resources.getString(R.string.send_amout_not_balance))
                                                }
                                            }, {
                                                dismissProgressDialog()
                                                Error.error(it, this@SendHtdfActivity)
                                            })
                                } else {
                                    toast(resources.getString(R.string.send_amout_not_balance2))
                                }
                            }
                        } else {
                            toast(resources.getString(R.string.recharge_hint_no_price))
                        }
                    } else {
                        if (wallet.unit == WaltType.htdf.name) {
                            toast(resources.getString(R.string.recharge_hint_no_price))
                        } else {
                            toast(resources.getString(R.string.send_amout_not_balance2))
                        }
                    }
                }, {
                    dismissProgressDialog()
                    Error.error(it, this@SendHtdfActivity)
                })
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

    fun show(s1: String, s3: String, s4: String, wallet: WInfo) {
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
            findViewById<TextView>(R.id.tv_m2).text = s3
            findViewById<TextView>(R.id.tv_m3).text = wallet.address
            findViewById<TextView>(R.id.tv_m4).text = s4 + wallet.unit.toUpperCase()
            findViewById<TextView>(R.id.btn_sure).setOnClickListener {
                show2(wallet, s4, s3)
                dismiss()
            }
            show()
        }
    }

    fun show2(wallet: WInfo, price: String, to: String) {
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
                val notSign = if (wallet.unit == WaltType.htdf.name)
                    HtdfSign.getNotSignTransaction(
                            wallet.address,
                            to,
                            price,
                            "satoshi",
                            this@SendHtdfActivity.et_remark.text.toString(),
                            Constant.htdffeeLimit, 30000
                            , "")
                else
                    HtdfSign.getNotSignTransaction(
                            wallet.address,
                            wallet.contract_address,
                            "0",
                            "satoshi",
                            this@SendHtdfActivity.et_remark.text.toString(),
                            Constant.htdffeeLimit, ((this@SendHtdfActivity.sb_fee.progress + 3) * 30000).toLong()//erc gaswanted必须大于62064才能通过验证
                            , "")

                var sign: HtdfTransaction? = null
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
                            sign = if (wallet.unit == WaltType.htdf.name) HtdfSign.getSignTransaction(wallet, it, notSign)
                            else HtdfSign.contract(wallet, it, notSign, to, price.toDouble())
                            val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().create()
                            Utils.HEX.encode(gson.toJson(sign).toByteArray())
                        }
                        .flatMap { HTDFService().broadcast(BroadCast(it)) }
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
                                    var txitem = TxItem(
                                            t.height,
                                            t.txhash.toLowerCase(),
                                            wallet.address,
                                            to,
                                            price,
                                            wallet.unit,
                                            this@SendHtdfActivity.et_remark.text.toString(),
                                            System.currentTimeMillis(),
                                            Constant.main
                                    )
                                    if (wallet.unit == WaltType.htdf.name) {
                                        txitem.saveOrUpdateAsync("Hash = ? and denom = ?", t.txhash.toLowerCase(), wallet.unit)
                                                .listen {
                                                    val intent = Intent(this@SendHtdfActivity, HTDFSuccessActivity::class.java)
                                                    intent.putExtra("data1", txitem)
                                                    startActivity(intent)
                                                    dismiss()
                                                    finish()
                                                }
                                    } else {
                                        txitem.saveOrUpdateAsync("Hash = ? and token = ?", t.txhash.toLowerCase(), wallet.contract_address)
                                                .listen {
                                                    val intent = Intent(this@SendHtdfActivity, HTDFSuccessActivity::class.java)
                                                    intent.putExtra("data1", txitem)
                                                    startActivity(intent)
                                                    dismiss()
                                                    finish()
                                                }
                                    }
                                }
                            }

                            override fun onError(e: Throwable) {
                                Error.error(e, this@SendHtdfActivity)
                            }
                        })
            }
            show()
        }
    }

}