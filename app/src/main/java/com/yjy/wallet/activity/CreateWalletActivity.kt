package com.yjy.wallet.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.AesUtils
import com.weiyu.baselib.util.BLog
import com.weiyu.baselib.util.MD5Util
import com.weiyu.baselib.util.StringUtils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.bean.UpdateW
import com.yjy.wallet.utils.YJYWalletUtils
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WaltType
import com.yjy.wallet.wallet.YWallet
import com.yjy.wallet.wordlist.Chinese_simplified
import com.yjy.wallet.wordlist.English
import com.zhl.cbdialog.CBDialogBuilder
import de.greenrobot.event.EventBus
import io.github.novacrypto.bip39.WordList
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_create_wallet.*

/**
 *Created by weiweiyu
 *on 2019/4/29
 * 创建钱包
 */
class CreateWalletActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_create_wallet
    var language = 1
    var languageArray: Array<String>? = null
    override fun initializeContentViews() {
        (MyWalletUtils.instance.getWallet() == null || MyWalletUtils.instance.getWallet()!!.isEmpty())
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var txt = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnBg()
            }

        }
        et_name.addTextChangedListener(txt)
        et_pwd2.addTextChangedListener(txt)
//        setEditTextInhibitInputSpace(et_name, Constant.NAME_SIZE)
//        languageArray = resources.getStringArray(R.array.sp_language)
//        if (LanguageUtil.getLocale(this) == LanguageUtil.LOCALE_ENGLISH) {
//            language = 0
//        } else if (LanguageUtil.getLocale(this) == LanguageUtil.LOCALE_CHINESE) {
//            language = 1
//        }
//        tv_language.text = languageArray!![language]
        rl_language.setOnClickListener {
            var intent = Intent(this@CreateWalletActivity, AddrTypeActivity::class.java)
            intent.putExtra("type", 1)
            intent.putExtra("position", language)
            startActivityForResult(intent, 100)
        }
        setEditTextInhibitInputSpace(et_pwd2, Constant.PWD_SIZE)
        setEditTextInhibitInputSpace(et_pwd, Constant.PWD_SIZE)
        et_pwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(et_pwd.text)) {
                    rl_hint.visibility = View.VISIBLE
                    if (et_pwd.text.length < 8) {
                        tv_pwd_hint.visibility = View.VISIBLE
                    } else {
                        tv_pwd_hint.visibility = View.INVISIBLE
                    }
                    val s = StringUtils.passwordStrong(et_pwd.text.toString())
                    rb_type.progress = s
                    when (s) {
                        20 -> tv_type.text = resources.getStringArray(R.array.pwd_type)[0]
                        60 -> tv_type.text = resources.getStringArray(R.array.pwd_type)[1]
                        90 -> tv_type.text = resources.getStringArray(R.array.pwd_type)[2]
                    }
                } else {
                    rl_hint.visibility = View.GONE
                }
                btnBg()
            }

        })
    }

    fun btnBg() {
        if (!TextUtils.isEmpty(et_name.text) && !TextUtils.isEmpty(et_pwd.text) && et_pwd.text.length >= 8 && !TextUtils.isEmpty(et_pwd2.text) && ll_agree.isSelected) {
            btn_create.isEnabled = true
            btn_create.setBackgroundResource(R.drawable.btn_5f8def_selector)
        } else {
            btn_create.isEnabled = false
            btn_create.setBackgroundResource(R.drawable.btn_cccccc_selector)
        }
    }

    fun create(v: View) {
        if (TextUtils.isEmpty(et_name.text)) {
            toast(resources.getString(R.string.create_toast_remark))
            return
        }
        MyWalletUtils.instance.getWallet()?.forEach {
            if (it.remark.equals(et_name.text.toString())) {
                toast(resources.getString(R.string.create_toast_hasremark))
                return
            }
        }
        if (TextUtils.isEmpty(et_pwd.text)) {
            toast(resources.getString(R.string.create_toast_pwd))
            return
        }
        if (!StringUtils.isPwd(et_pwd.text.toString())) {
            toast(resources.getString(R.string.create_pwd_hint))
            return
        }
        if (!TextUtils.equals(et_pwd.text, et_pwd2.text)) {
            toast(resources.getString(R.string.create_toast_no_equals))
            return
        }
        if (!ll_agree.isSelected) {
            toast(resources.getString(R.string.create_toast_agree))
            return
        }
        val pwd = et_pwd.text.toString().trim()
        btn_create.isEnabled = false
        showProgressDialog(resources.getString(R.string.create_toast_create))

        var wordList: WordList = when (language) {
            0 -> Chinese_simplified.INSTANCE
            1 -> English.INSTANCE
            else -> English.INSTANCE
        }

        val wallet = YJYWalletUtils.createWallet(wordList)
        val str = StringBuffer()
        wallet.activeKeyChain.mnemonicCode!!.forEach { item ->
            str.append(item).append(" ")
        }
        str.deleteCharAt(str.length - 1)
        Flowable.just(wallet)
                .compose(bindToLifecycle())
                .map {
                    //创建钱包
                    val list: MutableList<YWallet> = arrayListOf()
                    val words = AesUtils.encrypt(str.toString(), pwd)//创建先保存助记词
                    val yWallet = YWallet(MD5Util.getMD5String(pwd), et_name.text.toString(), 100)
                    yWallet.words = words
                    yWallet.show = true
                    WaltType.values().forEach { type ->
                        var walt = YJYWalletUtils.getWaltByWallet(it, type, pwd, 0)
                        walt.show = true
                        if (type != WaltType.USDT) {
                            yWallet.map[type.name] = walt
                        } else {
                            yWallet.wenMap[type.name] = walt
                        }
                    }
                    list.add(yWallet)
                    MyWalletUtils.instance.saveWallet(list)
                    list
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ResourceSubscriber<MutableList<YWallet>>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: MutableList<YWallet>) {
                        if (t.isNotEmpty() && t.size == 1) {
                            MyWalletUtils.instance.update(t[0])
                        }
                        EventBus.getDefault().post(UpdateW())
                        dismissProgressDialog()
                        show(str.toString(), t)
                    }

                    override fun onError(t: Throwable?) {
                        BLog.d("")
                    }

                })

    }


    fun agree(v: View) {
        if (ll_agree.isSelected) {
            ll_agree.isSelected = false
            iv_check.setImageDrawable(resources.getDrawable(R.mipmap.weixuanze1zhuangtai))
        } else {
            ll_agree.isSelected = true
            iv_check.setImageDrawable(resources.getDrawable(R.mipmap.xuanzezhaungtai))
        }
        btnBg()
    }

    fun pri(v: View) {
        var intent = Intent(this, WhatActivity::class.java)
        intent.putExtra("type", Constant.WHAT_100)
        startActivity(intent)
    }

    var isBtn = false
    fun show(words: String, yWallet: MutableList<YWallet>) {
        var dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 0.8f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                .setView(R.layout.dialog_backups)
                .create()
        with(dialog) {
            setOnDismissListener {
                startActivity(MainActivity::class.java)
                if (isBtn) {
                    val intent = Intent(this@CreateWalletActivity, ExportWordsActivity::class.java)
                    intent.putExtra("words", words)
                    intent.putExtra("data", Gson().toJson(yWallet))
                    startActivity(intent)
                }
                finish()
            }
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.right_layout).setOnClickListener {
                dismiss()
            }
            findViewById<TextView>(R.id.btn_backups).setOnClickListener {
                isBtn = true
                dismiss()
            }
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                if (data != null) {
                    language = data.getIntExtra("position", 0)
                    tv_language.text = languageArray!![language]
                }
            }
        }
    }
}