package com.yjy.wallet.activity.htdf

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.weiyu.baselib.base.ActivityManager
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.AesUtils
import com.weiyu.baselib.util.ImageLoaderManager
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.api.HTDFService
import com.yjy.wallet.api.TxService
import com.yjy.wallet.bean.TxItem
import com.yjy.wallet.bean.htdftx.NodeItem
import com.yjy.wallet.bean.params.BroadCast
import com.yjy.wallet.chainutils.htdf.HtdfSign
import com.yjy.wallet.utils.Error
import com.yjy.wallet.wallet.KeyStoneUtil
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import com.zhl.cbdialog.CBDialogBuilder
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_htdf_reward.*
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Utils
import org.json.JSONObject
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.utils.Numeric

/**
 * weiweiyu
 * 2020/3/6
 * 575256725@qq.com
 * 13115284785
 */
class HtdfRewardActivity : BaseActivity() {
    var item: NodeItem? = null
    var wallet: WInfo? = null
    override fun getContentLayoutResId(): Int = R.layout.activity_htdf_reward

    @SuppressLint("CheckResult")
    override fun initializeContentViews() {
        wallet = intent.getSerializableExtra("data") as WInfo
        item = intent.getSerializableExtra("item") as NodeItem
        if (!TextUtils.isEmpty(item?.server_logo) && item?.server_logo != "0")
            ImageLoaderManager.loadCircleImage(this, item?.server_logo, iv_icon)
        else {
            iv_icon.setImageResource(intent.getIntExtra("id", R.mipmap.node_icon1))
        }
        tv_name.text = item?.server_name
        tv_address.text = item?.server_address
        tv_price.text = resources.getString(R.string.withdraw_price_balance) + com.weiyu.baselib.util.Utils.toSubStringDegistForChart(wallet?.balance!!.toDouble(), Constant.priceP, false) + wallet?.unit!!.toUpperCase()
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        et_price.text = intent.getStringExtra("price")
    }

    fun show(p: String) {
        var memo = resources.getString(R.string.htdf_memo2)
        val dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 1f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_BOTTOM)
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setView(R.layout.dialog_send_two)
                .create()
        with(dialog) {
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            val pwd = findViewById<EditText>(R.id.et_pwd)
            setEdittxtForHw(pwd)
            findViewById<RelativeLayout>(R.id.right_layout).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_sure).setOnClickListener {
                if (TextUtils.isEmpty(pwd.text)) {
                    toast(resources.getString(R.string.send_input_pwd))
                    return@setOnClickListener
                }
                showProgressDialog(resources.getString(R.string.send_commit))
                Flowable.just("")
                        .compose(bindToLifecycle())
                        .map {
                            if (wallet!!.keystone.startsWith("{")) {
                                val walletfile = KeyStoneUtil.getWalletFile(wallet!!.keystone)
                                var c = Credentials.create(Wallet.decrypt(pwd.text.toString(), walletfile))
                                ECKey.fromPrivate(c.ecKeyPair.privateKey)
                            } else {
                                var keyHax = AesUtils.decrypt(wallet!!.keystone, pwd.text.toString())
                                ECKey.fromPrivate(Numeric.hexStringToByteArray(keyHax))
                            }
                        }.map {
                            var signJson = HtdfSign.reward(wallet!!, it,
                                    item!!.server_address, memo)
                            Utils.HEX.encode(signJson.toByteArray())
                        }
                        .flatMap {
                            HTDFService().broadcast(BroadCast(it))
                        }
                        .subscribe({ t ->
                            dismissProgressDialog()
                            if (!TextUtils.isEmpty(t.raw_log)) {
                                val json = JSONObject(t.raw_log.replace("\\", ""))
                                if (json.has("message")) {
                                    toast(json.getString("message"))
                                } else {
                                    toast(t.raw_log)
                                }
                            } else {
                                dialog.dismiss()
                                var txitem = TxItem(
                                        t.height,
                                        t.txhash.toLowerCase(),
                                        item!!.server_address,
                                        wallet!!.address,
                                        p.replace(",", ""),
                                        wallet!!.unit,
                                        memo,
                                        System.currentTimeMillis(),
                                        Constant.main
                                )
                                txitem.saveOrUpdate("Hash = ? and denom = ?", t.txhash.toLowerCase(), wallet!!.unit)
                                val intent = Intent(this@HtdfRewardActivity, HTDFSuccessActivity::class.java)
                                intent.putExtra("data1", txitem)
                                startActivity(intent)
                                dismiss()
                                ActivityManager.getInstance().finishActivity(HTDFNodeInfoActivity::class.java)
                                finish()
                            }
                        }, { e ->
                            Error.error(e, this@HtdfRewardActivity)
                        })
            }
            show()
        }
    }

    fun weituo(v: View) {
        if (item == null) {
            toast(resources.getString(R.string.commission_hint7))
            return
        }
        showProgressDialog("")
        TxService(WaltType.htdf.name).getAccount(wallet!!.address)
                .compose(bindToLifecycle())
                .subscribe({
                    dismissProgressDialog()
                    var data = it.data
                    if (it.err_code == 200 && data != null) {
                        if (data.value.coins != null && data.value.coins!!.isNotEmpty()) {
                            var amount = 0.0
                            for (b in data.value.coins!!) {
                                amount = amount.plus(b.amount.toDouble())
                            }
                            wallet!!.balance = amount.toString()
                            wallet!!.sequence = data.value.sequence
                            wallet!!.account_number = data.value.account_number
                            MyWalletUtils.instance.updateCheckInfo(wallet!!)
                            tv_price.text = resources.getString(R.string.withdraw_price_balance) + com.weiyu.baselib.util.Utils.toSubStringDegistForChart(wallet?.balance!!.toDouble(), Constant.priceP, false) + wallet?.unit!!.toUpperCase()
                            if (3000000 <= (wallet!!.balance.toDouble() * 100000000)) {
                                show(et_price.text.toString())
                            } else {
                                toast(resources.getString(R.string.commission_node_hint27))
                            }
                        } else {
                            toast(resources.getString(R.string.recharge_hint_no_price))
                        }
                    } else {
                        toast(resources.getString(R.string.recharge_hint_no_price))
                    }
                }, {
                    Error.error(it, this)
                })
    }


}
