package com.yjy.wallet.activity.htdf

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.ImageLoaderManager
import com.weiyu.baselib.util.Utils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.api.MiningService
import com.yjy.wallet.bean.htdftx.MyNodeInfo
import com.yjy.wallet.bean.htdftx.NodeItem
import com.yjy.wallet.wallet.WInfo
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_htdf_node_info.*
import java.util.concurrent.TimeUnit


/**
 * weiweiyu
 * 2020/4/8
 * 575256725@qq.com
 * 13115284785
 * 华特东方超级节点详细信息
 */

class HTDFNodeInfoActivity : BaseActivity() {
    var address: NodeItem? = null
    var w: WInfo? = null
    var info: MyNodeInfo? = null
    override fun getContentLayoutResId(): Int = R.layout.activity_htdf_node_info

    @SuppressLint("CheckResult")
    override fun initializeContentViews() {
        w = intent.getSerializableExtra("data") as WInfo
        address = intent.getSerializableExtra("item") as NodeItem
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tb_title.setRightLayoutClickListener(View.OnClickListener {
            val intent = Intent()
            intent.setClass(this, HTDFUserHistoryActivity::class.java)
            intent.putExtra("item", address)
            intent.putExtra("data", w)
            intent.putExtra("info", info)
            startActivity(intent)
        })
        if (intent.getIntExtra("type", 0) == 0) {
            info = intent.getSerializableExtra("info") as MyNodeInfo
            setData(info!!)
        }else{
            check()
        }
        tx4.text = Utils.toSubStringDegistForChart(address?.node_total!!, 4, false)
        tx5.text = address?.node_num.toString()
        tv_name.text = address?.server_name
        tv_tan.setOnClickListener { showPop(it) }
        tv_address.text = address?.server_address
        tx6.text = Utils.StringToPecent(intent.getStringExtra("pecent").toDouble())
        if (!TextUtils.isEmpty(address?.server_logo) && address?.server_logo != "0")
            ImageLoaderManager.loadCircleImage(this, address?.server_logo, iv_icon)
        else {
            iv_icon.setImageResource(intent.getIntExtra("id", R.mipmap.node_icon1))
        }
        tv_introduction.text = if (TextUtils.isEmpty(address?.server_introduce)) "...." else address?.server_introduce
        btn_weituo.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, HtdfMortgageActivity::class.java)
            intent.putExtra("item", address)
            intent.putExtra("data", w)
            intent.putExtra("id", getIntent().getIntExtra("id",R.mipmap.node_icon1))
            startActivityForResult(intent, 0)
        }
        tv_go.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, HTDFUserInfoActivity::class.java)
            intent.putExtra("item", address)
            intent.putExtra("data", w)
            intent.putExtra("id", getIntent().getIntExtra("id",R.mipmap.node_icon1))
            intent.putExtra("info", info)
            startActivity(intent)
        }
        Flowable.interval(10 * 1000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribe({
                    if (!isPause)
                        getInfo()
                }, {

                })
    }

    var isPause = false
    override fun onResume() {
        super.onResume()
        isPause = false
        getInfo()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    fun showPop(v: View) {
//        val view: View = LayoutInflater.from(this).inflate(R.layout.popup_item_active, null, false)
//        var popupWindow =  PopupWindow();
//        popupWindow.contentView = view;
//        popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        popupWindow.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        popupWindow.setBackgroundDrawable(ColorDrawable()); // 需要设置一个背景setOutsideTouchable(true)才会生效
//        popupWindow.isFocusable = true; // 防止点击事件穿透
//        popupWindow.isOutsideTouchable = true; // 设置点击外部时取消
//        popupWindow.showAsDropDown(v)
    }

    @SuppressLint("CheckResult")
    fun getInfo() {
        MiningService().getinfo(w!!.address, address!!.server_address)
                .compose(bindToLifecycle())
                .subscribe({
                    var data = it.data
                    if (it.code == 200 && data != null) {
                        setData(data)
                    } else {
                        ll_my.visibility = View.GONE
                        btn_weituo.text = resources.getString(R.string.commission_node_hint16)
                    }
                }, {
                    ll_my.visibility = View.GONE
                    btn_weituo.text = resources.getString(R.string.commission_node_hint16)
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            getInfo()
        }
    }

    fun check() {
        MiningService().check(w!!.address, address!!.server_address)
                .compose(bindToLifecycle())
                .subscribe({
                    if (it.data != null) {
                        if (it.data!!) {
                            tb_title.setRightText(resources.getString(R.string.history))
                            tb_title.setRightLayoutVisibility(View.VISIBLE)
                        } else {
                            tb_title.setRightLayoutVisibility(View.GONE)
                        }
                    } else {
                        tb_title.setRightLayoutVisibility(View.GONE)
                    }
                }, {

                })
    }

    fun setData(data: MyNodeInfo) {
        info = data
        btn_weituo.text = resources.getString(R.string.commission_node_hint23)
        var p = Utils.toSubStringDegistForChart((data.shares.toDouble() / 100000000), Constant.priceP, false)
        tv_my_price.text = p
        ll_my.visibility = View.VISIBLE

        var s = data.profit.toDouble().div(100000000)
        var p2 = Utils.toSubStringDegistForChart(s, Constant.priceP, false)
        tv_my_price2.text = p2
        tv_jiechu.setOnClickListener {
            val intent = Intent(this, HtdfInComeActivity::class.java)
            intent.putExtra("address", w!!.address)
            intent.putExtra("w", w)
            intent.putExtra("vaddress", address!!.server_address)
            startActivity(intent)
        }
        tv_reward.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, HtdfRewardActivity::class.java)
            intent.putExtra("item", address)
            intent.putExtra("data", w)
            intent.putExtra("id", getIntent().getIntExtra("id",R.mipmap.node_icon1))
            intent.putExtra("price", p2.replace(",", ""))
            startActivityForResult(intent, 0)
        }
    }

}