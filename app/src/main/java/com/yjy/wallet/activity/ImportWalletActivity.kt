package com.yjy.wallet.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import com.androidkun.xtablayout.XTabLayout
import com.google.common.base.Splitter
import com.ripple.crypto.ecdsa.Seed
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.MD5Util
import com.weiyu.baselib.util.StringUtils
import com.yjy.wallet.Constant
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.R
import com.yjy.wallet.adapter.PageAdapter
import com.yjy.wallet.bean.UpdateW
import com.yjy.wallet.chainutils.dash.DashUtils
import com.yjy.wallet.chainutils.ltc.LTCUtils
import com.yjy.wallet.chainutils.neo.NEOSign
import com.yjy.wallet.chainutils.xlm.StrKey
import com.yjy.wallet.ui.fragment.ImportFragment
import com.yjy.wallet.ui.fragment.ImportKeyFragment
import com.yjy.wallet.ui.fragment.ImportWordsFragment
import com.yjy.wallet.utils.CoinUtils
import com.yjy.wallet.utils.YJYWalletUtils
import com.yjy.wallet.wallet.*
import com.yjy.wallet.wordlist.Chinese_simplified
import com.yjy.wallet.wordlist.English
import de.greenrobot.event.EventBus
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_import_wallet.*
import org.bitcoinj.core.DumpedPrivateKey
import org.stellar.sdk.KeyPair
import org.web3j.crypto.CipherException
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.utils.Numeric
import java.io.IOException

/**
 *Created by weiweiyu
 *on 2019/4/29
 *
 * 导入钱包
 */
