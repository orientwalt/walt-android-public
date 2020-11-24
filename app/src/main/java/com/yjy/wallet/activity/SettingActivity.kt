package com.yjy.wallet.activity

import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_setting.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class SettingActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_setting

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
    }

    fun language(v: View) {
        startActivity(LanguageActivity::class.java)
    }
    fun node(v: View) {
        startActivity(NodeActivity::class.java)
    }

}