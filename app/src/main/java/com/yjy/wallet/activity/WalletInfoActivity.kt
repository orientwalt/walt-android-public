package com.yjy.wallet.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.AesUtils
import com.weiyu.baselib.util.MD5Util
import com.weiyu.baselib.util.StringUtils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.bean.UpdateW
import com.yjy.wallet.chainutils.dash.DashUtils
import com.yjy.wallet.chainutils.ltc.LTCUtils
import com.yjy.wallet.chainutils.neo.ECKeyPair
import com.yjy.wallet.wallet.*
import com.zhl.cbdialog.CBDialogBuilder
import de.greenrobot.event.EventBus
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_wallet_info.*
import org.bitcoinj.core.Bech32
import org.bitcoinj.core.ECKey
import org.stellar.sdk.KeyPair
import org.web3j.crypto.Wallet
import org.web3j.utils.Numeric

/**
 *Created by weiweiyu
 *on 2019/5/6
 */
class WalletInfoActivity : BaseActivity() {
    var CODE_PWD = 102
    lateinit var yWallet: YWallet
    override fun getContentLayoutResId(): Int = R.layout.activity_wallet_info

    override fun initializeContentViews() {
        EventBus.getDefault().register(this)
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        yWallet = intent.getSerializableExtra("data") as YWallet

        tv_name.text = yWallet.remark
        btn_delete.setOnClickListener {
            show()
        }
    }

    fun onEvent(yWallet: YWallet) {
        this.yWallet = yWallet
        if (TextUtils.isEmpty(yWallet.words)) {
            rl_words.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (TextUtils.isEmpty(yWallet.words)) {
            rl_words.visibility = View.GONE
        }
    }

    fun item(v: View) {
        when (v.id) {
            R.id.rl_name -> {
                show(yWallet)
            }
            R.id.rl_key -> {
                if (yWallet.map.isNotEmpty()) {
                    when {
                        yWallet.wType == 3 -> show2(yWallet.map["BTC"], 0)
                        yWallet.wType == 2 -> show2(yWallet.map["ETH"], 0)
                        yWallet.map.size == 1 -> yWallet.map.forEach {
                            show2(it.value, 0)
                        }
                        else -> show2(0)
                    }

                }
            }
            R.id.rl_keystore -> {
                if (yWallet.map.isNotEmpty()) {
                    when {
                        yWallet.wType == 3 -> show2(yWallet.map["BTC"], 2)
                        yWallet.wType == 2 -> show2(yWallet.map["ETH"], 2)
                        yWallet.map.size == 1 -> yWallet.map.forEach {
                            show2(it.value, 2)
                        }
                        else -> show2(2)
                    }
                }
            }
            R.id.rl_words -> {
                show2(null, 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CODE_PWD -> yWallet = data?.getSerializableExtra("data") as YWallet
            }
        }
    }

    fun show2(type: Int) {
        val dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 0.8f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                .setView(R.layout.dialog_pwd)
                .create()
        with(dialog) {
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            val pwd = findViewById<EditText>(R.id.et_pwd)
            this@WalletInfoActivity.setEdittxtForHw(pwd)
//            if (ModelUtils.isEMUI() && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//                pwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
//            }
            findViewById<RelativeLayout>(R.id.right_layout).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_close).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_sure).setOnClickListener {
                if (TextUtils.isEmpty(pwd.text)) {
                    toast(resources.getString(R.string.send_input_pwd))
                    return@setOnClickListener
                }
                if (!StringUtils.isPwd(pwd.text.toString())) {
                    toast(resources.getString(R.string.create_pwd_hint))
                    return@setOnClickListener
                }
                if (yWallet.pwd != MD5Util.getMD5String(pwd.text.toString().trim())) {
                    toast(resources.getString(R.string.send_err_pwd))
                    return@setOnClickListener
                }
                var intent = Intent(this@WalletInfoActivity, CoinActivity::class.java)
                intent.putExtra("data", Gson().toJson(yWallet.map))
                intent.putExtra("pwd", pwd.text.toString().trim())
                intent.putExtra("type", type)
                startActivity(intent)
                dismiss()
            }
            show()
        }
    }

