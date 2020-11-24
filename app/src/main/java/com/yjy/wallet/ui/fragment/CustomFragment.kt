package com.yjy.wallet.ui.fragment

import android.text.TextUtils
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.base.BaseFragment
import com.yjy.wallet.R
import com.yjy.wallet.utils.CoinUtils
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.fragment_custom.*

/**
 * weiweiyu
 * 2019/8/22
 * 575256725@qq.com
 * 13115284785
 */

class CustomFragment : BaseFragment() {
    override fun getContentLayoutResId(): Int = R.layout.fragment_custom

    override fun initializeContentViews() {
    }

    override fun viewCreated() {
//        et_address.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (!TextUtils.isEmpty(et_address.text) && CoinUtils.checkAddress(WaltType.ETH, et_address.text.toString())) {
//                    ERCType.values().forEach {
//                        if (it.address.toLowerCase() == et_address.text.toString().toLowerCase()) {
//                            et_search.setText(it.name)
//                            et_decimal.setText(it.factor.toString())
//                            et_search.isEnabled = false
//                            et_decimal.isEnabled = false
//                        }
//                    }
//                } else {
//                    et_search.setText("")
//                    et_decimal.setText("")
//                    et_search.isEnabled = true
//                    et_decimal.isEnabled = true
//                }
//            }
//        })

    }

    fun getBean(): String? {
        val address = et_address.text.toString().trim()
//        val name = et_search.text.toString().trim()
//        val decimal = et_decimal.text.toString().trim()
        if (TextUtils.isEmpty(address)) {
            (activity as BaseActivity).toast(resources.getString(R.string.add_addr_addr_hint))
            return null
        }
        if (!CoinUtils.checkAddress(WaltType.ETH, address)) {
            (activity as BaseActivity).toast(resources.getString(R.string.send_address_error))
            return null
        }
//        if (TextUtils.isEmpty(name)) {
//            (activity as BaseActivity).toast(resources.getString(R.string.add_token_custom_hint4))
//            return null
//        }
//        if (TextUtils.isEmpty(decimal)) {
//            (activity as BaseActivity).toast(resources.getString(R.string.add_token_custom_hint5))
//            return null
//        }
//        val bean = ERCBean(name.toUpperCase(), R.mipmap.erc_icon, address, name.toUpperCase())
//        bean.decimal = decimal.toInt()
        return address
    }
}