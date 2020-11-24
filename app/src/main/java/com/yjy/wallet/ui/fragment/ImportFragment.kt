package com.yjy.wallet.ui.fragment

import android.app.Activity
import android.content.Intent
import android.text.method.DigitsKeyListener
import com.weiyu.baselib.base.BaseFragment
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.WalletTypeActivity
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.fragment_import.*

/**
 *Created by weiweiyu
 *on 2019/4/29
 * 导入钱包
 */
class ImportFragment : BaseFragment() {
    var type: WaltType? = null

    override fun getContentLayoutResId(): Int = R.layout.fragment_import

    override fun initializeContentViews() {

    }

    override fun viewCreated() {
//        setEditTextInhibitInputSpace(et_remark,Constant.NAME_SIZE)
        setEditTextInhibitInputSpace(et_pwd1, Constant.PWD_SIZE)
        et_import.keyListener = DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghigjklmnopqrstuvwxyz0123456789{}:\"-,")
        rl_choice.setOnClickListener {
            val intent = Intent()
            intent.setClass(activity!!, WalletTypeActivity::class.java)
            intent.putExtra("type", 3)
            startActivityForResult(intent, 101)
        }
        setEdittxtForHw(et_pwd1)
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

    fun getText(): String {
        return et_import.text.toString()
    }

    fun getPwd(): String {
        return et_pwd1.text.toString().trim()
    }

}