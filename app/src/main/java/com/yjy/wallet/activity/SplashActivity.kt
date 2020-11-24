package com.yjy.wallet.activity

import android.annotation.SuppressLint
import com.jaeger.library.StatusBarUtil
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.utils.MyAnimationDrawable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.TimeUnit

/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class SplashActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_splash

    @SuppressLint("CheckResult")
    override fun initializeContentViews() {
        Constant.main = false
        MyAnimationDrawable.animateManuallyFromRawResource(R.drawable.anim_list, iv_anim, {}, {
            Flowable.timer(1500, TimeUnit.MILLISECONDS)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : ResourceSubscriber<Long>() {
                        override fun onError(t: Throwable?) {

                        }

                        override fun onComplete() {
                            startActivity(MainActivity::class.java)
                            finish()
                        }

                        override fun onNext(t: Long?) {

                        }
                    })

        }, 30)
//        setSvg(ModelSVG.values()[0])
    }
//
//    fun setSvg(modelSvg: ModelSVG) {
//        animated_svg_view.setGlyphStrings(*modelSvg.glyphs)
//        animated_svg_view.setFillColors(modelSvg.colors)
//        animated_svg_view.setViewportSize(modelSvg.width, modelSvg.height)
//        animated_svg_view.setTraceResidueColor(0x32000000)
//        animated_svg_view.setTraceColors(modelSvg.colors)
//        animated_svg_view.rebuildGlyphData()
//        animated_svg_view.start()
//    }

    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setTranslucentForImageView(this, 0, null)
    }

}