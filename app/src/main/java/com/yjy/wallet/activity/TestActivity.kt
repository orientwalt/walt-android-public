package com.yjy.wallet.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.Constant.Companion.first
import com.yjy.wallet.R
import com.yjy.wallet.adapter.PageAdapter
import com.yjy.wallet.ui.fragment.TestFragment
import com.zhl.cbdialog.CBDialogBuilder
import kotlinx.android.synthetic.main.activity_test.*


/**
 *Created by weiweiyu
 *on 2019/5/9
 * 评测界面
 */

class TestActivity : BaseActivity() {
    var fragments: MutableList<Fragment> = arrayListOf()
    override fun getContentLayoutResId(): Int = R.layout.activity_test
    var solution = arrayOf("A", "A", "A", "A", "A", "A", "A")
    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var test = arrayOf(resources.getStringArray(R.array.test1), resources.getStringArray(R.array.test2), resources.getStringArray(R.array.test3), resources.getStringArray(R.array.test4), resources.getStringArray(R.array.test5), resources.getStringArray(R.array.test6), resources.getStringArray(R.array.test7))
        var tebTitle = arrayOf("1", "2", "3", "4", "5", "6", "7")
        val a = IntArray(7)
        var i = 0
        while (i < a.size) {
            a[i] = (Math.random() * 7).toInt()
            for (j in 0 until i) {
                if (a[j] == a[i]) {
                    i--
                }
            }
            i++
        }
        vp_content.offscreenPageLimit = tebTitle.size
        for (i in tebTitle.indices) {
            tl_import.addTab(tl_import.newTab().setText(tebTitle[i]), false)
            var f = TestFragment().apply {
                arguments = Bundle().apply {
                    solution[i] = test[a[i]][1]
                    putStringArray("data", test[a[i]])
                    putString("type", tebTitle[i])
                }
            }
            fragments.add(f)
        }
        vp_content.adapter = PageAdapter(supportFragmentManager, fragments, tebTitle)
        tl_import.setupWithViewPager(vp_content)

    }


    fun show() {
        var finish = true
        fragments.forEach {
            var s = (it as TestFragment).anser
            if (TextUtils.isEmpty(s)) {
                finish = false
            }
        }
        if (finish) {
            btn_finish.visibility = View.VISIBLE
            btn_finish.setOnClickListener {
                var s1 = ""
                val str = StringBuffer()
                for (i in fragments.indices) {
                    var s = (fragments[i] as TestFragment).anser
                    if (solution[i] != s) {
                        (fragments[i] as TestFragment).solution(solution[i])
                        if (TextUtils.isEmpty(s1)) {
                            s1 = i.toString()
                        }
                        str.append(i + 1).append("、")
                    }
                }
                if (str.isNotEmpty()) {
                    str.deleteCharAt(str.length - 1)
                }
                if (str.isEmpty()) {
                    dialog(s1, resources.getString(R.string.test_finish4))
                } else {
                    dialog(s1, String.format(resources.getString(R.string.test_finish2), str))
                }
            }

        }
    }

    fun dialog(s: String, title: String) {
        var dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 0.8f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                .setView(R.layout.dialog_test)
                .create()
        with(dialog) {
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            findViewById<TextView>(R.id.tv_title).text = title
            var iv = findViewById<ImageView>(R.id.iv_icon)
            var btn = findViewById<TextView>(R.id.btn_finish)

            if (TextUtils.isEmpty(s)) {
                iv.setImageDrawable(resources.getDrawable(R.mipmap.huidazhengque))
                btn.text = resources.getString(R.string.test_finish5)
            } else {
                iv.setImageDrawable(resources.getDrawable(R.mipmap.huidacuowu))
                btn.text = resources.getString(R.string.test_finish3)
            }
            btn.setOnClickListener {
                if (TextUtils.isEmpty(s)) {
                    first = true
                    dismiss()
                    finish()
                } else {
                    dismiss()
                    this@TestActivity.vp_content.currentItem = s.toInt()
                }
            }
            show()
        }
    }
}