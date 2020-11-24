package com.yjy.wallet.ui.fragment

import android.view.View
import com.weiyu.baselib.base.BaseFragment
import com.yjy.wallet.R
import com.yjy.wallet.activity.TestActivity
import kotlinx.android.synthetic.main.fragment_test.*

/**
 *Created by weiweiyu
 *on 2019/5/9
 * 评测
 */

class TestFragment : BaseFragment() {
    lateinit var test: Array<String>
    override fun getContentLayoutResId(): Int = R.layout.fragment_test
    var anser = ""
    override fun initializeContentViews() {
        test = arguments!!.getStringArray("data")
    }

    override fun viewCreated() {
        val a = IntArray(4)
        var i = 0
        while (i < a.size) {
            a[i] = (Math.random() * 4).toInt()
            for (j in 0 until i) {
                if (a[j] == a[i]) {
                    i--
                }
            }
            i++
        }
        tv_test_title.text = arguments!!.getString("type") + test[0]
        rb_1.text = "A" + test[a[0] + 1]
        rb_2.text = "B" + test[a[1] + 1]
        rb_3.text = "C" + test[a[2] + 1]
        rb_4.text = "D" + test[a[3] + 1]
        rg_test.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_1 -> anser = rb_1.text.toString().replaceFirst("A", "")
                R.id.rb_2 -> anser = rb_2.text.toString().replaceFirst("B", "")
                R.id.rb_3 -> anser = rb_3.text.toString().replaceFirst("C", "")
                R.id.rb_4 -> anser = rb_4.text.toString().replaceFirst("D", "")
            }
            (activity as TestActivity).show()
        }
    }

    fun solution(str: String) {
        if (rb_1.text.toString().contains(str)) {
            tv_solution.text = String.format(resources.getString(R.string.test_solution), rb_1.text)
        }
        if (rb_2.text.toString().contains(str)) {
            tv_solution.text = String.format(resources.getString(R.string.test_solution), rb_2.text)
        }
        if (rb_3.text.toString().contains(str)) {
            tv_solution.text = String.format(resources.getString(R.string.test_solution), rb_3.text)
        }
        if (rb_4.text.toString().contains(str)) {
            tv_solution.text = String.format(resources.getString(R.string.test_solution), rb_4.text)
        }
        rg_test.clearCheck()
        tv_solution.visibility = View.VISIBLE
    }

}