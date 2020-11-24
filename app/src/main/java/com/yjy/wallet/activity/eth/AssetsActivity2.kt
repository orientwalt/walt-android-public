package com.yjy.wallet.activity.eth

import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_add_assets2.*

/**
 * weiweiyu
 * 2019/8/27
 * 575256725@qq.com
 * 13115284785
 * 全部币种管理
 */
class AssetsActivity2 : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_add_assets2

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tl_import.addTab(tl_import.newTab().setText(resources.getString(R.string.assets_wen)), true)
    }

}