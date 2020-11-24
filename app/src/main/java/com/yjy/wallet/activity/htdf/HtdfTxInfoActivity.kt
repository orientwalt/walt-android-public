package com.yjy.wallet.activity.htdf

import android.content.*
import android.graphics.Bitmap
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.QRCodeUtil
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.WebActivity1
import com.yjy.wallet.api.HTDFService
import com.yjy.wallet.bean.NotificationMsg
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.bean.htdf.tx.Htdftx
import com.yjy.wallet.utils.TimeUtils
import com.yjy.wallet.wallet.WaltType
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_success.*
import org.json.JSONObject
import org.litepal.LitePal
import retrofit2.HttpException


/**
 *Created by weiweiyu
 *on 2019/5/10
 * 交易详情
 */
class HtdfTxInfoActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_success

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var txinfo = intent.getSerializableExtra("data") as TxItem
        tv_price.text = intent.getStringExtra("type") + Utils.toSubStringDegistForChart(
                txinfo.amount!!.replace(",", "").toDouble(),
                Constant.priceP,
                false
        ) + " " + txinfo.denom!!.toUpperCase()
        var content = ContentValues()
        content.put("read", 1)
        LitePal.updateAllAsync(NotificationMsg::class.java, content, "txid = ?", txinfo.Hash?.toLowerCase()).listen {

        }
        tv_change.setOnClickListener {
            var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", txinfo.Hash?.toLowerCase())
            s.setPrimaryClip(mClipData)
            toast(resources.getString(R.string.receive_copy_toast))
        }
        tv_from.setOnClickListener {
            var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", txinfo.From)
            s.setPrimaryClip(mClipData)
            toast(resources.getString(R.string.receive_copy_toast))
        }
        tv_to.setOnClickListener {
            var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", txinfo.To)
            s.setPrimaryClip(mClipData)
            toast(resources.getString(R.string.receive_copy_toast))
        }
        tv_from.text = txinfo.From
        tv_to.text = txinfo.To
        tv_remark.text = txinfo.msg
        tv_change.text = txinfo.Hash
        iv_icon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.waite_icon, 0, 0)
        tv_qu_txt.visibility = View.GONE
        tv_qu.visibility = View.GONE
        iv_icon.text = resources.getString(R.string.success_title2)
        showProgressDialog("")
        HTDFService().gettx(txinfo.Hash.toString())
                .compose(bindToLifecycle())
                .subscribe(object : ResourceSubscriber<Htdftx>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: Htdftx?) {
                        txinfo.Height = t?.height
                        var isSuccess = false
                        if (t?.logs!!.isNotEmpty()) {
                            isSuccess = t.logs[0].success
                        }
                        if (isSuccess) {
                            txinfo.state = 1
                            tv_qu_txt.visibility = View.VISIBLE
                            tv_qu.visibility = View.VISIBLE
                            iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    R.mipmap.success_icon,
                                    0,
                                    0
                            )
                            iv_icon.text = resources.getString(R.string.success_title)
                            HTDFService().getBlockLatest()
                                    .compose(bindToLifecycle())
                                    .subscribe(object : ResourceSubscriber<String>() {
                                        override fun onComplete() {
                                        }

                                        override fun onNext(t: String) {
                                            var json = JSONObject(t)
                                            if (json.has("block_meta")) {
                                                var blockJson = json.getJSONObject("block_meta")
                                                if (blockJson.has("header")) {
                                                    val headerJson = blockJson.getJSONObject("header")
                                                    if (headerJson.has("height")) {
                                                        txinfo.comfirm = (headerJson.getString("height").toInt() - txinfo.Height!!.toInt())
                                                        txinfo.saveOrUpdate("Hash = ? and denom = ?", txinfo.Hash?.toLowerCase(), txinfo.denom)
                                                        tv_qu.text = txinfo.Height + "(${txinfo.comfirm}${resources.getString(R.string.str_confirm)})"
                                                    }
                                                }
                                            }
                                        }

                                        override fun onError(t: Throwable?) {
                                        }
                                    })
                        } else {
                            txinfo.state = 2
                            iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    R.mipmap.fail_icon,
                                    0,
                                    0
                            )
                            tv_qu_txt.visibility = View.GONE
                            tv_qu.visibility = View.GONE
                            iv_icon.text = resources.getString(R.string.success_title3)
                        }
                        try {
                            tv_qu.text = txinfo.Height
                            txinfo.fee = t.gas_used.toLong().times(t.tx.value.fee.gas_price.toLong())
                            tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toNormal((txinfo.fee.toDouble() / 100000000)).toString() + WaltType.htdf.name.toUpperCase()
                        } catch (e: Exception) {

                        }
                        txinfo.saveOrUpdate("Hash = ? and denom = ?", txinfo.Hash?.toLowerCase(), txinfo.denom)
                        dismissProgressDialog()
                    }

                    override fun onError(t: Throwable?) {
                        dismissProgressDialog()
                        if (t is HttpException) {
                            try {
                                val body = t.response().errorBody()
                                if (body?.string()!!.contains(txinfo.Hash!!.toUpperCase())) {
                                    iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                            0,
                                            R.mipmap.waite_icon,
                                            0,
                                            0
                                    )
                                    tv_qu_txt.visibility = View.GONE
                                    tv_qu.visibility = View.GONE
                                    iv_icon.text = resources.getString(R.string.success_title2)
                                }
                            } catch (e: Exception) {

                            }
                        }
                    }
                })

        when (txinfo.state) {
            2 -> {
                iv_icon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.fail_icon, 0, 0)
                iv_icon.text = resources.getString(R.string.success_title3)
            }
            else -> {
                if (txinfo.Height != "0") {
                    tv_qu_txt.visibility = View.VISIBLE
                    tv_qu.visibility = View.VISIBLE
                    tv_qu.text = txinfo.Height
                    iv_icon.text = resources.getString(R.string.success_title)
                } else {
                    iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.mipmap.waite_icon,
                            0,
                            0
                    )
                    iv_icon.text = resources.getString(R.string.success_title2)
                }

            }
        }
        tv_fee.visibility = View.VISIBLE
        tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toSubStringDegistForChart(
                txinfo.fee.toDouble() / 100000000,
                8,
                false
        ) + WaltType.htdf.name.toUpperCase()
        tv_go_block.visibility = View.VISIBLE
        tv_go_block.setOnClickListener {
            val intent = Intent(this, WebActivity1::class.java)
            when (txinfo.denom) {
                WaltType.htdf.name -> {
                    intent.putExtra("url", "http://www.htdfscan.com/#/deal_hash?tradehash=${txinfo.Hash}")
                }
                else -> {
                    intent.putExtra("url", "http://www.htdfscan.com/#/deal_hash?tradehash=${txinfo.Hash}")
                }
            }
            intent.putExtra("title", resources.getString(R.string.str_title_block))
            startActivity(intent)
        }

        tv_time.text = TimeUtils.stampToDate(txinfo.Time)
        Flowable.just(QRCodeUtil.createQRCode(txinfo.Hash?.toLowerCase(), resources.getDimensionPixelSize(R.dimen.dp100)))
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