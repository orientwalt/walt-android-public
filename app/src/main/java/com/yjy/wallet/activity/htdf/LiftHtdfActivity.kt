package com.yjy.wallet.activity.htdf

import android.text.TextUtils
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.R
import com.yjy.wallet.api.HTDFService
import com.yjy.wallet.api.MiningService
import com.yjy.wallet.bean.HtdfApp
import com.yjy.wallet.bean.htdf.tx.Htdftx
import com.yjy.wallet.utils.Error
import com.yjy.wallet.wallet.WaltType
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_htdf_lift.*
import org.json.JSONObject

/**
 * weiweiyu
 * 2020/4/15
 * 575256725@qq.com
 * 13115284785
 */

class LiftHtdfActivity : BaseActivity() {

    override fun getContentLayoutResId(): Int = R.layout.activity_htdf_lift

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var data = intent.getSerializableExtra("data") as HtdfApp
        tx_price.text = Utils.toSubStringDegistForChart(data.amount.toDouble(), 4, false) + "HTDF"
        when (data.type) {
            2 -> {
                tv_msg1.text = data.remark
                tv_state_title.text = resources.getString(R.string.commission_node_hint60)
                tv_msg.text = resources.getString(R.string.commission_node_hint62)
                tv_msg.setBackgroundColor(resources.getColor(R.color.color_red2_20))
                tv_msg.setTextColor(resources.getColor(R.color.red))
                v_p1.background = resources.getDrawable(R.drawable.state_fail_icon1)
                tv_state1.setTextColor(resources.getColor(R.color.color_red2))
                tv_state1.text = resources.getString(R.string.commission_node_hint66)
            }
            3 -> {
                tv_msg1.text = resources.getString(R.string.commission_node_hint32)
                tv_msg.text = resources.getString(R.string.commission_node_hint63)
                tv_msg.setBackgroundColor(resources.getColor(R.color.color_green2_20))
                tv_msg.setTextColor(resources.getColor(R.color.color_green2))
                tv_state1.setTextColor(resources.getColor(R.color.color_green2))
            }
            4, 5 -> {
                tv_msg.text = resources.getString(R.string.commission_node_hint64)
                tv_msg.setBackgroundColor(resources.getColor(R.color.color_blue2_20))
                tv_msg.setTextColor(resources.getColor(R.color.color_blue2))
                tv_state1.setTextColor(resources.getColor(R.color.color_green2))
                tv_state2.setTextColor(resources.getColor(R.color.color_green2))
                tv_state3.setTextColor(resources.getColor(R.color.color_green2))
                v_l1.setBackgroundColor(resources.getColor(R.color.color_green2))
                v_l2.setBackgroundColor(resources.getColor(R.color.color_green2))
                v_p1.background = resources.getDrawable(R.drawable.state_success_icon1)
                v_p2.background = resources.getDrawable(R.drawable.state_success_icon1)
                v_p3.background = resources.getDrawable(R.drawable.state_success_icon1)
                ll_app_time.visibility = View.GONE
                rl_info.visibility = View.VISIBLE
            }
            7 -> {
                tv_msg.text = resources.getString(R.string.commission_node_hint65)
                tv_msg.setBackgroundColor(resources.getColor(R.color.color_blue3_20))
                tv_msg.setTextColor(resources.getColor(R.color.color_blue3))
                tv_state1.setTextColor(resources.getColor(R.color.color_green2))
                tv_state2.setTextColor(resources.getColor(R.color.color_green2))
                tv_state3.setTextColor(resources.getColor(R.color.color_green2))
                tv_state4.setTextColor(resources.getColor(R.color.color_green2))
                v_l1.setBackgroundColor(resources.getColor(R.color.color_green2))
                v_l2.setBackgroundColor(resources.getColor(R.color.color_green2))
                v_l3.setBackgroundColor(resources.getColor(R.color.color_green2))
                v_p1.background = resources.getDrawable(R.drawable.state_success_icon1)
                v_p2.background = resources.getDrawable(R.drawable.state_success_icon1)
                v_p3.background = resources.getDrawable(R.drawable.state_success_icon1)
                v_p4.background = resources.getDrawable(R.drawable.state_success_icon1)
                ll_app_time.visibility = View.GONE
                rl_info.visibility = View.VISIBLE
            }
            6 -> {
                tv_msg.text = resources.getString(R.string.commission_node_hint62)
                tv_msg.setBackgroundColor(resources.getColor(R.color.color_red2_20))
                tv_msg.setTextColor(resources.getColor(R.color.red))
                tv_state1.setTextColor(resources.getColor(R.color.color_green2))
                tv_state2.setTextColor(resources.getColor(R.color.color_green2))
                tv_state3.setTextColor(resources.getColor(R.color.color_green2))
                tv_state4.setTextColor(resources.getColor(R.color.color_green2))
                v_l1.setBackgroundColor(resources.getColor(R.color.color_green2))
                v_l2.setBackgroundColor(resources.getColor(R.color.color_green2))
                v_l3.setBackgroundColor(resources.getColor(R.color.color_green2))
                v_p1.background = resources.getDrawable(R.drawable.state_success_icon1)
                v_p2.background = resources.getDrawable(R.drawable.state_success_icon1)
                v_p3.background = resources.getDrawable(R.drawable.state_success_icon1)
                v_p4.background = resources.getDrawable(R.drawable.state_fail_icon1)
                ll_app_time.visibility = View.GONE
                rl_info.visibility = View.VISIBLE
                tv_state4.text = resources.getString(R.string.commission_node_hint50)
                tv_state4.setTextColor(resources.getColor(R.color.red))
            }
        }
        MiningService().extract_detail(data.id.toString())
                .compose(bindToLifecycle())
                .subscribe({
                    if (it.data != null) {
                        tv_time6.text = if (TextUtils.isEmpty(it.data!!.time)) "--" else it.data!!.time
                        tx_info3.text = if (TextUtils.isEmpty(it.data!!.time)) "--" else it.data!!.time
                        if (data.type != 0) {
                            tv_time1.text = if (TextUtils.isEmpty(it.data!!.sh_time)) "" else it.data!!.sh_time
                        }
                        if (data.type == 2) {
                            tv_msg1.text = it.data!!.remark
                        }
                        tv_time2.text = if (TextUtils.isEmpty(it.data!!.tq_time)) "--" else it.data!!.tq_time
                        tv_time3.text = if (TextUtils.isEmpty(it.data!!.tq_time)) "--" else it.data!!.tq_time
                        tx_info4.text = it.data!!.tq_txhash
                        if (data.type == 4 || data.type == 5) {
                            tv_msg1.text = String.format(resources.getString(R.string.str_tiqu1), if (TextUtils.isEmpty(it.data!!.tq_dztime)) " --" else it.data!!.tq_dztime)
                            tv_time4.text = String.format(resources.getString(R.string.str_tiqu), if (TextUtils.isEmpty(it.data!!.tq_dztime)) " --" else it.data!!.tq_dztime)
                        } else if (data.type == 6) {
                            tv_time4.text = if (TextUtils.isEmpty(it.data!!.tq_dztime)) "--" else it.data!!.tq_dztime
                            tv_msg1.text = resources.getString(R.string.commission_node_hint50)
                        } else if (data.type == 7) {
                            tv_msg1.text = String.format(resources.getString(R.string.str_tiqu2), it.data!!.server_name)
                            tv_time4.text = if (TextUtils.isEmpty(it.data!!.tq_dztime)) "--" else it.data!!.tq_dztime
                        }
                        if (data.type >= 5) {
                            showProgressDialog("")
                            HTDFService().gettx(it.data!!.tq_txhash)
                                    .compose(bindToLifecycle())
                                    .subscribe(object : ResourceSubscriber<Htdftx>() {
                                        override fun onComplete() {
                                        }

                                        override fun onNext(t: Htdftx) {
                                            if (!t.tx.value.msg.isNullOrEmpty()) {
                                                tx_info2.text = t.tx.value.msg[0]!!.value.delegator_address
                                                tx_info1.text = t.tx.value.msg[0]!!.value.validator_address
                                            }
                                            tx_info5.text = t.height
                                            try {
                                                var fee = t.gas_used.toLong().times(t.tx.value.fee.gas_price.toLong())
                                                tx_info6.text = Utils.toNormal((fee.toDouble() / 100000000)).toString() + WaltType.htdf.name.toUpperCase()
                                            } catch (e: Exception) {

                                            }
                                            var isSuccess = false
                                            if (t?.logs!!.isNotEmpty()) {
                                                isSuccess = t.logs[0].success
                                            }
                                            if (isSuccess) {
                                                HTDFService().getBlockLatest()
                                                        .compose(bindToLifecycle())
                                                        .subscribe({
                                                            var json = JSONObject(it)
                                                            if (json.has("block_meta")) {
                                                                var blockJson = json.getJSONObject("block_meta")
                                                                if (blockJson.has("header")) {
                                                                    val headerJson = blockJson.getJSONObject("header")
                                                                    if (headerJson.has("height")) {
                                                                        var heightCom = (headerJson.getString("height").toInt() - t.height!!.toInt())
                                                                        tx_info5.text = "${t.height!!.toInt()}(${heightCom}${resources.getString(R.string.str_confirm)})"
                                                                    }
                                                                }
                                                            }
                                                        }, {

                                                        })
//
                                            }
//
                                            dismissProgressDialog()
                                        }

                                        override fun onError(t: Throwable?) {
                                            dismissProgressDialog()
                                        }
                                    })
                        }
                    }
                }, {
                    Error.error(it, this)
                })

    }

}