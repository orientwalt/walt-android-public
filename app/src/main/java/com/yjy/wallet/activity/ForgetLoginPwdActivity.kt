package com.yjy.wallet.activity

import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_forget_login_pwd.*

/**
 *Created by weiweiyu
 * 575256725@qq.com
 *on 2019/7/12
 */

class ForgetLoginPwdActivity : BaseActivity() {
    lateinit var mail: String
    override fun getContentLayoutResId(): Int = R.layout.activity_forget_login_pwd

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        mail = intent.getStringExtra("mail")
    }

    fun btn(v: View) {

    }

}