class ImportWalletActivity : BaseActivity() {
    var fragments: MutableList<Fragment> = arrayListOf()
    override fun getContentLayoutResId(): Int = R.layout.activity_import_wallet
    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tb_title.setRightLayoutClickListener(View.OnClickListener {
            if (RxPermissions(this).isGranted(Manifest.permission.CAMERA)) {
                val intent = Intent(this@ImportWalletActivity, ScanActivity::class.java)
                startActivityForResult(intent, 0x01)
            } else {
                RxPermissions(this).request(Manifest.permission.CAMERA)
                        .subscribe {
                            if (it) {
                                val intent = Intent(this@ImportWalletActivity, ScanActivity::class.java)
                                startActivityForResult(intent, 0x01)
                            } else {
                                toast(resources.getString(R.string.request_permissions))
                            }
                        }
            }
        })
        val tebTitle = resources.getStringArray(R.array.import_tab)
        tl_import.addOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabSelected(tab: XTabLayout.Tab?) {
                if (tl_import.selectedTabPosition != 0) {
                    tb_title.setRightLayoutVisibility(View.VISIBLE)
                } else {
                    tb_title.setRightLayoutVisibility(View.INVISIBLE)
                }
                val titles = arrayOf(
                        resources.getString(R.string.what_words),
                        resources.getString(R.string.what_key),
                        resources.getString(R.string.what_privacy)
                )
                tv_what.text = titles[tl_import.selectedTabPosition]
            }
        })
        fragments.add(ImportWordsFragment())
        fragments.add(ImportFragment())
        fragments.add(ImportKeyFragment())
        vp_content.offscreenPageLimit = tebTitle.size
        vp_content.adapter = PageAdapter(supportFragmentManager, fragments, tebTitle)
        tl_import.setupWithViewPager(vp_content)
        tv_what.setOnClickListener {
            val intent = Intent(this, WhatActivity::class.java)
            val type = when (tl_import.selectedTabPosition) {
                0 -> 1
                1 -> 2
                2 -> 0
                else -> 0
            }
            intent.putExtra("type", type)
            startActivity(intent)
        }
    }


    fun agree(v: View) {
        if (ll_agree.isSelected) {
            ll_agree.isSelected = false
            iv_check.setImageDrawable(resources.getDrawable(R.mipmap.weixuanze1zhuangtai))
        } else {
            ll_agree.isSelected = true
            iv_check.setImageDrawable(resources.getDrawable(R.mipmap.xuanzezhaungtai))
        }
    }

    fun import(v: View) {
        val fragment = fragments[tl_import.selectedTabPosition]
        when (tl_import.selectedTabPosition) {
            0 -> {
                (fragment as ImportWordsFragment).getList() ?: return
                val listword = Splitter.on(" ").splitToList(fragment.getList()).toMutableList()
                if (listword.size != 12) {
                    toast(resources.getString(R.string.import_words_toast2))
                    return
                }
                var boolean = listword.subtract(English.words.toMutableList()).isEmpty()
                if (!boolean) {
                    boolean = listword.subtract(Chinese_simplified.words.toMutableList()).isEmpty()
                }
                if (!boolean) {
                    toast(resources.getString(R.string.import_words_toast))
                    return
                }
                if (!TextUtils.equals(fragment.getPwd(), fragment.getPwd2())) {
                    toast(resources.getString(R.string.create_toast_no_equals))
                    return
                }
                if (TextUtils.isEmpty(fragment.getRemark())) {
                    toast(resources.getString(R.string.create_toast_remark))
                    return
                }
                MyWalletUtils.instance.getWallet()?.forEach {
                    if (it.remark.equals(fragment.getRemark())) {
                        toast(resources.getString(R.string.create_toast_hasremark))
                        return
                    }
                }
                if (TextUtils.isEmpty(fragment.getPwd())) {
                    toast(resources.getString(R.string.create_toast_pwd))
                    return
                }
                if (!StringUtils.isPwd(fragment.getPwd())) {
                    toast(resources.getString(R.string.create_pwd_hint))
                    return
                }
                if (!ll_agree.isSelected) {
                    toast(resources.getString(R.string.create_toast_agree))
                    return
                }
                showProgressDialog(resources.getString(R.string.import_ing))
                val wallet = YJYWalletUtils.importWallet(fragment.getList()!!)
                Flowable.just(wallet)
                        .compose(bindToLifecycle())
                        .map {
                            val pwd = fragment.getPwd()
                            val remark = fragment.getRemark()
                            //创建钱包
                            val yWallet = YWallet(MD5Util.getMD5String(pwd), remark, 100)
//                            val words = AesUtils.encrypt(fragment.getList()!!, pwd)//创建先保存助记词
//                            yWallet.words = words
                            WaltType.values().forEach { type ->
                                var eth = YJYWalletUtils.getWaltByWallet(it, type, pwd, 0)
                                if ((type == WaltType.htdf || type == WaltType.usdp) && fragment.is2 == 1) {
                                    eth = YJYWalletUtils.getWaltByWallet(it, type, pwd, 0, true)
                                }
                                eth.show = true
                                if (type != WaltType.USDT) {
                                    yWallet.map[type.name] = eth
                                } else {
                                    yWallet.wenMap[type.name] = eth
                                }
                            }
                            yWallet
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : ResourceSubscriber<YWallet>() {
                            override fun onComplete() {
                            }

                            override fun onNext(t: YWallet) {
                                btn_import.isEnabled = true
                                MyWalletUtils.instance.saveWallet(t)
                                MyWalletUtils.instance.update(t)
                                dismissProgressDialog()
                                startActivity(MainActivity::class.java)
                                EventBus.getDefault().post(UpdateW())
                            }

                            override fun onError(t: Throwable?) {
                                EventBus.getDefault().post(UpdateW())
                                btn_import.isEnabled = true
                                dismissProgressDialog()
                            }

                        })
            }
            1 -> {
                (fragment as ImportFragment)
                if (TextUtils.isEmpty(fragment.getText())) {
                    toast(resources.getString(R.string.create_toast_input_keystore))
                    return
                }
                if (TextUtils.isEmpty(fragment.getRemark())) {
                    toast(resources.getString(R.string.create_toast_remark))
                    return
                }
                MyWalletUtils.instance.getWallet()?.forEach {
                    if (it.remark.equals(fragment.getRemark())) {
                        toast(resources.getString(R.string.create_toast_hasremark))
                        return
                    }
                }
                if (TextUtils.isEmpty(fragment.getPwd())) {
                    toast(resources.getString(R.string.create_toast_pwd))
                    return
                }
                if (!StringUtils.isPwd(fragment.getPwd())) {
                    toast(resources.getString(R.string.create_pwd_hint))
                    return
                }
                var walletfile: WalletFile? = null
                try {
                    walletfile = KeyStoneUtil.getWalletFile(fragment.getText())
                } catch (e: IOException) {
                    toast(resources.getString(R.string.import_keystore_err))
                    btn_import.isEnabled = true
                    return
                }
                if (walletfile == null) {
                    toast(resources.getString(R.string.import_keystore_err))
                    btn_import.isEnabled = true
                    return
                }
                if (fragment.type == null) {
                    toast(resources.getString(R.string.coin_type_hint))
                    btn_import.isEnabled = true
                    return
                }
                val type = fragment.type ?: return
                if (!CoinUtils.checkAddress(type, walletfile.address)) {
                    toast(resources.getString(R.string.import_type))
                    btn_import.isEnabled = true
                    return
                }
                if (!ll_agree.isSelected) {
                    toast(resources.getString(R.string.create_toast_agree))
                    return
                }
                showProgressDialog(resources.getString(R.string.import_ing))
                Flowable.just("")
                        .compose(bindToLifecycle())
                        .map {
                            val remark = fragment.getRemark()
                            val pwd = fragment.getPwd()
                            val credentials = AllCredentials.create(Wallet.decrypt(pwd, walletfile))
                            val yWallet = YWallet(MD5Util.getMD5String(pwd), remark, type.ordinal)
                            val w = YJYWalletUtils.getWaltByKey(type,
                                    credentials.ecKeyPair.privateKey.toByteArray(),
                                    pwd)
                            w.show = true
                            yWallet.map[type.name] = w
                            if (type == WaltType.BTC) {
                                val w1 = YJYWalletUtils.getWaltByKey(WaltType.USDT, credentials.ecKeyPair.privateKey.toByteArray(), pwd)
                                w1.show = true
                                yWallet.map[WaltType.USDT.name] = w1
                            }
                            yWallet

                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : ResourceSubscriber<YWallet>() {
                            override fun onComplete() {
                            }

                            override fun onNext(t: YWallet) {
                                btn_import.isEnabled = true
                                dismissProgressDialog()
                                MyWalletUtils.instance.saveWallet(t)
                                MyWalletUtils.instance.update(t)
                                startActivity(MainActivity::class.java)
                                EventBus.getDefault().post(UpdateW())
                            }

                            override fun onError(t: Throwable?) {
                                btn_import.isEnabled = true
                                dismissProgressDialog()
                                if (t is CipherException) {
                                    toast(resources.getString(R.string.send_err_pwd))
                                }
                            }

                        })
            }
            2 -> {
                (fragment as ImportKeyFragment)
                if (TextUtils.isEmpty(fragment.getText())) {
                    toast(resources.getString(R.string.create_toast_input_key))
                    return
                }
                if (!TextUtils.equals(fragment.getPwd(), fragment.getPwd2())) {
                    toast(resources.getString(R.string.create_toast_no_equals))
                    return
                }
                if (TextUtils.isEmpty(fragment.getRemark())) {
                    toast(resources.getString(R.string.create_toast_remark))
                    return
                }
                MyWalletUtils.instance.getWallet()?.forEach {
                    if (it.remark.equals(fragment.getRemark())) {
                        toast(resources.getString(R.string.create_toast_hasremark))
                        return
                    }
                }
                if (TextUtils.isEmpty(fragment.getPwd())) {
                    toast(resources.getString(R.string.create_toast_pwd))
                    return
                }
                if (!StringUtils.isPwd(fragment.getPwd())) {
                    toast(resources.getString(R.string.create_pwd_hint))
                    return
                }
                if (fragment.type == null) {
                    toast(resources.getString(R.string.coin_type_hint))
                    btn_import.isEnabled = true
                    return
                }
                if (!ll_agree.isSelected) {
                    toast(resources.getString(R.string.create_toast_agree))
                    return
                }
                val type = fragment.type ?: return
                val key = fragment.getText()!!
                val isMain = main
                when (key.length) {
                    56 -> {
                        if (type == WaltType.XLM && !key.startsWith("S")) {
                            toast(resources.getString(R.string.import_key_toast))
                            btn_import.isEnabled = true
                            return
                        }
                    }
                    29 -> {
                        if (type == WaltType.XRP) {
                            try {
                                Seed.getKeyPair(key)
                            } catch (e: Exception) {
                                toast(resources.getString(R.string.import_key_toast))
                                btn_import.isEnabled = true
                                return
                            }
                        } else {
                            toast(resources.getString(R.string.import_key_toast))
                            return
                        }
                    }
                    66 -> {
                        if ((type == WaltType.ETH || type == WaltType.ETC) && !key.startsWith("0x")) {
                            toast(resources.getString(R.string.import_key_toast))
                            btn_import.isEnabled = true
                            return
                        }
                    }
                    64 -> if (!isHexNumber(key)) {
                        toast(resources.getString(R.string.import_key_toast))
                        btn_import.isEnabled = true
                        return
                    }
                    51 -> {
                        if (!key.startsWith("5")) {
                            toast(resources.getString(R.string.import_key_toast))
                            btn_import.isEnabled = true
                            return
                        }
                    }
                    52 -> when (type) {
                        WaltType.LTC -> {
                            if (!(key.startsWith("T") && isMain) || (!isMain && key.startsWith("c"))) {
                                toast(resources.getString(R.string.import_key_toast))
                                btn_import.isEnabled = true
                                return
                            }
                        }
                        WaltType.DASH -> {
                            if (!(key.startsWith("X") && isMain) || (!isMain && key.startsWith("c"))) {
                                toast(resources.getString(R.string.import_key_toast))
                                btn_import.isEnabled = true
                                return
                            }
                        }
                        WaltType.NEO -> {
                            try {
                                NEOSign.getPrivateKeyFromWIF(key)
                            } catch (e: Exception) {
                                toast(resources.getString(R.string.import_key_toast))
                                btn_import.isEnabled = true
                                return
                            }
                        }
                        WaltType.BTC, WaltType.BCH, WaltType.BSV, WaltType.USDT
                            , WaltType.CXC, WaltType.QTUM
                        -> {
                            if (!((key.startsWith("K") || key.startsWith("L") && isMain) || (!isMain && key.startsWith("c")))) {
                                toast(resources.getString(R.string.import_key_toast))
                                btn_import.isEnabled = true
                                return
                            }
                        }
                        else -> {
                            toast(resources.getString(R.string.import_key_toast))
                            btn_import.isEnabled = true
                            return
                        }
                    }
                    else -> {
                        toast(resources.getString(R.string.import_key_toast))
                        btn_import.isEnabled = true
                        return
                    }
                }
                showProgressDialog(resources.getString(R.string.import_ing))
                Flowable.just("")
                        .compose(bindToLifecycle())
                        .map {
                            val keyByteArray = if (key.length == 52) {
                                when (type) {
                                    WaltType.LTC -> LTCUtils.getByteKey(key)
                                    WaltType.DASH -> DashUtils.getByteKey(key)
                                    WaltType.NEO -> NEOSign.getPrivateKeyFromWIF(key)
                                    else -> DumpedPrivateKey.fromBase58(Constant().currentNetworkParams, key).key.privKeyBytes
                                }
                            } else if (key.length == 56 && key.startsWith("S")) {
                                StrKey.decodeStellarSecretSeed(KeyPair.fromSecretSeed(key).secretSeed)
                            } else if (key.length == 66 && key.startsWith("0x")) {
                                Numeric.hexStringToByteArray(key.substring(2, key.length))
                            } else if (key.length == 29) {
                                Numeric.hexStringToByteArray(Seed.getKeyPair(key).privHex())
                            } else {
                                Numeric.hexStringToByteArray(key)
                            }
                            val pwd = fragment.getPwd()
                            val remark = fragment.getRemark()
                            val yWallet = YWallet(MD5Util.getMD5String(pwd), remark, type.ordinal)
                            val w = YJYWalletUtils.getWaltByKey(type, keyByteArray, pwd)
                            w.show = true
                            yWallet.map[type.name] = w
                            if (type == WaltType.BTC) {
                                val w1 = YJYWalletUtils.getWaltByKey(WaltType.USDT, keyByteArray, pwd)
                                w1.show = true
                                yWallet.map[WaltType.USDT.name] = w1
                            }
                            yWallet
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : ResourceSubscriber<YWallet>() {
                            override fun onComplete() {
                            }

                            override fun onNext(t: YWallet) {
                                btn_import.isEnabled = true
                                MyWalletUtils.instance.saveWallet(t)
                                MyWalletUtils.instance.update(t)
                                dismissProgressDialog()
                                startActivity(MainActivity::class.java)
                                EventBus.getDefault().post(UpdateW())
                            }

                            override fun onError(t: Throwable?) {
                                btn_import.isEnabled = true
                                toast(resources.getString(R.string.import_key_toast))
                                dismissProgressDialog()
                            }

                        })


            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x01)
                if (data != null) {
                    if (tl_import.selectedTabPosition == 1) {
                        val fragment = fragments[tl_import.selectedTabPosition] as ImportFragment
                        fragment.setText(data.getStringExtra("sacanurl"))
                    } else {
                        val fragment = fragments[tl_import.selectedTabPosition] as ImportKeyFragment
                        fragment.setText(data.getStringExtra("sacanurl"))
                    }
                }
        }
    }

    fun pri(v: View) {
        val intent = Intent(this, WhatActivity::class.java)
        intent.putExtra("type", Constant.WHAT_101)
        startActivity(intent)
    }

    //十六进制
    fun isHexNumber(str: String): Boolean {
        var flag = false
        for (i in 0 until str.length) {
            val cc = str[i]
            if (cc == '0' || cc == '1' || cc == '2' || cc == '3' || cc == '4' || cc == '5' || cc == '6' || cc == '7' || cc == '8' || cc == '9' || cc == 'A' || cc == 'B' || cc == 'C' ||
                    cc == 'D' || cc == 'E' || cc == 'F' || cc == 'a' || cc == 'b' || cc == 'c' || cc == 'c' || cc == 'd' || cc == 'e' || cc == 'f'
            ) {
                flag = true
            } else {
                return false
            }
        }
        return flag
    }
}