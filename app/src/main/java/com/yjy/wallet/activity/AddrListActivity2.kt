package com.yjy.wallet.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.ui.fragment.AddressFragment
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.activity_address_list.tb_title
import kotlinx.android.synthetic.main.activity_address_list2.*

/**
 *Created by weiweiyu
 *on 2019/6/5
 * 地址本
 */
class AddrListActivity2 : BaseActivity() {
    var fragments: MutableList<Fragment> = ArrayList()
    override fun getContentLayoutResId(): Int = R.layout.activity_address_list2

    override fun initializeContentViews() {
        tb_title.setRightLayoutClickListener(View.OnClickListener { startActivity(AddAddrActivity::class.java) })
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var titles: MutableList<String> = mutableListOf()
        titles.add(resources.getString(R.string.quotes_all))
        WaltType.values().forEach {
            if (it != WaltType.USDT && it != WaltType.EOS)
                titles.add(it.name)
        }
        titles.forEach {
            var fragment = AddressFragment()
            var b = Bundle()
            b.putString("type", it)
            fragment.arguments = b
            fragments.add(fragment)
            var tab = xTablayout.newTab()
            tab.text = it.toUpperCase()
            xTablayout.addTab(tab)
        }
        viewpager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getCount(): Int {
                return fragments.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position].toUpperCase()
            }
        }
        xTablayout.setupWithViewPager(viewpager)
    }

}