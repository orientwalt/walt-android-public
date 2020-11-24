package com.yjy.wallet.activity.other

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.QRCodeUtil
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.R
import com.yjy.wallet.activity.WebActivity1
import com.yjy.wallet.api.*
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.bean.htdf.TxRep
import com.yjy.wallet.bean.neo.NEOTxInfo
import com.yjy.wallet.bean.trx.rpc.RpcTRXTXInfo
import com.yjy.wallet.bean.xrp.XRPTx
import com.yjy.wallet.utils.Error
import com.yjy.wallet.utils.TimeUtils
import com.yjy.wallet.wallet.WaltType
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_success.*
import org.json.JSONObject
import org.web3j.utils.Convert
import retrofit2.HttpException
import java.math.BigDecimal


/**
 *Created by weiweiyu
 *on 2019/5/10
 * 交易详情
 */
class TxInfoActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_success

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
        if ((System.currentTimeMillis() - txinfo.Time) > 5 * 60 * 1000 || txinfo.state == 1) {
            showProgressDialog("")
            when (txinfo.denom!!.toUpperCase()) {
                "USDP", "HET" -> {
                    USDPService(txinfo.denom!!).gettx(txinfo.Hash.toString())
                            .compose(bindToLifecycle())
                            .subscribe(object : ResourceSubscriber<TxRep>() {
                                override fun onComplete() {
                                }

                                override fun onNext(t: TxRep?) {
                                    txinfo.Height = t?.height
                                    var isSuccess = false
                                    if (t?.log!!.size > 0) {
                                        isSuccess = t.log[0].success
                                    }
                                    if (isSuccess) {
                                        tv_qu_txt.visibility = View.VISIBLE
                                        tv_qu.visibility = View.VISIBLE
                                        iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                0,
                                                R.mipmap.success_icon,
                                                0,
                                                0
                                        )
                                        iv_icon.text = resources.getString(R.string.success_title)
                                    } else {
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
                                    txinfo.state = 1
                                    txinfo.saveOrUpdate("Hash = ?", txinfo.Hash.toString().toLowerCase())
                                    tv_qu.text = txinfo.Height
                                    USDPService(txinfo.denom!!).getBlockLatest()
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
                                                                txinfo.saveOrUpdate("Hash = ?", txinfo.Hash.toString().toLowerCase())
                                                                tv_qu.text = txinfo.Height + "(${txinfo.comfirm}${resources.getString(R.string.str_confirm)})"
                                                            }
                                                        }
                                                    }
                                                }

                                                override fun onError(t: Throwable?) {
                                                }
                                            })
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
                }
                WaltType.TRX.name -> {
                    TRXService.instance.tx(txinfo.Hash.toString())
                            .compose(bindToLifecycle())
                            .subscribe(object : ResourceSubscriber<RpcTRXTXInfo>() {
                                override fun onComplete() {
                                }

                                override fun onNext(t: RpcTRXTXInfo) {
                                    dismissProgressDialog()
                                    txinfo.Height = t.blockNumber.toString()
                                    tv_qu_txt.visibility = View.VISIBLE
                                    tv_qu.visibility = View.VISIBLE
                                    tv_qu.text = txinfo.Height
                                    txinfo.saveOrUpdate("Hash = ?", txinfo.Hash?.toLowerCase())
                                    tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toNormal((txinfo.fee.toDouble() / 1000000)).toString() + WaltType.XRP.name
                                    dismissProgressDialog()
                                    if (txinfo.state == 2) {
                                        iv_icon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.fail_icon, 0, 0)
                                        iv_icon.text = resources.getString(R.string.success_title3)
                                    } else {
                                        iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                0,
                                                R.mipmap.success_icon,
                                                0,
                                                0
                                        )
                                        iv_icon.text = resources.getString(R.string.success_title)
                                    }
                                }

                                override fun onError(t: Throwable?) {
                                    dismissProgressDialog()
                                }
                            })

                }
                WaltType.NEO.name -> {
                    NEOService.instance.tx(txinfo.Hash.toString())
                            .compose(bindToLifecycle())
                            .subscribe(object : ResourceSubscriber<NEOTxInfo>() {
                                override fun onComplete() {
                                }

                                override fun onNext(t: NEOTxInfo) {
                                    if (t.errors == null) {
                                        txinfo.Height = t.block_height.toString()
                                        txinfo.state = 1
                                    } else {
                                        txinfo.state = 2
                                    }
                                    txinfo.saveOrUpdate("Hash = ?", txinfo.Hash?.toLowerCase())
                                    if (txinfo.state == 2) {
                                        iv_icon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.fail_icon, 0, 0)
                                        iv_icon.text = resources.getString(R.string.success_title3)
                                    } else {
                                        iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                0,
                                                R.mipmap.success_icon,
                                                0,
                                                0
                                        )
                                        iv_icon.text = resources.getString(R.string.success_title)
                                    }
                                    dismissProgressDialog()
                                }

                                override fun onError(t: Throwable?) {
                                    dismissProgressDialog()
                                }
                            })
                }
                WaltType.XRP.name -> {
                    XRPService.instance.tx(txinfo.Hash.toString())
                            .compose(bindToLifecycle())
                            .subscribe(object : ResourceSubscriber<XRPTx>() {
                                override fun onComplete() {
                                }

                                override fun onNext(t: XRPTx) {
                                    txinfo.Height = t.result.ledger_index.toString()
                                    txinfo.fee = t.result.Fee.toLong()
                                    txinfo.state = if (t.result.validated) 1 else 2
                                    txinfo.saveOrUpdate("Hash = ?", txinfo.Hash?.toLowerCase())
                                    tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toNormal((txinfo.fee.toDouble() / 1000000)).toString() + WaltType.XRP.name
                                    dismissProgressDialog()
                                    if (txinfo.state == 2) {
                                        iv_icon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.fail_icon, 0, 0)
                                        iv_icon.text = resources.getString(R.string.success_title3)
                                    } else {
                                        iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                                0,
                                                R.mipmap.success_icon,
                                                0,
                                                0
                                        )
                                        iv_icon.text = resources.getString(R.string.success_title)
                                    }
                                }

                                override fun onError(t: Throwable?) {
                                    dismissProgressDialog()
                                }
                            })
                }
                WaltType.XLM.name -> {
                    XLMService().getTx(txinfo.Hash.toString())
                            .compose(bindToLifecycle())
                            .subscribe({
                                dismissProgressDialog()
//                                val bytes: ByteArray = BaseEncoding.base64().decode(it.envelopeXdr)
//                                var t = TransactionEnvelope.decode(XdrDataInputStream(ByteArrayInputStream(bytes)))
//                                var info = t.v1.tx.operations[0].body.createAccountOp
//                                var publicKey = PublicKey()
//                                publicKey.ed25519 = info.destination.accountID.ed25519
//                                val tokenDecimal = BigDecimal.TEN.pow(7)
//                                val bigDecimal = BigDecimal(info.startingBalance.int64).divide(tokenDecimal)
                                iv_icon.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        R.mipmap.success_icon,
                                        0,
                                        0
                                )
                                txinfo.fee = it.feeCharged
                                val tokenDecimal = BigDecimal.TEN.pow(7)
                                val bigDecimal = BigDecimal(txinfo.fee).divide(tokenDecimal)
                                if(bigDecimal.toDouble()>0) {
                                    tv_fee.visibility = View.VISIBLE
                                    tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toSubStringDegistForChartStr(
                                            bigDecimal.toString(),
                                            7,
                                            false
                                    ) + txinfo.denom?.toUpperCase()
                                }
                                txinfo.Height = it.ledger.toString()
                                txinfo.state = if (it.isSuccessful) 1 else 2
                                iv_icon.text = resources.getString(R.string.success_title)
                                tv_qu_txt.visibility = View.VISIBLE
                                tv_qu.visibility = View.VISIBLE
                                tv_qu.text = txinfo.Height
                                txinfo.saveOrUpdate("Hash = ?  and denom = ?", txinfo.Hash.toString().toLowerCase(), WaltType.XLM.name)
                            }, {
                                dismissProgressDialog()
                                if (it is HttpException && it.code() == 404) {
                                    txinfo.state = 2
                                    iv_icon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.fail_icon, 0, 0)
                                    iv_icon.text = resources.getString(R.string.success_title3)
                                    txinfo.saveOrUpdate("Hash = ?  and denom = ?", txinfo.Hash.toString().toLowerCase(), WaltType.XLM.name)
                                } else {
                                    Error.error(it, this@TxInfoActivity)
                                }
                            })
                }
                else -> {

                }
            }
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
        when (txinfo.denom!!.toUpperCase()) {
            "USDP", "HET" -> {
                tv_fee.visibility = View.VISIBLE
                tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toSubStringDegistForChart(
                        txinfo.fee.toDouble() / 100000000,
                        8,
                        false
                ) + txinfo.denom?.toUpperCase()
            }
            WaltType.TRX.name, WaltType.NEO.name -> {
                tv_fee.visibility = View.GONE
            }
            WaltType.XLM.name -> {
                if (txinfo.fee > 0) {
                    val tokenDecimal = BigDecimal.TEN.pow(7)
                    val bigDecimal = BigDecimal(txinfo.fee).divide(tokenDecimal)
                    tv_fee.visibility = View.VISIBLE
                    tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toSubStringDegistForChartStr(
                            bigDecimal.toString(),
                            7,
                            false
                    ) + txinfo.denom?.toUpperCase()
                }
            }
            WaltType.XRP.name -> {
                if (txinfo?.fee > 0) {
                    tv_fee.visibility = View.VISIBLE
                    tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toNormal((txinfo.fee.toDouble() / 1000000)).toString() + WaltType.XRP.name
                }
            }
            else -> {
                if (txinfo.From!!.startsWith("htdf")) {
                    tv_fee.visibility = View.VISIBLE
                    tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toSubStringDegistForChart(
                            txinfo.fee.toDouble() / 100000000,
                            8,
                            false
                    ) + "HTDF"
                } else {
                    if (txinfo?.fee > 0) {
                        tv_fee.visibility = View.VISIBLE
                        tv_fee.text = resources.getString(R.string.send_fee_txt) + ":" + Utils.toNormal(
                                Convert.fromWei(
                                        txinfo?.fee.toString(),
                                        Convert.Unit.GWEI
                                ).toString()
                        ) + WaltType.ETH.name
                    }
                }
            }
        }
        tv_go_block.visibility = View.VISIBLE
        tv_go_block.setOnClickListener {
            val intent = Intent(this, WebActivity1::class.java)
            when (txinfo.denom) {
                WaltType.XLM.name -> {
                    intent.putExtra("url", " https://stellarchain.io/tx/${txinfo.Hash}")
                }
                WaltType.usdp.name -> {
                    intent.putExtra("url", "http://www.usdpscan.io/#/deal_hash?tradehash=${txinfo.Hash}")
                }
                WaltType.htdf.name -> {
                    intent.putExtra("url", "http://www.htdfscan.com/#/deal_hash?tradehash=${txinfo.Hash}")
                }
                WaltType.HET.name -> {
                    intent.putExtra("url", "http://www.hetbiscan.com/#/deal_hash?tradehash=${txinfo.Hash}")
                }
                WaltType.XRP.name -> {
                    intent.putExtra("url", "https://livenet.xrpl.org/transactions/${txinfo.Hash}/simple")
                }
                WaltType.TRX.name -> {
                    if (main)
                        intent.putExtra("url", "https://tronscan.org/#/transaction/${txinfo.Hash}")
                    else
                        intent.putExtra("url", "https://shasta.tronscan.org/#/transaction/${txinfo.Hash}")
                }
                WaltType.NEO.name -> {
                    if (main)
                        intent.putExtra("url", "https://neoscan.io/transaction/${txinfo.Hash}")
                    else
                        intent.putExtra("url", "https://neoscan-testnet.io/transaction/${txinfo.Hash}")
                }
                WaltType.EOS.name -> {
                    intent.putExtra("url", "https://eospark.com/tx/${txinfo.Hash}")
                }
                else -> {
                    if (txinfo.From!!.startsWith("htdf")) {
                        intent.putExtra("url", "http://www.htdfscan.com/#/deal_hash?tradehash=${txinfo.Hash}")
                    } else
                        intent.putExtra("url", "https://cn.etherscan.com/tx/${txinfo.Hash}")
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