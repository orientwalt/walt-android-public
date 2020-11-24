package com.yjy.wallet.activity.eth

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.BLog
import com.weiyu.baselib.util.QRCodeUtil
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.WebActivity1
import com.yjy.wallet.api.TokenSendService
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.utils.TimeUtils
import com.yjy.wallet.wallet.WaltType
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_success.*
import org.web3j.utils.Convert


/**
 *Created by weiweiyu
 *on 2019/5/10
 * 交易详情
 */
class ETHTxInfoActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_success

    @SuppressLint("CheckResult")
    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var txinfo = intent.getSerializableExtra("data") as TxItem
        tv_price.text = intent.getStringExtra("type") + Utils.toSubStringDegistForChart(
                txinfo.amount!!.toDouble(),
                Constant.priceP,
                false
        ) + " " + txinfo.denom!!.toUpperCase()
        tv_change.setOnClickListener {
            var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", txinfo.Hash?.toLowerCase())
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


        if ((System.currentTimeMillis() - txinfo.Time) > 2 * 60 * 1000 || txinfo.state == 1) {//5五分
            showProgressDialog("")
            TokenSendService().inpending(txinfo.Hash.toString(), try {
                if (WaltType.valueOf(txinfo.denom!!) == WaltType.USDT) {
                    WaltType.ETH
                } else {
                    WaltType.valueOf(txinfo.denom!!)
                }
            } catch (e: Exception) {
                WaltType.ETH
            })
                    .compose(bindToLifecycle())
                    .subscribe({
                        showProgressDialog("")
                        if (it.code == 1) {
                            txinfo.state = 0
                            iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    R.mipmap.waite_icon,
                                    0,
                                    0
                            )
                            tv_qu_txt.visibility = View.GONE
                            tv_qu.visibility = View.GONE
                            iv_icon.text = resources.getString(R.string.success_title2)
                            txinfo.saveOrUpdate("Hash = ?  and denom = ?", txinfo.Hash?.toLowerCase(), txinfo.denom)
                        } else {
                            showProgressDialog("")
                            TokenSendService().tx(txinfo.Hash.toString(), try {
                                if (WaltType.valueOf(txinfo.denom!!) == WaltType.USDT) {
                                    WaltType.ETH
                                } else {
                                    WaltType.valueOf(txinfo.denom!!)
                                }
                            } catch (e: Exception) {
                                WaltType.ETH
                            })
                                    .compose(bindToLifecycle())
                                    .subscribe({
                                        dismissProgressDialog()
                                        if (it.code == 1) {
                                            tv_qu.text = txinfo.Height
                                            tv_qu.visibility = View.VISIBLE
                                            tv_qu_txt.visibility = View.VISIBLE
                                            tv_fee.visibility = View.VISIBLE
                                            tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + it.data!!.fee + try {
                                                WaltType.valueOf(txinfo.denom!!)
                                            } catch (e: Exception) {
                                                WaltType.ETH
                                            }
                                            if (it.data?.confirmations!! > 0) {
                                                if (it.data?.toIsContract == 1 && !TextUtils.isEmpty(it.data?.traceErr)) {
                                                    BLog.d("-----------------------------------2")
                                                    txinfo.state = 2
                                                    txinfo.saveOrUpdate(
                                                            "Hash = ?  and denom = ?",
                                                            txinfo.Hash.toString().toLowerCase(), txinfo.denom
                                                    )
                                                    iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                            0,
                                                            R.mipmap.fail_icon,
                                                            0,
                                                            0
                                                    )
                                                    tv_qu_txt.visibility = View.GONE
                                                    tv_qu.visibility = View.GONE
                                                    iv_icon.text = it.data?.traceErr
                                                } else {
                                                    tv_qu.text = it.data!!.height.toString() + "(${it.data!!.confirmations}${resources.getString(R.string.str_confirm)})"
                                                    iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                            0,
                                                            R.mipmap.success_icon,
                                                            0,
                                                            0
                                                    )
                                                    iv_icon.text = resources.getString(R.string.success_title)
                                                    txinfo.state = 1
                                                }
                                            } else {
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
                                        } else {
                                            BLog.d("-----------------------------------22")
                                            txinfo.state = 2
                                            txinfo.saveOrUpdate(
                                                    "Hash = ?  and denom = ?",
                                                    txinfo.Hash.toString().toLowerCase(), txinfo.denom
                                            )
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
                                        txinfo.saveOrUpdate("Hash = ?  and denom = ?", txinfo.Hash?.toLowerCase(), txinfo.denom!!)
                                    }, {
                                        dismissProgressDialog()
                                    })
                        }
                    }, {
                        dismissProgressDialog()
                    })

        }
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
        if (txinfo.fee > 0) {
            tv_fee.visibility = View.VISIBLE
            tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toNormal(
                    Convert.fromWei(
                            txinfo?.fee.toString(),
                            Convert.Unit.GWEI
                    ).toString()
            ) + if (txinfo.denom == WaltType.ETC.name) WaltType.ETC.name else WaltType.ETH.name
        }

        tv_go_block.visibility = View.VISIBLE
        tv_go_block.setOnClickListener {
            val intent = Intent(this, WebActivity1::class.java)
            intent.putExtra("url", "https://tokenview.com/en/search/${txinfo.Hash}")
            intent.putExtra("title", resources.getString(R.string.str_title_block))
            startActivity(intent)
        }

        tv_time.text = TimeUtils.stampToDate(txinfo.Time!!)
        Flowable.just(QRCodeUtil.createQRCode(txinfo.Hash, resources.getDimensionPixelSize(R.dimen.dp100)))
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