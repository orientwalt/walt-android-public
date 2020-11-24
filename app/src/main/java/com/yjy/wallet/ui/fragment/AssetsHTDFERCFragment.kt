package com.yjy.wallet.ui.fragment

import android.app.Activity.RESULT_OK
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.base.BaseFragment
import com.weiyu.baselib.net.BaseResult
import com.weiyu.baselib.net.DefaultObserver
import com.weiyu.baselib.util.BLog
import com.yjy.wallet.R
import com.yjy.wallet.api.TxService
import com.yjy.wallet.bean.ERCBean
import com.yjy.wallet.bean.htdf.HRCBean
import com.yjy.wallet.utils.CoinUtils
import com.yjy.wallet.wallet.HTDFERCType
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.fragment_search.*
import org.litepal.LitePal

/**
 * weiweiyu
 * 2019/8/22
 * 575256725@qq.com
 * 13115284785
 */
class AssetsHTDFERCFragment : BaseFragment() {
    var adapter: LQRAdapterForRecyclerView<ERCBean>? = null
    override fun getContentLayoutResId(): Int = R.layout.fragment_search

    override fun initializeContentViews() {
        adapter = object : LQRAdapterForRecyclerView<ERCBean>(activity, mutableListOf(), R.layout.adapter_erc_add_item2) {
            override fun convert(helper: LQRViewHolderForRecyclerView, item: ERCBean, position: Int) {
                helper.setText(R.id.tv_name, item.name.toUpperCase())
                helper.setText(R.id.tv_address, item.address)
                try {
                    val ercType = HTDFERCType.fromAddress(item.address)
                    helper.getView<ImageView>(R.id.iv_icon)!!.setImageDrawable(resources.getDrawable(ercType.drawable))
                    helper.setText(R.id.tv_name2, ercType.fall_name)
                } catch (e: java.lang.Exception) {
                    helper.getView<ImageView>(R.id.iv_icon)!!.setImageDrawable(resources.getDrawable(R.mipmap.hrc_icon))
                    helper.setText(R.id.tv_name2, item.fullName)
                }
                val rlSwitch = helper.getView<RelativeLayout>(R.id.rl_switch)
                rlSwitch.isSelected = item.open
                val tv_bg = helper.getView<TextView>(R.id.tv_bg)
                val tv_left = helper.getView<TextView>(R.id.tv_left)
                val tv_right = helper.getView<TextView>(R.id.tv_right)
                if (rlSwitch.isSelected) {
                    tv_left.visibility = View.GONE
                    tv_right.visibility = View.VISIBLE
                    tv_bg.background = resources.getDrawable(R.drawable.switch_bg)
                } else {
                    tv_left.visibility = View.VISIBLE
                    tv_right.visibility = View.GONE
                    tv_bg.background = resources.getDrawable(R.drawable.switch_bg2)
                }
                rlSwitch.setOnClickListener {
                    if (rlSwitch.isSelected) {
                        MyWalletUtils.instance.removehtdfERC(item.address)
                    } else {
                        val yWallet = MyWalletUtils.instance.getCheckWallet()
                        if (yWallet?.wType == 0 || yWallet?.wType == 100) {
                            val eth = yWallet.map[WaltType.htdf.name]
                            val erc = eth?.copy()
                            erc?.balance = "0.0"
                            erc?.unit = item.name
                            erc?.contract_address = item.address
                            erc?.decimal = item.decimal
                            erc?.custom = item.custom
                            MyWalletUtils.instance.addHtdfERC(erc!!)
                        }
                    }
                    val tx = this@AssetsHTDFERCFragment.et_search.text.toString().trim().toLowerCase()
                    setdata(tx)
                    activity?.setResult(RESULT_OK)
                }
            }
        }
    }

    override fun viewCreated() {
        config_hidden.requestFocus()
        et_search.visibility = View.VISIBLE
        rcv_erc.layoutManager = LinearLayoutManager(activity)
        rcv_erc.adapter = adapter
        setdata("")
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val tx = et_search.text.toString().trim().toLowerCase()
                setdata(tx)
            }
        })
    }

    fun setdata(tx: String) {
        val yWallet = MyWalletUtils.instance.getCheckWallet()
        val sList = if (TextUtils.isEmpty(tx)) {//先查找数据库自定义添加的erc20
            LitePal.where("type = ?", WaltType.htdf.ordinal.toString()).find(ERCBean::class.java)
        } else {
            if (CoinUtils.checkAddress(WaltType.htdf, tx)) {
                LitePal.where("address = ?", tx).find(ERCBean::class.java)
            } else {
                LitePal.where("(( name like ? ) or ( fullName like ? )) and type = ?", "%$tx%", "%$tx%", WaltType.htdf.ordinal.toString()).find(ERCBean::class.java)
            }
        }
        BLog.d("------------------------------${sList.size}")
        var ercList: MutableList<ERCBean> = mutableListOf()
        HTDFERCType.values().forEach {
            val bean = ERCBean(it.name, it.drawable, it.address, it.fall_name)
            bean.decimal = it.factor
            ercList.add(bean)
            sList.forEach { itBean ->
                if (it.address == itBean.address) {
                    sList.remove(itBean)
                    itBean.delete()
                }
            }
        }
        var allList = ercList.union(sList).toMutableList()
        yWallet?.htdfercmMap?.forEach {
            //遍历枚举erc20
            allList.forEach { s ->
                if (it.key == s.address) {
                    s.open = true
                }
            }
        }
        if (!TextUtils.isEmpty(tx)) {
            allList = allList.filter {
                it.fullName.contains(tx.toUpperCase()) ||
                        it.name.contains(tx.toUpperCase()) ||
                        (CoinUtils.checkAddress(WaltType.htdf, tx) && it.address == tx)
            }.toMutableList()
        }
        ercList.sortBy { it.custom }
        adapter?.data = allList
        if (allList.isEmpty() && CoinUtils.checkAddress(WaltType.htdf, tx)) {
            search(tx)
        }
    }

    fun search(address: String) {
        val yWallet = MyWalletUtils.instance.getCheckWallet() ?: return
        val htdf = yWallet.map[WaltType.htdf.name]
        val hrc = htdf?.copy()
        if (yWallet.wType == 0 || yWallet.wType == 100) {
            TxService(WaltType.htdf.name).searchToken(address)
                    .compose(bindToLifecycle())
                    .subscribe(object : DefaultObserver<BaseResult<List<HRCBean>>>(activity as BaseActivity, "") {
                        override fun onSuccess(response: BaseResult<List<HRCBean>>) {
                            if (response.data.isNullOrEmpty()) {
                                toast(resources.getString(R.string.add_token_custom_hint6))
                            } else {
                                var bean = response.data!![0]
                                var t = ERCBean()
                                t.custom = true
                                t.address = bean.address
                                t.drawable = R.mipmap.hrc_icon
                                t.name = bean.en_name
                                t.fullName = bean.name
                                t.decimal = bean.company.toInt()
                                hrc?.unit = t.name
                                hrc?.contract_address = t.address
                                hrc?.decimal = t.decimal
                                hrc?.custom = true
                                t.type = WaltType.htdf.ordinal
                                t.saveOrUpdate("address = ?", address)
                                adapter?.addLastItem(t)
                            }

                        }

                        override fun onErr(e: Throwable) {
                            super.onErr(e)
                            toast(resources.getString(R.string.add_token_custom_hint6))
                        }
                    })
        }
    }
}