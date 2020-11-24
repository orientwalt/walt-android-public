package com.yjy.wallet.activity

import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.bean.Address
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.activity_addr_info.*
import org.litepal.LitePal

/**
 *Created by weiweiyu
 *on 2019/6/5
 * 添加地址本
 */
class AddrInfoActivity : BaseActivity() {
    var position = 0
    override fun getContentLayoutResId(): Int = R.layout.activity_addr_info
    override fun initializeContentViews() {
        var address = intent.getSerializableExtra("data") as Address
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        val waltType = WaltType.values()[address.type]
        et_name.text = address.user
        iv_coin.setImageDrawable(resources.getDrawable(waltType.drawable))
        tv_type.text = waltType.name
        et_address.text = address.address
        if (TextUtils.isEmpty(address.remarck)) {
            et_remark.visibility = View.GONE
            v_line.visibility = View.GONE
        } else {
            et_remark.text = address.remarck
        }

        btn_delete.setOnClickListener {
            LitePal.deleteAllAsync(Address::class.java, "time = ${address.time}").listen { finish() }
        }
        rl_copy.setOnClickListener {
            var s = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            s.text = et_address.text
            toast(resources.getString(R.string.receive_copy_toast))
        }
    }

}