package com.yjy.wallet.activity

import android.support.v4.app.Fragment
import android.view.View
import com.androidkun.xtablayout.XTabLayout
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.adapter.PageAdapter
import com.yjy.wallet.ui.fragment.AssetsERCFragment
import com.yjy.wallet.ui.fragment.AssetsHTDFERCFragment
import com.yjy.wallet.ui.fragment.AssetsMainFragment
import com.yjy.wallet.ui.fragment.AssetsStableFragment
import com.yjy.wallet.wallet.HTDFERCType
import kotlinx.android.synthetic.main.activity_add_assets.*

/**
 * weiweiyu
 * 2019/8/27
 * 575256725@qq.com
 * 13115284785
 * 全部币种管理
 */
class AssetsActivity : BaseActivity() {
    var fragments: MutableList<Fragment> = arrayListOf()
    override fun getContentLayoutResId(): Int = R.layout.activity_add_assets

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var tebTitle = resources.getStringArray(R.array.add_asset_tab)
        if (HTDFERCType.values().isNullOrEmpty()) {
            tebTitle = resources.getStringArray(R.array.add_asset_tab2)
        }
        fragments.add(AssetsMainFragment())
        fragments.add(AssetsStableFragment())
        fragments.add(AssetsERCFragment())
        if (HTDFERCType.values().isNotEmpty()) {
            fragments.add(AssetsHTDFERCFragment())
        }
        vp_content.offscreenPageLimit = tebTitle.size
        for (i in tebTitle.indices) {
            tl_import.addTab(tl_import.newTab().setText(tebTitle[i]), false)
        }

        tl_import.addOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabSelected(tab: XTabLayout.Tab?) {
                if (tl_import.selectedTabPosition == 0) {
                    tb_title.setRightLayoutVisibility(View.INVISIBLE)
                } else {
                    tb_title.setRightLayoutVisibility(View.VISIBLE)
                }
            }
        })
        vp_content.adapter = PageAdapter(supportFragmentManager, fragments, tebTitle)
        tl_import.setupWithViewPager(vp_content)
    }

}