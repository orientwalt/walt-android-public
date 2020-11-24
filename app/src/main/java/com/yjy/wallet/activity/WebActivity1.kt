package com.yjy.wallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_what.*


/**
 *Created by weiweiyu
 *on 2019/4/30
 */
class WebActivity1 : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_web1

    @SuppressLint("SetJavaScriptEnabled")
    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tb_title.setTitle(intent.getStringExtra("title"))
        val webSettings = webview.getSettings()
        //设置为可调用js方法
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.blockNetworkImage = false
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
                super.onReceivedSslError(view, handler, error)
            }
        }
        webview.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.setData(Uri.parse(url))
            startActivity(intent)
        }
        webview.loadUrl(intent.getStringExtra("url"))
    }

}
