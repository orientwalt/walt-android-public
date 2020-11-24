package com.yjy.wallet.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.lqr.adapter.OnItemClickListener
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.activity_coin.*

/**
 * Created by weiweiyu
 * on 2019/5/7
 * 币种选择
 */
class WalletTypeActivity : BaseActivity() {
    var adapter: LQRAdapterForRecyclerView<WaltType>? = null
    override fun getContentLayoutResId(): Int = R.layout.activity_wallet_type

    override fun initializeContentViews() {
        val t = intent.getIntExtra("type", 0)
        if (t == 2) {
            tb_title.setTitle(resources.getString(R.string.address_type_title))
        }
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        adapter = object : LQRAdapterForRecyclerView<WaltType>(this, arrayListOf(), R.layout.adapter_coin_item2) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: WaltType, position: Int) {
                helper?.setText(R.id.tv_name, item.name.toUpperCase())
                helper?.setText(R.id.tv_name2, item.fall_name)
                val drawable = resources.getDrawable(item.drawable)
                helper?.getView<ImageView>(R.id.iv_icon)!!.setImageDrawable(drawable)
                helper?.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    val intent = Intent()
                    intent.putExtra("type", item)
                    when (t) {
                        0 -> {
                            intent.setClass(this@WalletTypeActivity, CreateWalletActivity::class.java)
                            startActivity(intent)
                        }
                        1 -> {
                            intent.setClass(this@WalletTypeActivity, ImportWalletActivity::class.java)
                            startActivity(intent)
                        }
                        2 -> {
                            setResult(Activity.RESULT_OK, intent)
                        }
                        3 -> {
                            setResult(Activity.RESULT_OK, intent)
                        }
                    }

                    finish()
                }
            }
        }
        rcv_coin.layoutManager = LinearLayoutManager(this)
        rcv_coin.adapter = adapter
        val list = WaltType.values().toMutableList()
        list.remove(WaltType.USDT)
        list.remove(WaltType.EOS)
        adapter!!.addNewData(list)
    }
}
