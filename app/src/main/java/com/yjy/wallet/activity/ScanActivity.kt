package com.yjy.wallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import com.jaeger.library.StatusBarUtil
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_scan1.*


/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class ScanActivity : BaseActivity() {
    val IMAGE_PICKER = 100

    override fun getContentLayoutResId(): Int = R.layout.activity_scan1

    override fun initializeContentViews() {
        title_tb.setTitleTextColor(R.color.white)
        title_tb.setLeftLayoutClickListener(View.OnClickListener { finish() })
        val captureFragment = CaptureFragment()
        captureFragment.analyzeCallback = object : CodeUtils.AnalyzeCallback {
            override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
                handleResult(result)
            }

            override fun onAnalyzeFailed() {

            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commit()
    }



    @SuppressLint("NewApi")
    private fun handleResult(result: String?) {
        result ?: return
        val intent = Intent()
        intent.putExtra("sacanurl", result)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setDarkMode(this)
        StatusBarUtil.setColor(this, resources.getColor(R.color.alpha_50_black), 0)
    }
}