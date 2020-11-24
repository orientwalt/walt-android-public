package com.yjy.wallet.activity

import android.text.Html
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_what.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class NoticeInfoActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_what

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tb_title.setTitle(resources.getStringArray(R.array.notice_tab)[1])
        tv_title1.text = intent.getStringExtra("title")
        tv_content.text = Html.fromHtml(intent.getStringExtra("content"))
    }

}
