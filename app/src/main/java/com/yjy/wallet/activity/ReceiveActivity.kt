package com.yjy.wallet.activity

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.jaeger.library.StatusBarUtil
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.QRCodeUtil
import com.yjy.wallet.R
import com.yjy.wallet.utils.CoinUtils
import com.yjy.wallet.wallet.WInfo
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_receive.*


/**
 *Created by weiweiyu
 *on 2019/4/29
 * 收款二维码
 */
class ReceiveActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_receive

    override fun initializeContentViews() {
        tb_title.setTitleTextColor(R.color.white)
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var wallet = intent.getSerializableExtra("data") as WInfo
        tv_hint.text = String.format(resources.getString(R.string.receive_hint), wallet.unit.toUpperCase())
        tv_address.text = wallet.address
        iv_icon.setImageDrawable(CoinUtils.getDrawableByType(wallet, this))
        Flowable.just(QRCodeUtil.createQRCode(wallet.address, 800, 0xff000000.toInt(), 0xfff5f5f5.toInt()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(object : ResourceSubscriber<Bitmap>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: Bitmap?) {
                        iv_code.setImageBitmap(t)
                    }

                    override fun onError(t: Throwable?) {
                    }
                })
        tv_address.setOnClickListener {
            var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            s.text = wallet.address
            toast(resources.getString(R.string.receive_copy_toast))
        }
    }

    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setTranslucentForImageView(this, 0, null)
    }
}