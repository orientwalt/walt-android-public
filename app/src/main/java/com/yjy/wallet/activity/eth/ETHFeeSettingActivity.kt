package com.yjy.wallet.activity.eth

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import kotlinx.android.synthetic.main.activity_eth_fee_setting.*

/**
 * weiweiyu
 * 2020/5/20
 * 575256725@qq.com
 * 13115284785
 */
class ETHFeeSettingActivity : BaseActivity() {
    override fun getContentLayoutResId(): Int = R.layout.activity_eth_fee_setting

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        var gas = intent.getIntExtra("gas", 10)
        var gaslimit = intent.getIntExtra("gaslimit", 21000)
        var nonce = intent.getIntExtra("nonce", 0)
        et_gas.setText(gas.toString())
        et_gaslimit.setText(gaslimit.toString())
        et_nonce.setText(nonce.toString())
        btn_commit.setOnClickListener {
            var gasStr = et_gas.checkBlank(resources.getString(R.string.eth_fee_hint3))
                    ?: return@setOnClickListener
            var gasLimitStr = et_gaslimit.checkBlank(resources.getString(R.string.eth_fee_hint4))
                    ?: return@setOnClickListener
            var nonceStr = et_nonce.text.toString()
            if (gasStr.toInt() < intent.getIntExtra("min", 10)) {
                toast(String.format(resources.getString(R.string.eth_fee_hint6), intent.getIntExtra("min", 10)))
                return@setOnClickListener
            }
            if (gasLimitStr.toInt() < 21000) {
                toast(resources.getString(R.string.eth_fee_hint5))
                return@setOnClickListener
            }
            var i = Intent()
            i.putExtra("gas", gasStr.toInt())
            i.putExtra("gaslimit", gasLimitStr.toInt())
            if (!TextUtils.isEmpty(nonceStr)) {
                i.putExtra("nonce", nonceStr.toInt())
            }
            setResult(Activity.RESULT_OK, i)
            finish()
        }
    }

}
