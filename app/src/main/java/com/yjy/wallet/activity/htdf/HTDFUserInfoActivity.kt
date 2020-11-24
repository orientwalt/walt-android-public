package com.yjy.wallet.activity.htdf

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.Utils
import com.weiyu.baselib.widget.DecimalInputTextWatcher
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.api.MiningService
import com.yjy.wallet.bean.HtdfApp
import com.yjy.wallet.bean.htdftx.MyNodeInfo
import com.yjy.wallet.bean.htdftx.NodeItem
import com.yjy.wallet.utils.Error
import com.yjy.wallet.wallet.WInfo
import com.zhl.cbdialog.CBDialogBuilder
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_htdf_user_info.*
import java.util.concurrent.TimeUnit


/**
 * weiweiyu
 * 2020/4/8
 * 575256725@qq.com
 * 13115284785
 * 华特东方超级节点详细信息
 */

class HTDFUserInfoActivity : BaseActivity() {
    var adapter: LQRAdapterForRecyclerView<HtdfApp>? = null
    var data1: NodeItem? = null
    var w: WInfo? = null
    override fun getContentLayoutResId(): Int = R.layout.activity_htdf_user_info

    @SuppressLint("CheckResult")
    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        w = intent.getSerializableExtra("data") as WInfo
        data1 = intent.getSerializableExtra("item") as NodeItem
        myinfo = intent.getSerializableExtra("info") as MyNodeInfo
        setData(myinfo!!)
        tv_jiechu.setOnClickListener { show(data1!!.id.toString(), w!!.address) }
        tv_tiqu.setOnClickListener {
            val intent = Intent(this, HtdfInComeActivity::class.java)
            intent.putExtra("address", w!!.address)
            intent.putExtra("w", w)
            intent.putExtra("vaddress", data1!!.server_address)
            startActivity(intent)
        }

