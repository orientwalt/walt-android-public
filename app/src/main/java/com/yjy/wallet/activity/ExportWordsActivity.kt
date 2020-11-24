package com.yjy.wallet.activity

import android.content.Intent
import android.view.View
import android.view.WindowManager
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_export_words.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class ExportWordsActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_export_words

    override fun initializeContentViews() {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var s = intent.getStringExtra("words").split(" ")
        tv_w1.text = s[0]
        tv_w2.text = s[1]
        tv_w3.text = s[2]
        tv_w4.text = s[3]
        tv_w5.text = s[4]
        tv_w6.text = s[5]
        tv_w7.text = s[6]
        tv_w8.text = s[7]
        tv_w9.text = s[8]
        tv_w10.text = s[9]
        tv_w11.text = s[10]
        tv_w12.text = s[11]
        btn_next.setOnClickListener {
            val intent = Intent(this@ExportWordsActivity, WordsActivity::class.java)
            intent.putExtra("data", getIntent().getStringExtra("data"))
            intent.putExtra("words", getIntent().getStringExtra("words"))
            startActivity(intent)
            finish()
        }
    }

}