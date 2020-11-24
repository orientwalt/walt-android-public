package com.yjy.wallet.activity

import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.bean.UpdateW
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.YWallet
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.activity_words.*


/**
 *Created by weiweiyu
 *on 2019/4/30
 * 导出助记词第二步
 */
class WordsActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_words
    var map: MutableMap<Int, String> = mutableMapOf()
    override fun initializeContentViews() {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var s = intent.getStringExtra("words").split(" ")
        val a = IntArray(12)
        var i = 0
        while (i < a.size) {
            a[i] = (Math.random() * 12).toInt()
            for (j in 0 until i) {
                if (a[j] == a[i]) {
                    i--
                }
            }
            i++
        }
        tv_w1.text = s[a[0]]
        tv_w2.text = s[a[1]]
        tv_w3.text = s[a[2]]
        tv_w4.text = s[a[3]]
        tv_w5.text = s[a[4]]
        tv_w6.text = s[a[5]]
        tv_w7.text = s[a[6]]
        tv_w8.text = s[a[7]]
        tv_w9.text = s[a[8]]
        tv_w10.text = s[a[9]]
        tv_w11.text = s[a[10]]
        tv_w12.text = s[a[11]]
        btn_next.setOnClickListener {
            if (intent.getStringExtra("words") == tv_words.text.toString().trim()) {
                //确认助记词之后删除助记词
                var yWallet = Gson().fromJson<MutableList<YWallet>>(intent.getStringExtra("data"), object : TypeToken<MutableList<YWallet>>() {}.type)
                yWallet.forEach {
                    MyWalletUtils.instance.updateWords(it)
                }
                EventBus.getDefault().post(yWallet)
                toast(resources.getString(R.string.words_sure_finish1))
                EventBus.getDefault().post(UpdateW())
                finish()
            } else {
                toast(resources.getString(R.string.words_sure_finish2))
                clear()
            }
        }
    }

    fun clear() {
        tv_words.text = ""
        tv_w1.isSelected = false
        tv_w2.isSelected = false
        tv_w3.isSelected = false
        tv_w4.isSelected = false
        tv_w5.isSelected = false
        tv_w6.isSelected = false
        tv_w7.isSelected = false
        tv_w8.isSelected = false
        tv_w9.isSelected = false
        tv_w10.isSelected = false
        tv_w11.isSelected = false
        tv_w12.isSelected = false
        tv_w1.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w2.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w3.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w4.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w5.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w6.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w7.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w8.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w9.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w10.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w11.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        tv_w12.setBackgroundResource(R.drawable.btn_eeeeee_shape)
        map.clear()
    }

    fun select(v: View) {
        (v as TextView)
        if (v.isSelected) {
            v.isSelected = false
            v.setBackgroundResource(R.drawable.btn_eeeeee_shape)
            map.remove(v.id)
        } else {
            v.isSelected = true
            v.setBackgroundResource(R.drawable.btn_eeeeee50_shape)
            map[v.id] = v.text.toString()
        }
        val sb = StringBuffer()
        map.forEach {
            sb.append(it.value).append(" ")
        }
        if (sb.isNotEmpty())
            sb.deleteCharAt(sb.length - 1)
        tv_words.text = sb.toString()
    }

}