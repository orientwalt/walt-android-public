package com.yjy.wallet.activity.utxo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import com.jaeger.library.StatusBarUtil
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.QRCodeUtil
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.WebActivity1
import com.yjy.wallet.api.CXCService
import com.yjy.wallet.api.QtumService
import com.yjy.wallet.api.TokenSendService
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.bean.cxc.CTxInfo
import com.yjy.wallet.bean.qtum.ItemsItem
import com.yjy.wallet.utils.TimeUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_tx_btc.*
import org.bitcoinj.core.Coin


/**
 *Created by weiweiyu
 *on 2019/5/10
 * 交易详情
 */
class BTCTxInfoActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_tx_btc

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
//        tb_title.setTitle(resources.getString(R.string.tx_info_title))
        tb_title.setTitleTextColor(R.color.txt_333333)
        val txinfo = intent.getSerializableExtra("data") as TxItem
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
        if (txinfo.fee > 0L) {
            when (txinfo.denom) {
                WaltType.USDT.name -> {
                    tv_fee.visibility = View.VISIBLE
                    tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Coin.valueOf(txinfo.fee).toFriendlyString()
                }
                WaltType.CXC.name -> {
                    tv_fee.visibility = View.VISIBLE
                    tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Coin.valueOf(txinfo.fee * 100).toPlainString() + txinfo.denom
                }
                else -> {
                    tv_fee.visibility = View.VISIBLE
                    tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Coin.valueOf(txinfo.fee).toPlainString() + txinfo.denom
                }
            }

        }
        iv_icon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.waite_icon, 0, 0)
        iv_icon.text = resources.getString(R.string.success_title2)
        if (txinfo.state == 2) {
            iv_icon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.fail_icon, 0, 0)
            iv_icon.text = resources.getString(R.string.success_title3)
        } else if (txinfo.state == 1) {
            iv_icon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.success_icon, 0, 0)
            iv_icon.text = resources.getString(R.string.success_title)
        }
        if (txinfo.Height == "0" || TextUtils.isEmpty(txinfo.Height)) {
            tv_qu_txt.visibility = View.GONE
            tv_qu.visibility = View.GONE
        } else {
            tv_qu_txt.visibility = View.VISIBLE
            tv_qu.visibility = View.VISIBLE
        }
        tv_qu.text = txinfo.Height
        val wallet = intent.getSerializableExtra("walt") as WInfo

        when (txinfo.denom) {
            WaltType.BSV.name, WaltType.BCH.name, WaltType.BTC.name, WaltType.LTC.name, WaltType.DASH.name, WaltType.USDT.name -> {
                if ((System.currentTimeMillis() - txinfo.Time) > 2 * 60 * 1000 || txinfo.state == 1) {
                    showProgressDialog("")
                    TokenSendService().inpending(txinfo.Hash.toString(), WaltType.valueOf(wallet.unit))
                            .compose(bindToLifecycle())
                            .subscribe({
                                dismissProgressDialog()
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
                                    txinfo.saveOrUpdate("Hash = ?  and denom = ?", txinfo.Hash?.toLowerCase(), wallet.unit)
                                } else {
                                    showProgressDialog("")
                                    TokenSendService().utx(txinfo.Hash.toString(), WaltType.valueOf(wallet.unit))
                                            .compose(bindToLifecycle())
                                            .subscribe({
                                                dismissProgressDialog()
                                                if (it.code == 1) {
                                                    tv_qu.text = txinfo.Height
                                                    tv_qu.visibility = View.VISIBLE
                                                    tv_qu_txt.visibility = View.VISIBLE
                                                    tv_fee.visibility = View.VISIBLE
                                                    tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + it.data!!.fee + if (txinfo.denom == WaltType.USDT.name) WaltType.BTC.name else txinfo.denom!!
                                                    tv_qu.text = it.data!!.height.toString() + "(${it.data!!.confirmations}${resources.getString(R.string.str_confirm)})"
                                                    if (it.data?.confirmations!! >= 6) {
                                                        iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                                0,
                                                                R.mipmap.success_icon,
                                                                0,
                                                                0
                                                        )
                                                        iv_icon.text = resources.getString(R.string.success_title)
                                                        txinfo.state = 1
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
                                                txinfo.saveOrUpdate("Hash = ?  and denom = ?", txinfo.Hash?.toLowerCase(), wallet.unit)
                                            }, {
                                                dismissProgressDialog()
                                            })
                                }
                            }, {
                                dismissProgressDialog()
                            })
                }

            }
            else -> {
                if ((System.currentTimeMillis() - txinfo.Time) > 5 * 60 * 1000 || txinfo.state == 1) {//5五分钟之后才可以确认
                    showProgressDialog("")
                    when (txinfo.denom) {
                        WaltType.CXC.name -> {
                            CXCService().showdeal(txinfo.Hash.toString())
                                    .compose(bindToLifecycle())
                                    .subscribe(object : ResourceSubscriber<CTxInfo>() {
                                        override fun onComplete() {
                                        }

                                        override fun onNext(t: CTxInfo) {
                                            dismissProgressDialog()
                                            if (TextUtils.isEmpty(t.error)) {
                                                if (TextUtils.isEmpty(t.result.blockhash)) {
                                                    txinfo.state = 2
                                                } else {
                                                    txinfo.state = 1
                                                    txinfo.comfirm = t.result.confirmations.toInt()
                                                    tv_qu.text = txinfo.Height + "(${txinfo.comfirm}${resources.getString(R.string.str_confirm)})"
                                                }
                                            } else {
                                                txinfo.state = 2
                                            }
                                            when (txinfo.state) {
                                                2 -> {
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
                                                1 -> {
                                                    iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                            0,
                                                            R.mipmap.success_icon,
                                                            0,
                                                            0
                                                    )
                                                    iv_icon.text = resources.getString(R.string.success_title)
                                                }
                                                0 -> {
                                                    iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                            0,
                                                            R.mipmap.waite_icon,
                                                            0,
                                                            0
                                                    )
                                                    iv_icon.text = resources.getString(R.string.success_title2)
                                                }
                                            }
                                            txinfo.saveOrUpdate(
                                                    "Hash = ?  and denom = ?",
                                                    txinfo.Hash.toString().toLowerCase(), txinfo.denom
                                            )
                                        }

                                        override fun onError(t: Throwable?) {
                                            dismissProgressDialog()
                                        }
                                    })
                        }
                        WaltType.QTUM.name -> {
                            QtumService.instance.tx(txinfo.Hash.toString())
                                    .compose(bindToLifecycle())
                                    .subscribe(object : ResourceSubscriber<ItemsItem>() {
                                        override fun onComplete() {
                                        }

                                        override fun onNext(t: ItemsItem) {
                                            txinfo.Height = t.blockheight.toString()
                                            if (t.confirmations < 1) {
                                                txinfo.state = 0
                                            } else {
                                                txinfo.state = 1
                                                txinfo.comfirm = t.confirmations
                                                tv_qu.text = txinfo.Height + "(${txinfo.comfirm}${resources.getString(R.string.str_confirm)})"
                                            }
                                            when (txinfo.state) {
                                                2 -> {
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
                                                1 -> {
                                                    iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                            0,
                                                            R.mipmap.success_icon,
                                                            0,
                                                            0
                                                    )
                                                    iv_icon.text = resources.getString(R.string.success_title)
                                                }
                                                0 -> {
                                                    iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                            0,
                                                            R.mipmap.waite_icon,
                                                            0,
                                                            0
                                                    )
                                                    iv_icon.text = resources.getString(R.string.success_title2)
                                                }
                                            }
                                            txinfo.saveOrUpdate(
                                                    "Hash = ?  and denom = ?",
                                                    txinfo.Hash.toString().toLowerCase(), txinfo.denom
                                            )
                                            dismissProgressDialog()
                                        }

                                        override fun onError(t: Throwable?) {
                                            iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                    0,
                                                    R.mipmap.fail_icon,
                                                    0,
                                                    0
                                            )
                                            tv_qu_txt.visibility = View.GONE
                                            tv_qu.visibility = View.GONE
                                            iv_icon.text = resources.getString(R.string.success_title3)
                                            txinfo.state = 2
                                            txinfo.saveOrUpdate(
                                                    "Hash = ?  and denom = ?",
                                                    txinfo.Hash.toString().toLowerCase(), txinfo.denom)
                                            dismissProgressDialog()
                                        }
                                    })
                        }

                    }
                }
            }
        }

        tv_go_block.visibility = View.VISIBLE
        tv_go_block.setOnClickListener {
            val intent = Intent(this, WebActivity1::class.java)
            when (txinfo.denom) {
                WaltType.DASH.name, WaltType.LTC.name, WaltType.BTC.name, WaltType.BSV.name, WaltType.BCH.name, WaltType.USDT.name -> {
                    intent.putExtra("url", "https://tokenview.com/en/search/${txinfo.Hash}")
                }
                WaltType.QTUM.name -> {
                    var s = if (Constant.main) "https://explorer.qtum.org/tx/${txinfo.Hash}/" else "https://testnet.qtum.org/tx/${txinfo.Hash}"
                    intent.putExtra("url", s)
                }
            }
            intent.putExtra("title", resources.getString(R.string.str_title_block))
            startActivity(intent)
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
        tv_time.text = TimeUtils.stampToDate(txinfo.Time!!)
        if (!TextUtils.isEmpty(txinfo.Hash))
            Flowable.just(QRCodeUtil.createQRCode(txinfo.Hash, resources.getDimensionPixelSize(R.dimen.dp100)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle())
                    .subscribe(
                            object : ResourceSubscriber<Bitmap>() {
                                override fun onComplete() {
                                }

                                override fun onNext(t: Bitmap?) {
                                    iv_code.setImageBitmap(t)
                                }

                                override fun onError(t: Throwable?) {
                                }
                            })


    }

    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setLightMode(this)
        StatusBarUtil.setColor(this, resources.getColor(R.color.white), 0)

    }
}