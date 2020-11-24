package com.yjy.wallet.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.bean.UpdateW
import com.yjy.wallet.wallet.MyWalletUtils
import com.yjy.wallet.wallet.WInfo
import com.yjy.wallet.wallet.WaltType
import com.yjy.wallet.wallet.YWallet
import com.zhl.cbdialog.CBDialogBuilder
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.activity_wallet_list.*

/**
 *Created by weiweiyu
 *on 2019/5/5
 */
class WalletListActivity : BaseActivity() {
    var adapter: LQRAdapterForRecyclerView<YWallet>? = null
    val bg = arrayListOf<Int>(R.drawable.gradient_blue, R.drawable.gradient_blue2, R.drawable.gradient_orange)
    override fun getContentLayoutResId(): Int = R.layout.activity_wallet_list
    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tb_title.setRightLayoutClickListener(View.OnClickListener { startActivity(AddCoinActivity::class.java) })
        adapter = object : LQRAdapterForRecyclerView<YWallet>(this, arrayListOf(), R.layout.adapter_wallet_list_item) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: YWallet, position: Int) {
                val mostValue = getMostValue(item)
                if (mostValue != null) {
                    helper?.getView<LinearLayout>(R.id.ll_addr)?.visibility = View.VISIBLE
                    helper?.setText(R.id.tv_coin_type, mostValue.unit.toUpperCase() + "  ")
                    helper?.setText(R.id.tv_address, mostValue.address)
                }
                helper?.setText(R.id.tv_name, item.remark)
                helper?.getView<RelativeLayout>(R.id.rl_bg)?.setBackgroundResource(bg[position % 3])
                helper?.setOnItemClickListener { _, _, _, _ ->
                    MyWalletUtils.instance.update(item)
                    EventBus.getDefault().post(UpdateW())
                    finish()
                }
                helper?.getView<ImageView>(R.id.tv_go)?.setOnClickListener {
                    val intent = Intent(this@WalletListActivity, WalletInfoActivity::class.java)
                    intent.putExtra("data", item)
                    startActivity(intent)
                }
            }
        }
        rcv_wallet.layoutManager = LinearLayoutManager(this)
        rcv_wallet.adapter = adapter
    }

    fun getMostValue(yWallet: YWallet): WInfo? {
        val list: MutableList<WInfo?> = arrayListOf()
        yWallet.map.forEach { item ->
            if (yWallet.wType == 100) {
                if (item.value.show && item.key != WaltType.EOS.name) {
                    list.add(item.value)
                }
            } else {
                list.add(item.value)
            }
        }
        yWallet.ercmMap.forEach { item ->
            if (!TextUtils.isEmpty(item.value?.contract_address)) {
                list.add(item.value)
            }
        }
        yWallet.htdfercmMap.forEach { item ->
            if (!TextUtils.isEmpty(item.value?.contract_address)) {
                list.add(item.value)
            }
        }
        yWallet.wenMap.forEach { item ->
            list.add(item.value)
        }
        if (list.isEmpty()) {
            return null
        }
        return list[0]
    }

    override fun onResume() {
        super.onResume()
        adapter!!.clearData()
        adapter!!.addNewData(MyWalletUtils.instance.getWallet())
    }

    fun show() {
        var dialog = CBDialogBuilder(this, CBDialogBuilder.DIALOG_STYLE_NORMAL, 0.75f).showIcon(false)
                .setTouchOutSideCancelable(false)
                .showCancelButton(false)
                .showConfirmButton(false)
                .setTitle("")
                .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_CENTER)
                .setView(R.layout.dialog_main_test)
                .create()
        with(dialog) {
            findViewById<TextView>(R.id.dialog_title).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.right_layout).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.btn_start).setOnClickListener {
                startActivity(TestActivity::class.java)
                dismiss()
            }
            show()
        }
    }

}