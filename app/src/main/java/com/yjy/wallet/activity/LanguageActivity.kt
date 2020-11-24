package com.yjy.wallet.activity

import android.view.View
import com.weiyu.baselib.base.ActivityManager
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.tinker.util.BaseApplicationContext
import com.weiyu.baselib.util.LanguageUtil
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_language.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class LanguageActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_language

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        if (LanguageUtil.getLocale(this) == LanguageUtil.LOCALE_ENGLISH) {
            iv_check1.visibility = View.GONE
            iv_check2.visibility = View.VISIBLE
        } else if (LanguageUtil.getLocale(this) == LanguageUtil.LOCALE_CHINESE) {
            iv_check1.visibility = View.VISIBLE
            iv_check2.visibility = View.GONE
        }
    }

    fun check(v: View) {
        var need = false
        when (v.id) {
            R.id.rl_zh -> {
                iv_check1.visibility = View.VISIBLE
                iv_check2.visibility = View.GONE
                need = LanguageUtil.updateLocale(BaseApplicationContext.application, LanguageUtil.LOCALE_CHINESE)
                if (need) {
                    ActivityManager.getInstance().finishAllActivity()
                    startActivity(MainActivity::class.java)
//                    ActivityManager.getInstance().recreateAllOtherActivity(this)
//                    finish()
                }
            }
            R.id.rl_en -> {
                iv_check1.visibility = View.GONE
                iv_check2.visibility = View.VISIBLE
                need = LanguageUtil.updateLocale(BaseApplicationContext.application, LanguageUtil.LOCALE_ENGLISH)
                if (need) {
                    ActivityManager.getInstance().finishAllActivity()
                    startActivity(MainActivity::class.java)
//                    ActivityManager.getInstance().recreateAllOtherActivity(this)
//                    finish()
                }

            }
        }
    }

}