    fun show2(wallet: WInfo?, type: Int) {
        val dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 0.8f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                .setView(R.layout.dialog_pwd)
                .create()
        with(dialog) {
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            val pwd = findViewById<EditText>(R.id.et_pwd)
            this@WalletInfoActivity.setEdittxtForHw(pwd)
            findViewById<RelativeLayout>(R.id.right_layout).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_close).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_sure).setOnClickListener {
                if (TextUtils.isEmpty(pwd.text)) {
                    toast(resources.getString(R.string.send_input_pwd))
                    return@setOnClickListener
                }
                if (MD5Util.getMD5String(pwd.text.toString()) != yWallet.pwd) {
                    toast(resources.getString(R.string.send_err_pwd))
                    return@setOnClickListener
                }
                showProgressDialog("")
                when (type) {
                    0 -> {
                        Flowable.just("")
                                .map {
                                    val ecKey = if (wallet!!.keystone.startsWith("{")) {
                                        var walletfile = KeyStoneUtil.getWalletFile(wallet.keystone)
                                        var credentials = AllCredentials.create(Wallet.decrypt(pwd.text.toString(), walletfile))
                                        ECKey.fromPrivate(credentials.ecKeyPair.privateKey)
                                    } else {
                                        var keyHax = AesUtils.decrypt(wallet.keystone, pwd.text.toString())
                                        ECKey.fromPrivate(Numeric.hexStringToByteArray(keyHax))
                                    }
                                    when (wallet.unit) {
                                        WaltType.BTC.name, WaltType.BSV.name, WaltType.BCH.name, WaltType.USDT.name
                                            , WaltType.CXC.name, WaltType.QTUM.name
                                        -> {
                                            ecKey.getPrivateKeyAsWiF(Constant().currentNetworkParams)
                                        }
                                        WaltType.NEO.name -> ECKeyPair.create(ecKey.privKey).exportAsWIF()
                                        WaltType.ETH.name -> "0x${ecKey.privateKeyAsHex}"
                                        WaltType.LTC.name -> LTCUtils.getKey(ecKey.privKeyBytes)
                                        WaltType.DASH.name -> DashUtils.getKey(ecKey.privKeyBytes)
                                        WaltType.XLM.name -> {
                                            var c = KeyPair.fromSecretSeed(ecKey.privKeyBytes).secretSeed
                                            var s = StringBuffer()
                                            c.forEach { s.append(it.toUpperCase()) }
                                            s.toString()
                                        }
                                        else -> ecKey.privateKeyAsHex
                                    }
                                }
                                .compose(bindToLifecycle())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : ResourceSubscriber<String>() {
                                    override fun onComplete() {
                                    }

                                    override fun onNext(t: String?) {
                                        dismissProgressDialog()
                                        t ?: return
                                        keystrore2(t)
                                        dismiss()
                                    }

                                    override fun onError(t: Throwable?) {
                                        toast(resources.getString(R.string.send_err_pwd))
                                        dismissProgressDialog()
                                    }
                                })


//                        show(ecKey.privateKeyAsHex)
                    }
                    1 -> {
                        Flowable.just("")
                                .compose(bindToLifecycle())
                                .map { AesUtils.decrypt(yWallet.words, pwd.text.toString()) }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : ResourceSubscriber<String>() {
                                    override fun onComplete() {
                                    }

                                    override fun onNext(t: String?) {
                                        dismissProgressDialog()
                                        t ?: return
                                        val intent = Intent(this@WalletInfoActivity, ExportWordsActivity::class.java)
                                        intent.putExtra("words", t)
                                        var ylist: MutableList<YWallet> = arrayListOf(yWallet)
                                        intent.putExtra("data", Gson().toJson(ylist))
                                        startActivity(intent)
                                        dismiss()
                                    }

                                    override fun onError(t: Throwable?) {
                                        dismissProgressDialog()
                                    }
                                })

                    }
                    2 -> {
                        if (wallet!!.unit == "htdf" || wallet.unit == "usdp") {
                            Flowable.just("")
                                    .compose(bindToLifecycle())
                                    .map {
                                        if (wallet.keystone.startsWith("{")) {
                                            var walletfile = KeyStoneUtil.getWalletFile(wallet.keystone)
                                            var toData = Bech32.decode(walletfile!!.address)
                                            if (toData.hrp != wallet.unit) {//第一版导出的是一个Keystore的地址统一是usdp
                                                val credentials =
                                                        AllCredentials.create(Wallet.decrypt(pwd.text.toString(), walletfile))
                                                val ecKey = ECKey.fromPrivate(credentials.ecKeyPair.privateKey)
                                                val payloadBytes2 =
                                                        BitcoinCashBitArrayConverter.convertBits(ecKey.pubKeyHash, 8, 5, true)
                                                val address = Bech32.encode(wallet!!.unit, payloadBytes2)
                                                walletfile.address = address
                                                Gson().toJson(walletfile)
                                            } else {
                                                wallet.keystone
                                            }
                                        } else {
                                            var keyHax = AesUtils.decrypt(wallet.keystone, pwd.text.toString())
                                            val address = wallet.address
                                            var walletfile = KeyStoneUtil.createWalletFile(pwd.text.toString(), org.web3j.crypto.ECKeyPair.create(Numeric.hexStringToByteArray(keyHax)), false, address)
                                            Gson().toJson(walletfile)
                                        }
                                    }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(object : ResourceSubscriber<String>() {
                                        override fun onComplete() {
                                        }

                                        override fun onNext(t: String?) {
                                            dismissProgressDialog()
                                            t ?: return
                                            keystrore(t)
                                            dismiss()
                                        }

                                        override fun onError(t: Throwable?) {
                                            toast(resources.getString(R.string.send_err_pwd))
                                            dismissProgressDialog()
                                        }
                                    })

                        } else {
                            if (wallet.keystone.startsWith("{")) {
                                dismissProgressDialog()
                                keystrore(wallet.keystone)
                                dismiss()
                            } else {
                                Flowable.just("")
                                        .compose(bindToLifecycle())
                                        .map {
                                            var keyHax = AesUtils.decrypt(wallet.keystone, pwd.text.toString())
                                            val address = wallet.address
                                            var walletfile = KeyStoneUtil.createWalletFile(pwd.text.toString(), org.web3j.crypto.ECKeyPair.create(Numeric.hexStringToByteArray(keyHax)), false, address)
                                            Gson().toJson(walletfile)
                                        }
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(object : ResourceSubscriber<String>() {
                                            override fun onComplete() {
                                            }

                                            override fun onNext(t: String) {
                                                dismissProgressDialog()
                                                keystrore(t)
                                                dismiss()
                                            }

                                            override fun onError(t: Throwable?) {
                                                dismissProgressDialog()
                                            }
                                        })

                            }

                        }
                    }
                }

            }
            show()
        }
    }

    fun show() {
        val dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 0.8f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                .setView(R.layout.dialog_pwd)
                .create()
        with(dialog) {
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            val pwd = findViewById<EditText>(R.id.et_pwd)
            this@WalletInfoActivity.setEdittxtForHw(pwd)
            findViewById<RelativeLayout>(R.id.right_layout).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_close).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_sure).setOnClickListener {
                if (TextUtils.isEmpty(pwd.text)) {
                    toast(resources.getString(R.string.send_input_pwd))
                    return@setOnClickListener
                }
                if (yWallet.pwd != MD5Util.getMD5String(pwd.text.toString().trim())) {
                    toast(resources.getString(R.string.send_err_pwd))
                    return@setOnClickListener
                }
                MyWalletUtils.instance.delete(yWallet)
                EventBus.getDefault().post(UpdateW())
                dismiss()
                finish()
            }
            show()
        }
    }

    fun keystrore(key: String) {
        var intent = Intent(this, ExportKeyActivity::class.java)
        intent.putExtra("key", key)
        startActivity(intent)
    }

    fun keystrore2(key: String) {
        var intent = Intent(this, ExportKeyActivity2::class.java)
        intent.putExtra("key", key)
        startActivity(intent)
    }

    fun show(yWallet: YWallet) {
        var dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 1f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_BOTTOM)
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setView(R.layout.dialog_update_name)
                .create()
        with(dialog) {
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            var et = findViewById<EditText>(R.id.et_pwd)
//            this@WalletInfoActivity.setEditTextInhibitInputSpace(et, Constant.NAME_SIZE)
            findViewById<TextView>(R.id.btn_sure).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_sure2).setOnClickListener {
                if (TextUtils.isEmpty(et.text)) {
                    toast(resources.getString(R.string.create_toast_remark))
                    return@setOnClickListener
                }
                MyWalletUtils.instance.getWallet()?.forEach {
                    if (it.remark.equals(et.text.toString())) {
                        toast(resources.getString(R.string.create_toast_hasremark))
                        return@setOnClickListener
                    }
                }
                MyWalletUtils.instance.updateName(yWallet, et.text.toString())
                yWallet.remark = et.text.toString()
                this@WalletInfoActivity.tv_name.text = yWallet.remark
                dismiss()
            }
            show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}