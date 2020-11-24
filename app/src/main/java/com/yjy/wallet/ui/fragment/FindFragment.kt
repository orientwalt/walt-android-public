package com.yjy.wallet.ui.fragment

import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.ustory.koinsample.Adapter.KAdapterFactory
import com.ustory.koinsample.Adapter.KotlinAdapter
import com.weiyu.baselib.base.BaseFragment
import com.weiyu.baselib.util.Utils
import com.weiyu.baselib.widget.FullyGridLayoutManager
import com.yjy.wallet.Constant
import com.yjy.wallet.Constant.Companion.priceP
import com.yjy.wallet.R
import com.yjy.wallet.activity.AddCoinActivity
import com.yjy.wallet.activity.WebActivity1
import com.yjy.wallet.activity.htdf.HtdfNodeActivity
import com.yjy.wallet.api.TxService
import com.yjy.wallet.bean.waltbean.FindBean
import com.yjy.wallet.bean.waltbean.FindItem
import com.yjy.wallet.bean.waltbean.InfoItem2
import com.yjy.wallet.utils.GlideImageLoader
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import com.yjy.wallet.wallet.YWallet
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.zhl.cbdialog.CBDialogBuilder
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.fragment_find.*


/**
 * weiweiyu
 * 2020/6/19
 * 575256725@qq.com
 * 13115284785
 */
class FindFragment : BaseFragment() {
    var winfo: WInfo? = null
    var banner: Banner? = null
    var adapter: KotlinAdapter<FindBean>? = null


    override fun getContentLayoutResId(): Int = R.layout.fragment_find

    override fun initializeContentViews() {
        EventBus.getDefault().register(this)
    }

    fun onEvent(yWallet: YWallet?) {
        setname()
    }

    var list: ArrayList<FindBean> = arrayListOf()
    var fList: ArrayList<FindItem> = arrayListOf()
    override fun viewCreated() {
        fList.add(FindItem(resources.getString(R.string.find_htdf_title),
                resources.getString(R.string.find_htdf_title1), "", 1, R.mipmap.ad_htdf.toString()))
//        fList.add(FindItem(resources.getString(R.string.plan_title),
//                resources.getString(R.string.plan_hint11), "", 2, R.mipmap.ad_plan.toString()))
        adapter = KAdapterFactory.KAdapter {
            header(R.layout.find_head) {
                banner = it.findViewById<Banner>(R.id.banner)
                val list = mutableListOf<Int>()
                list.add(R.mipmap.banner1)
                //设置图片加载器
                banner?.setImageLoader(GlideImageLoader())
                //设置图片集合
                banner?.setImages(list)
                banner?.isAutoPlay(true)
                //设置轮播时间
                banner?.setDelayTime(3000)
                //设置指示器位置（当banner模式中有指示器时）
                banner?.setIndicatorGravity(BannerConfig.CENTER)
                banner?.setOnBannerListener {
                    if (winfo != null) {
                        val intent = Intent()
                        intent.setClass(activity!!, HtdfNodeActivity::class.java)
                        intent.putExtra("data", winfo)
                        startActivity(intent)
                    } else {
                        toast(resources.getString(R.string.find_btc_choice_walt))
                    }
                }
                banner?.start()
            }
            layout {
                R.layout.adapter_find_item
            }
            bindData { type, vh, bean ->
                vh.bindView<TextView>(R.id.tv_hint1).text = bean.title
                var rcv = vh.bindView<RecyclerView>(R.id.rcv_find_item)
                rcv.layoutManager = FullyGridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
                var blist: ArrayList<FindItem> = arrayListOf()
                if (bean.show) {
                    blist = bean.list
                } else {
                    if (bean.list.size > 3) {
                        blist.add(bean.list[0])
                        blist.add(bean.list[1])
                        blist.add(bean.list[2])
                    } else {
                        blist = bean.list
                    }
                }
                var iAdapter: KotlinAdapter<FindItem> = KAdapterFactory.KAdapter {
                    layout { R.layout.adapter_find_item_item }
                    bindData { type: Int, vh: KotlinAdapter.ViewHolder, data: FindItem ->
                        var img = vh.bindView<ImageView>(R.id.iv_htdf)
                        var t1 = vh.bindView<TextView>(R.id.t1)
                        var t2 = vh.bindView<TextView>(R.id.t2)
                        t1.text = data.title
                        t2.text = data.describe
                        when (data.type) {
                            0 -> {
                                Glide.with(context)
                                        .load(Constant.waltpath + data.image)
                                        .into(img)
                            }
                            else -> {
                                Glide.with(context)
                                        .load(data.image.toInt())
                                        .into(img)
                            }
                        }
                        vh.itemView.setOnClickListener {
                            when (data.type) {
                                0 -> {
                                    startWeb(data.url, data.title)
                                }
                                1 -> {
                                    if (winfo != null) {
                                        val intent = Intent()
                                        intent.setClass(activity!!, HtdfNodeActivity::class.java)
                                        intent.putExtra("data", winfo)
                                        startActivity(intent)
                                    } else {
                                        toast(resources.getString(R.string.find_btc_choice_walt))
                                    }
                                }
                            }
                        }
                    }
                    data { blist }
                }
                if (bean.list.size > 3) {
                    iAdapter.footer(R.layout.adapter_find_item_item_footer) {
                        var showTv = it.findViewById<ImageView>(R.id.tv_show)
                        if (bean.show) {
                            showTv.setImageResource(R.mipmap.find_show_icon)
                        } else {
                            showTv.setImageResource(R.mipmap.find_show_icon1)
                        }
                        it.setOnClickListener {
                            bean.show = !bean.show
                            adapter?.notifyDataSetChanged()
                        }
                    }
                }
                iAdapter.into(rcv)
            }
            data { list }
        }
        rcv_find.layoutManager = LinearLayoutManager(activity)
        adapter?.into(rcv_find)
        rl_walt.setOnClickListener {
            showList()
        }
        srl_coin.setOnRefreshListener {
            getdata()
        }
        srl_coin.autoRefresh()
    }

