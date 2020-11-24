package com.yjy.wallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.jaeger.library.StatusBarUtil
import com.tencent.tinker.lib.tinker.TinkerInstaller
import com.weiyu.baselib.base.BaseActivity
import com.yjy.wallet.Constant
import com.yjy.wallet.Constant.Companion.tinkerpaht
import com.yjy.wallet.R
import com.yjy.wallet.ui.fragment.FindFragment
import com.yjy.wallet.ui.fragment.IndexFragment
import com.yjy.wallet.ui.fragment.MyFragment
import com.zhl.cbdialog.CBDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_web1.*
import kotlin.system.exitProcess

/**
 *Created by weiweiyu
 *on 2019/4/29
 */
class MainActivity : BaseActivity() {
    //for receive customer msg from jpush server
    var fragments = arrayOf<Fragment>()
    var textviews = arrayOf<TextView>()
    override fun getContentLayoutResId(): Int = R.layout.activity_main


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    override fun initializeContentViews() {
        TinkerInstaller.onReceiveUpgradePatch(
                applicationContext,
                tinkerpaht
        )
        fragments = arrayOf(IndexFragment(), MyFragment(),FindFragment())
        textviews = arrayOf(tv_menu1_icon, tv_menu2_icon,tv_menu5_icon)
        textviews[0].isSelected = true
        supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, fragments[0])
                .show(fragments[0]).commit()
    }

    var index = 0
    var currentTabIndex = 0
    fun onclick(v: View) {
        when (v.id) {
            R.id.rl_menu1 -> {
                index = 0
            }
            R.id.rl_menu2 -> {
                index = 1
            }
            R.id.rl_menu5 -> {
                index = 2
            }
        }
        if (currentTabIndex != index) {
            val trx = supportFragmentManager.beginTransaction()
            trx.hide(fragments[currentTabIndex])
            textviews[currentTabIndex].isSelected = false
            if (!fragments[index].isAdded) {
                trx.add(R.id.fl_content, fragments[index])
            }
            trx.show(fragments[index]).commit()
            textviews[index].isSelected = true
        } else {
//            fragments[index].backTop()
        }
        currentTabIndex = index
    }

    var id = ""
    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setLightMode(this@MainActivity)
        StatusBarUtil.setTranslucentForImageView(this, 0, null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.repeatCount == 0) {
            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    private var mExitTime: Long = 0
    private fun exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this@MainActivity, resources.getString(R.string.exit_app), Toast.LENGTH_SHORT).show()
            mExitTime = System.currentTimeMillis()
        } else {
            finish()
            exitProcess(0)
        }
    }

    fun btn1(v: View) {
        if (!Constant.first) {
            show()
        } else
            startActivity(CreateWalletActivity::class.java)

    }

    @SuppressLint("CheckResult")
    fun btn2(v: View) {
        val intent = Intent(this, ImportWalletActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        webview?.destroy()
        super.onDestroy()
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
