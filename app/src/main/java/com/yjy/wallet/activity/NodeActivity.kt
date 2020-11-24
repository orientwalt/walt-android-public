package com.yjy.wallet.activity

import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.R
import com.yjy.wallet.wallet.MyWalletUtils
import kotlinx.android.synthetic.main.activity_node.*


/**
 *Created by weiweiyu
 *on 2019/5/18
 * 节点设置
 */
class NodeActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_node

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })

        if (main) {
            iv_check1.visibility = View.VISIBLE
            iv_check2.visibility = View.GONE
            rl_en.visibility = View.GONE
            v_line.visibility = View.GONE
        } else {
            v_line.visibility = View.VISIBLE
            iv_check1.visibility = View.GONE
            iv_check2.visibility = View.VISIBLE
        }
        ll_root.setOnClickListener {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                count = 0
                mExitTime = System.currentTimeMillis()
            } else {
                count++
                if (count == 7) {
                    rl_en.visibility = View.VISIBLE
                    v_line.visibility = View.VISIBLE
                    ll_root.setOnClickListener { }
                }
            }
        }
    }

    var count = 0
    var mExitTime: Long = 0
    fun check(v: View) {
        when (v.id) {
            R.id.rl_zh -> {
                main = true
                iv_check1.visibility = View.VISIBLE
                iv_check2.visibility = View.GONE
            }
            R.id.rl_en -> {
                main = false
                iv_check1.visibility = View.GONE
                iv_check2.visibility = View.VISIBLE
            }
        }
        MyWalletUtils.instance.clear()
    }

}