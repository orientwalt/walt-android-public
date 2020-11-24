package com.yjy.wallet

import android.os.Environment
import com.weiyu.baselib.util.PrefUtils
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params


class Constant {
    var currentNetworkParams: NetworkParameters = if (main) MainNetParams.get() else TestNet3Params.get()

    companion object {
        var MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION"
        var KEY_MESSAGE = "message"
        var KEY_EXTRAS = "extras"

        var PWD_SIZE = 16//密码长度
        var NAME_SIZE = 20//钱包名称长度
        var WHAT_100 = 100
        var WHAT_101 = 101
        var WHAT_103 = 103
        var feeLimit = "20"//交易手续费
        var htdffeeLimit = 100L//交易手续费
        var hetIP = arrayOf("mainchain.hetcoin.info")
        var testHtdfIp = "htdf2020-test01.orientwalt.cn"
        var testUsdpIp = "39.108.52.9"
        var nodeHET = "testchain.hetcoin.info"
        var allAssetsView by PrefUtils("SP_Assets_View", true)
        var rmbP = 2
        var unit = "CNY"
        var priceP = 6//统一金额小数点位数
        var main by PrefUtils("SP_MAIN_TEST", true)
        var first by PrefUtils("SP_FIRST", false)
        var mainHTDFNet = ""//节点
        var mainUSDPNet = ""
        var mainHETNet = hetIP[0]
        var logopath = if (main) "" else "http://test-python1.oss-cn-shenzhen.aliyuncs.com"
        var waltpath = if (main) "" else "https://walt-test.oss-cn-shenzhen.aliyuncs.com"
        var tinkerpaht by PrefUtils("TINEK_PATH", Environment.getExternalStorageDirectory().absolutePath + "/wallet_tinker/patch_signed_7zip.apk")
        var mainChainRMB by PrefUtils("SP_MAIN_CHAIN_RMB", "")
    }
}