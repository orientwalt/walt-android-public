package com.yjy.wallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_add_coin.*

/**
 * weiweiyu
 * 2019/8/27
 * 575256725@qq.com
 * 13115284785
 */
class AddCoinActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_add_coin

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
    }

    fun btn1(v: View) {
        startActivity(CreateWalletActivity::class.java)
        finish()
    }


    @SuppressLint("CheckResult")
    fun btn2(v: View) {
        val intent = Intent(this, ImportWalletActivity::class.java)
        startActivity(intent)
        finish()
    }

}