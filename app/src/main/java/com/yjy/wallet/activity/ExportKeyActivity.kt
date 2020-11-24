package com.yjy.wallet.activity

import android.support.v4.app.Fragment
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.adapter.PageAdapter
import com.yjy.wallet.ui.fragment.KeyCodeFragment
import com.yjy.wallet.ui.fragment.KeyFragment
import kotlinx.android.synthetic.main.activity_export_key.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class ExportKeyActivity : BaseActivity() {

    var fragments: MutableList<Fragment> = arrayListOf()
    override fun getContentLayoutResId(): Int = R.layout.activity_export_key

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var tebTitle = resources.getStringArray(R.array.export_tab)
        vp_content.offscreenPageLimit = tebTitle.size
        fragments.add(KeyFragment())
        fragments.add(KeyCodeFragment())
        vp_content.adapter = PageAdapter(supportFragmentManager, fragments, tebTitle)
        tl_import.setupWithViewPager(vp_content)
    }

}