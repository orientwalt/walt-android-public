package com.yjy.wallet.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class PageAdapter : FragmentPagerAdapter {

    var fragments: MutableList<Fragment> = ArrayList()
    var array: Array<String>? = null

    constructor(fm: FragmentManager, fragments: MutableList<Fragment>, array: Array<String>) : super(fm) {
        this.fragments = fragments
        this.array = array
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int = fragments.size
    override fun getPageTitle(position: Int): CharSequence? {
        return array!![position]
    }
}
