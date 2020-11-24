package com.yjy.wallet.activity.htdf

import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_add_erc.*

/**
 * weiweiyu
 * 2019/8/21
 * 575256725@qq.com
 * 13115284785
 */

class HtdfERCActivity : BaseActivity() {

    override fun getContentLayoutResId(): Int = R.layout.activity_htdf_erc

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tb_title.setRightText(resources.getString(R.string.str_sure))
        tb_title.setRightLayoutVisibility(View.INVISIBLE)
        tl_import.addTab(tl_import.newTab().setText("HRC-20"), true)

    }
}