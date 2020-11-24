package com.yjy.wallet.ui.fragment

import android.graphics.Bitmap
import com.weiyu.baselib.base.BaseFragment
import com.weiyu.baselib.util.QRCodeUtil
import com.yjy.wallet.R
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.fragment_key_code.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 * keystore二维码
 */
class KeyCodeFragment : BaseFragment() {
    override fun getContentLayoutResId(): Int = R.layout.fragment_key_code

    override fun initializeContentViews() {

    }

    override fun viewCreated() {
        Flowable.just(QRCodeUtil.createQRCode(activity!!.intent.getStringExtra("key"), resources.getDimensionPixelSize(R.dimen.dp200)))
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
    }

}