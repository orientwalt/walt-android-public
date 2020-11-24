package com.yjy.wallet.activity

import android.support.v4.app.Fragment
import android.view.View
import com.jaeger.library.StatusBarUtil
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.adapter.PageAdapter
import com.yjy.wallet.ui.fragment.KeyCodeFragment2
import com.yjy.wallet.ui.fragment.KeyFragment2
import kotlinx.android.synthetic.main.activity_export_key.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class ExportKeyActivity2 : BaseActivity() {

    var fragments: MutableList<Fragment> = arrayListOf()
    override fun getContentLayoutResId(): Int = R.layout.activity_export_key

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tb_title.setTitle(resources.getString(R.string.walletinfo_item3))
        tb_title.setTitleTextColor(R.color.txt_333333)
        var tebTitle = resources.getStringArray(R.array.export_tab1)
        vp_content.offscreenPageLimit = tebTitle.size
        fragments.add(KeyFragment2())
        fragments.add(KeyCodeFragment2())
        vp_content.adapter = PageAdapter(supportFragmentManager, fragments, tebTitle)
        tl_import.setupWithViewPager(vp_content)
    }

    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setLightMode(this)
        StatusBarUtil.setColor(this, resources.getColor(R.color.white), 0)
    }
}