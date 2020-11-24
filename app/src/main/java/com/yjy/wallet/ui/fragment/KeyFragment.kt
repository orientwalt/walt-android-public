package com.yjy.wallet.ui.fragment

import android.content.ClipboardManager
import android.content.Context
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.base.BaseFragment
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.fragment_key.*

/**
 *Created by weiweiyu
 *on 2019/4/30
 * keystore内容
 */
class KeyFragment : BaseFragment() {

    override fun getContentLayoutResId(): Int = R.layout.fragment_key

    override fun initializeContentViews() {

    }

    override fun viewCreated() {
        tv_key.text = activity!!.intent.getStringExtra("key")
        btn_copy.setOnClickListener {
            var s = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            s.text = tv_key.text
            (activity as BaseActivity).toast(resources.getString(R.string.receive_copy_toast))
        }
    }


}