package com.yjy.wallet.activity.htdf

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.QRCodeUtil
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.MainActivity
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.utils.TimeUtils
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_success.*

/**
 *Created by weiweiyu
 *on 2019/5/10
 * 交易成功页面
 */
class HTDFSuccessActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_success
    override fun initializeContentViews() {
        tb_title.setLeftLayoutVisibility(View.INVISIBLE)
        tb_title.setRightText(resources.getString(R.string.words_sure_finish))
        if (intent.getIntExtra("type", 0) == 1) {
            tv_back.visibility = View.VISIBLE
        }
        tb_title.setRightLayoutClickListener(View.OnClickListener { finish() })
        var txitem = intent.getSerializableExtra("data1") as TxItem
        tv_change.setOnClickListener {
            var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", txitem.Hash?.toLowerCase())
            s.setPrimaryClip(mClipData)
            toast(resources.getString(R.string.receive_copy_toast))
        }

        set(txitem)
        Flowable.just(QRCodeUtil.createQRCode(txitem.Hash?.toLowerCase(), resources.getDimensionPixelSize(R.dimen.dp100)))
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
        tv_to.setOnClickListener {
            var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", txitem.From)
            s.setPrimaryClip(mClipData)
            toast(resources.getString(R.string.receive_copy_toast))
        }
        tv_from.setOnClickListener {
            var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", txitem.To)
            s.setPrimaryClip(mClipData)
            toast(resources.getString(R.string.receive_copy_toast))
        }
    }

    fun set(txinfo: TxItem) {
        tv_price.text = "-" + Utils.toSubStringDegistForChart(
                txinfo.amount!!.replace(",", "").toDouble(),
                Constant.priceP,
                false
        ) + " " + txinfo.denom!!.toUpperCase()
        tv_from.text = txinfo.From
        tv_to.text = txinfo.To
        tv_remark.text = txinfo.msg
        tv_change.text = txinfo.Hash
        if (txinfo.Height == "0") {
            iv_icon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.waite_icon, 0, 0)
            iv_icon.text = resources.getString(R.string.success_title2)
        } else {
            tv_qu_txt.visibility = View.VISIBLE
            tv_qu.visibility = View.VISIBLE
            tv_qu.text = txinfo.Height
            iv_icon.text = resources.getString(R.string.success_title)
        }
        tv_time.text = TimeUtils.stampToDate(txinfo.Time)
        tv_change.setOnClickListener {
            var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            s.text = txinfo.Hash
            toast(resources.getString(R.string.receive_copy_toast))
        }
        tv_back.setOnClickListener { startActivity(MainActivity::class.java) }
    }

}