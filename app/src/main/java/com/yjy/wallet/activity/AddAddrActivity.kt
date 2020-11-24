package com.yjy.wallet.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.View
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.R
import com.yjy.wallet.bean.Address
import com.yjy.wallet.utils.CoinUtils
import com.yjy.wallet.wallet.WaltType
import kotlinx.android.synthetic.main.activity_add_address.*

/**
 *Created by weiweiyu
 *on 2019/6/5
 * 添加地址本
 */
class AddAddrActivity : BaseActivity() {
    var type = WaltType.htdf
    override fun getContentLayoutResId(): Int = R.layout.activity_add_address
    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        tb_title.setRightText(resources.getString(R.string.save))
        tb_title.setRightLayoutClickListener(View.OnClickListener {
            add()
        })
        rl_scan.setOnClickListener {
            if (RxPermissions(this).isGranted(Manifest.permission.CAMERA)) {
                val intent = Intent(this@AddAddrActivity, ScanActivity::class.java)
                startActivityForResult(intent, 0x01)
            } else {
                RxPermissions(this).request(Manifest.permission.CAMERA)
                        .subscribe {
                            if (it) {
                                val intent = Intent(this@AddAddrActivity, ScanActivity::class.java)
                                startActivityForResult(intent, 0x01)
                            } else {
                                toast(resources.getString(R.string.request_permissions))
                            }
                        }
            }

        }
        setText()
        rl_type.setOnClickListener {
            var intent = Intent(this@AddAddrActivity, WalletTypeActivity::class.java)
            intent.putExtra("type", 2)
            startActivityForResult(intent, 100)
        }
    }

    fun add() {
        val name = et_name.checkBlank(resources.getString(R.string.add_addr_name_hint)) ?: return
        val address = et_address.checkBlank(resources.getString(R.string.add_addr_addr_hint))
                ?: return
        if (!CoinUtils.checkAddress(type, address)) {
            toast(resources.getString(R.string.send_address_error))
            return
        }
        Address(type.ordinal, address, name, et_remark.text.toString(), System.currentTimeMillis()).saveOrUpdateAsync("address = ? and type = ?", address, type.ordinal.toString()).listen { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                if (data != null) {
                    type = data.getSerializableExtra("type") as WaltType
                    setText()
                }
            }
            if (requestCode == 0x01) {
                if (data != null) {
                    runOnUiThread {
                        et_address.setText(data.getStringExtra("sacanurl"))
                    }
                }
            }
        }
    }

    fun setText() {
        iv_coin.setImageDrawable(resources.getDrawable(type.drawable))
        tv_type.text = type.name.toUpperCase()
    }
}