        Flowable.interval(10 * 1000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribe({
                    if (!isPause)
                        getInfo()
                }, {

                })
        var arr = resources.getStringArray(R.array.htdf_state)
        adapter = object : LQRAdapterForRecyclerView<HtdfApp>(this, arrayListOf(), R.layout.adapter_htdf_app_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView, item: HtdfApp, position: Int) {
                var tvState = helper.getView<TextView>(R.id.tv_state)
                var tvState2 = helper.getView<TextView>(R.id.tv_state2)
                if (item.type < arr.size) {
                    tvState.text = arr[item.type]
                }
                when (item.type) {
                    0, 1 -> tvState.setTextColor(resources.getColor(R.color.txt_333333))
                    2, 6 -> {
                        tvState.setTextColor(resources.getColor(R.color.red))
                    }
                    3, 4, 5, 7 -> tvState.setTextColor(resources.getColor(R.color.green))
                }
                when (item.type) {
                    2 -> {
                        tvState2.text = resources.getString(R.string.commission_node_hint44)
                        tvState2.setOnClickListener {
                            show(data1!!.id.toString(), w!!.address, item.id.toString())
                        }
                    }
                    3 -> {
                        tvState2.text = resources.getString(R.string.commission_node_hint43)
                        tvState2.setOnClickListener {
                            var p = Utils.toSubStringDegistForChart(item.amount.toDouble(), 8, false).replace(",", "")
                            var intent = Intent(this@HTDFUserInfoActivity, HtdfUnMortgageActivity::class.java)
                            intent.putExtra("data", w)
                            intent.putExtra("item", data1)
                            intent.putExtra("price", p)
                            intent.putExtra("id", item.id.toString())
                            startActivityForResult(intent, 0)
                        }
                    }
                    else -> tvState2.text = ""
                }
                var s = Utils.toSubStringDegistForChart(item.amount.toDouble(), Constant.priceP, false)
                helper.setText(R.id.tv_address, String.format(resources.getString(R.string.commission_node_hint41), item.time))
                helper.setText(R.id.tv_exchang_time, String.format(resources.getString(R.string.commission_node_hint42), s))
                helper.itemView.setOnClickListener {
                    var i = Intent(this@HTDFUserInfoActivity, LiftHtdfActivity::class.java)
                    i.putExtra("data", item)
                    startActivity(i)
                }
            }
        }
        rcv_app.layoutManager = LinearLayoutManager(this)
        rcv_app.adapter = adapter
        srl_coin.setOnRefreshListener {
            getList()
        }
        srl_coin.autoRefresh()
    }

    fun getList() {
        MiningService().relievelist(data1!!.server_address, w!!.address)
                .compose(bindToLifecycle())
                .subscribe({
                    adapter?.data = it.data
                    srl_coin.finishRefresh()
                }, {
                    srl_coin.finishRefresh()
                })
    }

    var isPause = false

    override fun onResume() {
        super.onResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    var myinfo: MyNodeInfo? = null
    @SuppressLint("CheckResult")
    fun getInfo() {
        MiningService().getinfo(w!!.address, data1!!.server_address)
                .compose(bindToLifecycle())
                .subscribe({
                    var data = it.data
                    if (it.code == 200 && data != null) {
                        myinfo = data
                        setData(data)
                    } else {
                        finish()
                    }
                }, {
                    finish()
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            getList()
        }
    }

    fun setData(data: MyNodeInfo) {
        var p = Utils.toSubStringDegistForChart((data.shares.toDouble() / 100000000), Constant.priceP, false)
        tv_my_price.text = p
        ll_my.visibility = View.VISIBLE
        var s = data.profit.toDouble().div(100000000)
        var p2 = Utils.toSubStringDegistForChart(s, Constant.priceP, false)
        tv_my_price2.text = p2
        tv_reward.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, HtdfRewardActivity::class.java)
            intent.putExtra("item", data1)
            intent.putExtra("data", w)
            intent.putExtra("id", getIntent().getIntExtra("id",R.mipmap.node_icon1))
            intent.putExtra("price", p2)
            startActivityForResult(intent, 0)
        }
    }

    fun show(id: String, address: String) {
        show(id, address, "")
    }

    @SuppressLint("CheckResult")
    fun show(id: String, address: String, updateid: String) {
        showProgressDialog("")
        MiningService().delegatortotal(data1!!.server_address, address)
                .compose(bindToLifecycle())
                .subscribe({
                    if (it.code == 200) {
                        val dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 0.8f).showIcon(false)
                                .setTouchOutSideCancelable(false)
                                .showCancelButton(false)
                                .showConfirmButton(false)
                                .setTitle("")
                                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                                .setView(R.layout.dialog_jiechu)
                                .create()
                        with(dialog) {
                            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
                            findViewById<RelativeLayout>(R.id.right_layout).setOnClickListener { dismiss() }
                            findViewById<TextView>(R.id.btn_close).setOnClickListener { dismiss() }
                            var tv_number = findViewById<TextView>(R.id.tv_number)
                            var p = Utils.toSubStringDegistForChart(it.data!!.toDouble(), Constant.priceP, false).replace(",", "")
                            tv_number.text = String.format(resources.getString(R.string.commission_node_hint49), p)
                            var input = findViewById<EditText>(R.id.et_number)
                            input.addTextChangedListener(
                                    DecimalInputTextWatcher(
                                            input,
                                            10,
                                            6,
                                            DecimalInputTextWatcher.Back {

                                            }))

                            findViewById<TextView>(R.id.btn_sure).setOnClickListener {
                                var s = input.text.toString()
                                if (TextUtils.isEmpty(s)) {
                                    toast(resources.getString(R.string.commission_node_hint38))
                                    return@setOnClickListener
                                }
                                if (s.toDouble() <= 0) {
                                    toast(resources.getString(R.string.commission_node_hint39))
                                    return@setOnClickListener
                                }

                                if (s.toDouble() > p.toDouble()) {
                                    toast(String.format(resources.getString(R.string.commission_node_hint48), p))
                                    return@setOnClickListener
                                }
                                showProgressDialog("")
                                MiningService().relievenode(address, if (TextUtils.isEmpty(updateid)) id else updateid, s, if (TextUtils.isEmpty(updateid)) "1" else "2")
                                        .compose(bindToLifecycle())
                                        .subscribe({
                                            this@HTDFUserInfoActivity.dismissProgressDialog()
                                            if (it.code == 200) {
                                                getList()
                                                toast(resources.getString(R.string.feedback_hint14))
                                                dismiss()
                                            } else {
                                                toast(it.msg!!)
                                            }
                                        }, {
                                            Error.error(it, this@HTDFUserInfoActivity)
                                            dismiss()
                                        })
                            }
                            dialog.show()
                        }
                    } else {
                        toast(it.msg!!)
                    }
                    dismissProgressDialog()
                }, {
                    Error.error(it, this)
                    dismissProgressDialog()
                })

    }

}