package com.yjy.wallet.activity.htdf

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.TextView
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.ui.fragment.MyCommissionFragment
import com.yjy.wallet.ui.fragment.NodeFragment
import kotlinx.android.synthetic.main.activity_htdf_node.*

/**
 * weiweiyu
 * 2020/4/8
 * 575256725@qq.com
 * 13115284785
 * 华特东方超级节点列表
 */

class HtdfNodeActivity : BaseActivity() {

    var fragments = mutableListOf<Fragment>()
    var textviews = mutableListOf<TextView>()
    override fun getContentLayoutResId(): Int = R.layout.activity_htdf_node

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tb_title.setRightText(resources.getString(R.string.commission_node_hint19))
        tb_title.setRightLayoutVisibility(View.VISIBLE)
        tb_title.setRightLayoutClickListener(View.OnClickListener {
            startActivity(HTDFMortgageHelpActivity::class.java)
        })
        textviews.add(tv_left)
        textviews.add(tv_right)
        var node = NodeFragment()
        var b = Bundle()
        b.putSerializable("data", intent.getSerializableExtra("data"))
        node.arguments = b
        var my = MyCommissionFragment()
        my.arguments = b
        fragments.add(node)
        fragments.add(my)
        supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, fragments[index])
                .show(fragments[index]).commit()

        tv_left.setOnClickListener { onclick(it) }
        tv_right.setOnClickListener { onclick(it) }
    }

    var index = 0
    var currentTabIndex = 0
    fun onclick(v: View) {
        when (v.id) {
            R.id.tv_left -> {
                index = 0
            }
            R.id.tv_right -> {
                index = 1
            }
        }
        if (currentTabIndex != index) {
            val trx = supportFragmentManager.beginTransaction()
            trx.hide(fragments[currentTabIndex])
            textviews[currentTabIndex].setTextColor(resources.getColor(R.color.txt_454545))
            textviews[currentTabIndex].background = resources.getDrawable(R.color.transparent)
            if (!fragments[index].isAdded) {
                trx.add(R.id.fl_content, fragments[index])
            }
            textviews[index].setTextColor(resources.getColor(R.color.white))
            textviews[index].background = resources.getDrawable(R.drawable.btn_blue_shape)
            trx.show(fragments[index]).commit()
        } else {
//            fragments[index].backTop()
        }
        currentTabIndex = index
    }

}