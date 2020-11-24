package com.yjy.wallet.ui.fragment

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import com.weiyu.baselib.base.BaseFragment
import com.weiyu.baselib.util.StringUtils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.WalletTypeActivity
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.fragment_import_key.*

/**
 *Created by weiweiyu
 *on 2019/4/29
 * 导入钱包
 */
class ImportKeyFragment : BaseFragment() {
    override fun getContentLayoutResId(): Int = R.layout.fragment_import_key
    override fun initializeContentViews() {

    }

    var type: WaltType? = null
    override fun viewCreated() {
//        setEditTextInhibitInputSpace(et_remark,Constant.NAME_SIZE)
        setEditTextInhibitInputSpace(et_pwd1, Constant.PWD_SIZE)
        setEditTextInhibitInputSpace(et_pwd2, Constant.PWD_SIZE)
        et_import.keyListener = DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghigjklmnopqrstuvwxyz0123456789")
        setEdittxtForHw(et_pwd1)
        setEdittxtForHw(et_pwd2)
        rl_choice.setOnClickListener {
            val intent = Intent()
            intent.setClass(activity!!, WalletTypeActivity::class.java)
            intent.putExtra("type", 3)
            startActivityForResult(intent, 101)
        }
        et_pwd1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(et_pwd1.text)) {
                    rl_hint.visibility = View.VISIBLE
                    if (et_pwd1.text.length < 8) {
                        tv_pwd_hint.visibility = View.VISIBLE
                    } else {
                        tv_pwd_hint.visibility = View.INVISIBLE
                    }
                    val s = StringUtils.passwordStrong(et_pwd1.text.toString())
                    rb_type.progress = s
                    when (s) {
                        20 -> tv_type.text = resources.getStringArray(R.array.pwd_type)[0]
                        60 -> tv_type.text = resources.getStringArray(R.array.pwd_type)[1]
                        90 -> tv_type.text = resources.getStringArray(R.array.pwd_type)[2]
                    }
                } else {
                    rl_hint.visibility = View.GONE
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x01)
                if (data != null) {

                    setText(data.getStringExtra("sacanurl"))

                }
            if (requestCode == 101) {
                if (data != null) {
                    type = data.getSerializableExtra("type") as WaltType
                    tv_language.text = type!!.name.toUpperCase()
                    iv_coin.setImageDrawable(resources.getDrawable(type!!.drawable))
                }
            }
        }
    }

    fun setText(string: String) {
        et_import.setText(string)
    }

    fun getRemark(): String {
        return et_remark.text.toString()
    }

    fun getText(): String? {
        return et_import.text.toString()
    }

    fun getPwd(): String {
        return et_pwd1.text.toString().trim()
    }

    fun getPwd2(): String {
        return et_pwd2.text.toString()
    }
}