package com.yjy.wallet.wallet

import android.text.TextUtils
import com.google.gson.Gson
import com.weiyu.baselib.util.PrefUtils
import com.yjy.wallet.bean.UpdateW
import de.greenrobot.event.EventBus

class MyWalletUtils private constructor() {
    private var walletinfo by PrefUtils("WalletInfo", "")

    companion object StaticParams {
        val instance: MyWalletUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MyWalletUtils()
        }
    }

    fun saveWallet(wallet: YWallet) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        if (walletInfo1.wList.isNullOrEmpty()) {
            wallet.show = true
        }
        walletInfo1.wList.add(wallet)
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
        EventBus.getDefault().post(UpdateW())
    }

    fun saveWallet(wallet: MutableList<YWallet>) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.addAll(wallet)
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun delete(wallet: YWallet) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.remove(wallet)
        if (wallet.show && walletInfo1.wList.size > 0) {
            walletInfo1.wList[0].show = true
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun updateName(wallet: YWallet, name: String) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            if (it.remark.equals(wallet.remark)) {
                it.remark = name
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
        EventBus.getDefault().post(UpdateW())
    }

    fun showMain(name: String, open: Boolean) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            if (it.show) {
                it.map.forEach { item ->
                    if (item.key == name) {
                        item.value.show = open
                    }
                }
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun updateWords(wallet: YWallet) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            if (it.remark.equals(wallet.remark)) {
                it.words = ""
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }


    fun updateCheckInfo(wInfo: WInfo) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            it.map.forEach { item ->
                if (item.key == wInfo.unit && item.value.address == wInfo.address) {
                    it.map[wInfo.unit] = wInfo
                }
            }
            it.ercmMap.forEach { item ->
                if (item.key == wInfo.contract_address&&item.value?.address==wInfo.address) {
                    it.ercmMap[wInfo.contract_address] = wInfo
                }
            }
            it.wenMap.forEach { item ->
                if (item.key == wInfo.unit && item.value?.address == wInfo.address) {
                    it.wenMap[wInfo.unit] = wInfo
                }
            }
            it.htdfercmMap.forEach { item ->
                if (item.key == wInfo.contract_address&&item.value?.address==wInfo.address) {
                    it.htdfercmMap[wInfo.contract_address] = wInfo
                }
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun addUSDT(wInfo: WInfo) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            if (it.show) {
                it.wenMap[wInfo.unit] = wInfo
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun getUSDTWalt(): MutableList<WInfo> {
        val list = mutableListOf<WInfo>()
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return mutableListOf()
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        walletInfo.wList.forEach {
            if (it.wType == WaltType.BTC.ordinal) {
                if (it.map[WaltType.USDT.name] != null) {
                    list.add(it.map[WaltType.USDT.name]!!)
                }

            }
            if (it.wType == 100) {
                if (it.wenMap[WaltType.USDT.name] != null) {
                    list.add(it.wenMap[WaltType.USDT.name]!!)
                }
            }
        }
        return list
    }

    fun getUSDPWalt(): MutableList<WInfo> {
        val list = mutableListOf<WInfo>()
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return mutableListOf()
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        walletInfo.wList.forEach {
            if (it.wType == WaltType.usdp.ordinal) {
                if (it.map[WaltType.usdp.name] != null) {
                    list.add(it.map[WaltType.usdp.name]!!)
                }
            }
            if (it.wType == 100) {
                if (it.map[WaltType.usdp.name] != null) {
                    list.add(it.map[WaltType.usdp.name]!!)
                }
            }
        }
        return list
    }

    fun getUSDPYWallet(): MutableList<YWallet> {
        val list = mutableListOf<YWallet>()
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return mutableListOf()
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        walletInfo.wList.forEach {
            if (it.wType == WaltType.usdp.ordinal) {
                if (it.map[WaltType.usdp.name] != null) {
                    list.add(it)
                }
            }
            if (it.wType == 100) {
                if (it.map[WaltType.usdp.name] != null) {
                    list.add(it)
                }
            }
        }
        return list
    }

    fun getUSDTYWallet(): MutableList<YWallet> {
        val list = mutableListOf<YWallet>()
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return mutableListOf()
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        walletInfo.wList.forEach {
            if (it.wType == WaltType.BTC.ordinal) {
                if (it.map[WaltType.USDT.name] != null) {
                    list.add(it)
                }

            }
            if (it.wType == 100) {
                if (it.wenMap[WaltType.USDT.name] != null) {
                    list.add(it)
                }
            }
        }
        return list
    }

    fun getHTDFWallet(): MutableList<YWallet> {
        var list: MutableList<YWallet> = mutableListOf()
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return mutableListOf()
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        walletInfo.wList.forEach {
            if (it.wType == WaltType.htdf.ordinal) {
                if (it.map[WaltType.htdf.name] != null) {
                    list.add(it)
                }
            }
            if (it.wType == 100) {
                if (it.map[WaltType.htdf.name] != null) {
                    list.add(it)
                }
            }
        }
        return list
    }

    fun getHTDFWalt(): MutableList<WInfo> {
        val list = mutableListOf<WInfo>()
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return mutableListOf()
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        walletInfo.wList.forEach {
            if (it.wType == WaltType.htdf.ordinal) {
                if (it.map[WaltType.htdf.name] != null) {
                    list.add(it.map[WaltType.htdf.name]!!)
                }
            }
            if (it.wType == 100) {
                if (it.map[WaltType.htdf.name] != null) {
                    list.add(it.map[WaltType.htdf.name]!!)
                }
            }
        }
        return list
    }

    fun removeUSDT(key: String) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            if (it.show) {
                it.wenMap.remove(key)
                it.map.remove(key)
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun addERC(wInfo: WInfo) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            if (it.show) {
                it.ercmMap[wInfo.contract_address] = wInfo
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun addHtdfERC(wInfo: WInfo) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            if (it.show) {
                it.htdfercmMap[wInfo.contract_address] = wInfo
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun removeERC(key: String) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            if (it.show) {
                it.ercmMap.remove(key)
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun removehtdfERC(key: String) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            if (it.show) {
                it.htdfercmMap.remove(key)
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun updateInfo(wallet: YWallet) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        for (i in walletInfo1.wList.indices) {
            var w = walletInfo1.wList[i]
            if (w.remark.equals(wallet.remark)) {
                walletInfo1.wList[i] = wallet
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun update(wallet: YWallet) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            it.show = false
            if (it.remark.equals(wallet.remark)) {
                it.show = true
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun updateHTDF(wallet: YWallet) {
        var data = walletinfo
        var walletInfo1: WalletInfo = if (TextUtils.isEmpty(data)) {
            WalletInfo()
        } else {
            Gson().fromJson(data, WalletInfo::class.java)
        }
        walletInfo1.wList.forEach {
            it.htdf = false
            if (it.remark.equals(wallet.remark)) {
                it.htdf = true
            }
        }
        var jsonStr = Gson().toJson(walletInfo1)
        walletinfo = jsonStr
    }

    fun clear() {
        var data = walletinfo
        if (!TextUtils.isEmpty(data)) {
            val walletInfo1: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
            for (i in walletInfo1.wList.indices) {
                var yw = walletInfo1.wList[i]
                yw.map.forEach {
                    it.value.balance = "0"
                    it.value.account_number = "0"
                    it.value.hight = 0
                    it.value.sequence = "0"
                    yw.map[it.value.unit] = it.value
                }
            }
            var jsonStr = Gson().toJson(walletInfo1)
            walletinfo = jsonStr
        }

    }

    fun getWallet(): MutableList<YWallet>? {
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return null
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        return walletInfo.wList
    }

    fun getALLHtdf(): MutableList<String> {
        var w = instance.getWallet()
        var list = mutableListOf<String>()
        w?.forEach {
            var htdf = it.map[WaltType.htdf.name]
            if (htdf != null) {
                list.add(htdf.address)
            }
        }
        return list.distinct().toMutableList()
    }

    fun getWalletByAddr(address: String): MutableList<WInfo> {
        var list: MutableList<WInfo> = mutableListOf()
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return list
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        walletInfo.wList.forEach {
            it.map.forEach { item ->
                if (item.value.address == address) {
                    list.add(item.value)
                }
            }
        }
        return list
    }

    fun getHtdfListByAddr(address: String): MutableList<YWallet> {
        var list: MutableList<YWallet> = mutableListOf()
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return list
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        walletInfo.wList.forEach {
            it.map.forEach { item ->
                if (item.value.address == address) {
                    list.add(it)
                }
            }
        }
        return list
    }

    fun getCheckWallet(): YWallet? {
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return null
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        walletInfo.wList.forEach {
            if (it.show) {
                return it
            }
        }
        return null
    }

    fun getCheckHTDFWallet(): YWallet? {
        var data = walletinfo
        if (TextUtils.isEmpty(data)) {
            return null
        }
        val walletInfo: WalletInfo = Gson().fromJson(data, WalletInfo::class.java)
        walletInfo.wList.forEach {
            if (it.htdf) {
                return it
            }
        }
        return null
    }
}