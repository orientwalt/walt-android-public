package com.yjy.wallet.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.google.gson.Gson
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.BLog
import com.weiyu.baselib.util.QRCodeUtil
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.R
import com.yjy.wallet.api.USDPService
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.bean.htdf.AccountInfo
import com.yjy.wallet.bean.htdf.Send
import com.yjy.wallet.bean.params.TransationsParams
import com.yjy.wallet.chainutils.usdp.UsdpTransaction
import com.yjy.wallet.utils.TimeUtils
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_success.*
import org.bitcoinj.core.Coin

/**
 *Created by weiweiyu
 *on 2019/5/10
 * 交易成功页面
 */
class SuccessActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_success
    override fun initializeContentViews() {
        tb_title.setLeftLayoutVisibility(View.INVISIBLE)
        tb_title.setRightText(resources.getString(R.string.words_sure_finish))
        if (intent.getIntExtra("type", 0) == 1) {
            tv_back.visibility = View.VISIBLE
        }
        tb_title.setRightLayoutClickListener(View.OnClickListener { finish() })
        val date3 = intent.getSerializableExtra("data3") as WInfo
        var hax = ""
        when (date3.unit) {
            WaltType.usdp.name, WaltType.HET.name -> {
                val data1 =
                        Gson().fromJson<UsdpTransaction>(intent.getStringExtra("data1"), UsdpTransaction::class.java)
                val data2 = intent.getSerializableExtra("data2") as Send
                MyWalletUtils.instance.updateCheckInfo(date3)
                hax = data2.txhash
                //重新获取账号信息
                USDPService(date3.unit)
                        .getAccount(date3.address)
                        .compose(bindToLifecycle())
                        .subscribe(object : ResourceSubscriber<AccountInfo>() {
                            override fun onComplete() {
                            }

                            override fun onNext(data: AccountInfo) {
                                if (data.value.coins!!.isNotEmpty()) {
                                    var amount = 0.0
                                    for (b in data.value.coins) {
                                        amount = amount.plus(b.amount.toDouble())
                                    }
                                    date3.balance = amount.toString()

                                }
                                date3.sequence = data.value.sequence
                                date3.account_number = data.value.account_number
                                MyWalletUtils.instance.updateCheckInfo(date3)
                            }

                            override fun onError(t: Throwable) {
                                BLog.d(t.message.toString())
                            }
                        })

                tv_change.setOnClickListener {
                    var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val mClipData = ClipData.newPlainText("Label", data2.txhash)
                    s.setPrimaryClip(mClipData)
                    toast(resources.getString(R.string.receive_copy_toast))
                }
                var txitem = TxItem(
                        data2.height,
                        data2.txhash.toLowerCase(),
                        data1.value.msg[0].value.From,
                        data1.value.msg[0].value.To,
                        intent.getStringExtra("price"),
                        date3.unit,
                        data1.value.memo,
                        System.currentTimeMillis(),
                        main
                )
                txitem.fee = Constant.feeLimit.toLong()
                txitem.saveOrUpdate("Hash = ?", txitem.Hash!!.toLowerCase())
                set(txitem)
                tv_fee.visibility = View.VISIBLE
                tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toSubStringDegistForChart(
                        txitem.fee.toDouble() / 100000000,
                        8,
                        false
                ) + txitem.denom?.toUpperCase()
                //获取一下转账记录
                USDPService(date3.unit)
                        .transactions(TransationsParams(date3.address, date3.hight, 0, 0))
                        .compose(bindToLifecycle())
                        .map {
                            for (i in it.ArrTx!!.indices) {
                                var item = it.ArrTx[i]
                                if (item.Hash.toLowerCase() == data2.txhash.toLowerCase()) {
                                    txitem.Time = TimeUtils.dateToStamp(item.Time)
                                    txitem.Height = item.Height
                                    txitem.saveOrUpdate("Hash = ?", item.Hash.toLowerCase())
                                }
                            }
                            txitem
                        }
                        .subscribe(object : ResourceSubscriber<TxItem>() {
                            override fun onComplete() {
                            }

                            override fun onNext(t: TxItem?) {
                                set(t!!)
                            }

                            override fun onError(t: Throwable?) {
                                txitem.saveOrUpdate("Hash = ?", data2.txhash.toLowerCase())
                                set(txitem)
                            }

                        })
            }
            else -> {
                var txinfo = intent.getSerializableExtra("tx") as TxItem
                hax = txinfo.Hash!!
                set(txinfo)
            }
        }
        Flowable.just(QRCodeUtil.createQRCode(hax, resources.getDimensionPixelSize(R.dimen.dp100)))
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

    fun set(txinfo: TxItem) {
        tv_price.text = "-" + Utils.toSubStringDegistForChart(
                txinfo.amount!!.toDouble(),
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
        if (txinfo.fee > 0 && txinfo.denom == "BTC") {
            tv_fee.visibility = View.VISIBLE
            tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Coin.valueOf(txinfo.fee).toFriendlyString()
        }
        tv_time.text = TimeUtils.stampToDate(txinfo.Time)
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
        tv_back.setOnClickListener { startActivity(MainActivity::class.java) }
    }

}