    fun startWeb(path: String, title: String) {
        val intent = Intent(activity, WebActivity1::class.java)
        intent.putExtra("url", path)
        intent.putExtra("title", title)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        banner?.startAutoPlay();
        setname()
    }

    var first = true
    fun setname() {
        var w = MyWalletUtils.instance.getCheckHTDFWallet()
        if (w != null) {
            var wInfo = w.map[WaltType.htdf.name]
            winfo = wInfo
            if (first) {
                if (!TextUtils.isEmpty(Constant.mainChainRMB)) {
                    var mainChainRMB = Gson().fromJson<MutableList<InfoItem2>>(Constant.mainChainRMB, object : TypeToken<MutableList<InfoItem2>>() {}.type)
                    var item = mainChainRMB.find { it.symbol == WaltType.htdf.name }
                    if (item != null) {
                        winfo?.rmb = if (TextUtils.isEmpty(item.price_cny)) 0.0 else item.price_cny.toDouble()
                    }
                }
                TxService(WaltType.htdf.name).getAccount(winfo!!.address)
                        .compose(bindToLifecycle())
                        .subscribe({
                            first = false
                            var data = it.data
                            if (it.err_code == 200 && data != null) {
                                var amount = 0.0
                                if (data.value.coins != null && data.value.coins!!.isNotEmpty()) {
                                    for (b in data.value.coins!!) {
                                        if (winfo!!.unit.toLowerCase() == b.denom.toLowerCase())
                                            amount = amount.plus(b.amount.toDouble())
                                    }
                                }
                                winfo?.balance = amount.toString()
                                winfo?.sequence = data.value.sequence
                                winfo?.account_number = data.value.account_number

                            } else {
                                winfo?.balance = "0.0"
                            }
                            MyWalletUtils.instance.updateCheckInfo(winfo!!)

                        }, {})

            }
            tv_walt_name.text = w.remark
        } else {
            tv_walt_name.text = resources.getString(R.string.recharge_walt)
        }
    }

    override fun onStop() {
        super.onStop()
        banner?.stopAutoPlay();
    }

    fun showList() {
        var data = MyWalletUtils.instance.getHTDFWallet()
        var view = LayoutInflater.from(activity).inflate(R.layout.dialog_walt, null)
        var dialog = CBDialogBuilder(activity, CBDialogBuilder.DIALOG_STYLE_NORMAL, 1f).showIcon(false)
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_BOTTOM)
                .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                .setView(view)
                .showCancelButton(false)
                .setTitle("")
                .showConfirmButton(false)
                .create()
        var rcv_wallet = view.findViewById<RecyclerView>(R.id.rcv_wallet)
        var rl_null = view.findViewById<RelativeLayout>(R.id.rl_null)
        if (data.isEmpty()) {
            rl_null.visibility = View.VISIBLE
        }
        view.findViewById<TextView>(R.id.btn_login).setOnClickListener {
            startActivity(AddCoinActivity::class.java)
        }
        var adapter = object : LQRAdapterForRecyclerView<YWallet>(activity, arrayListOf(), R.layout.adapter_wallet_list_item3) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: YWallet, position: Int) {
                var check = helper?.getView<RelativeLayout>(R.id.rl_check)
                if (item.htdf) {
                    check?.visibility = View.VISIBLE
                } else {
                    check?.visibility = View.GONE
                }
                val mostValue = item.map[WaltType.htdf.name]
                helper?.setText(R.id.tv_name2, mostValue!!.address)
                helper?.setText(R.id.tv_name, item.remark)
                helper?.setText(R.id.tv_price, Utils.toSubStringDegistForChart(mostValue!!.balance.toDouble(), priceP, false) + mostValue.unit.toUpperCase())
                helper?.setOnItemClickListener { _, _, _, _ ->
                    dialog.dismiss()
                    MyWalletUtils.instance.updateHTDF(item)
                    first = true
                    setname()
                }
            }
        }
        rcv_wallet.layoutManager = LinearLayoutManager(activity)
        rcv_wallet.adapter = adapter
        adapter.data = data
        dialog.findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
        dialog.show()
    }

    fun getdata() {
        list.add(FindBean(resources.getString(R.string.find_hint1), fList))
        adapter?.data { list }
        adapter?.notifyDataSetChanged()
        srl_coin.finishRefresh()
    }
}