package com.yjy.wallet.activity.utxo

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

class AddERCActivity : BaseActivity() {

    override fun getContentLayoutResId(): Int = R.layout.activity_add_erc

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tb_title.setRightText(resources.getString(R.string.str_sure))
        tl_import.addTab(tl_import.newTab().setText("ERC-20"), true)
    }
}