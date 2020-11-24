package com.yjy.wallet.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.lqr.adapter.OnItemClickListener
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.AesUtils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.chainutils.dash.DashUtils
import com.yjy.wallet.chainutils.ltc.LTCUtils
import com.yjy.wallet.chainutils.neo.ECKeyPair
import com.yjy.wallet.wallet.*
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_coin.*
import org.bitcoinj.core.Bech32
import org.bitcoinj.core.ECKey
import org.stellar.sdk.KeyPair
import org.web3j.crypto.Wallet
import org.web3j.utils.Numeric

/**
 * Created by weiweiyu
 * on 2019/5/7
 * 币种选择
 */
class CoinActivity : BaseActivity() {
    var adapter: LQRAdapterForRecyclerView<WInfo>? = null
    override fun getContentLayoutResId(): Int = R.layout.activity_coin

    override fun initializeContentViews() {
        tb_title.setLeftLayoutClickListener(View.OnClickListener { finish() })
        adapter = object : LQRAdapterForRecyclerView<WInfo>(this, arrayListOf(), R.layout.adapter_coin_item2) {
            override fun convert(helper: LQRViewHolderForRecyclerView?, item: WInfo, position: Int) {
                val type = WaltType.valueOf(item.unit)
                helper?.setText(R.id.tv_name, item.unit.toUpperCase())
                helper?.setText(R.id.tv_name2, type.fall_name)
                helper?.getView<ImageView>(R.id.iv_icon)!!.setImageDrawable(resources.getDrawable(type.drawable))
                helper.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    showProgressDialog("")
                    if (intent.getIntExtra("type", 0) == 2) {
                        if (item?.unit == WaltType.htdf.name || item?.unit == WaltType.usdp.name) {
                            Flowable.just("")
                                    .compose(bindToLifecycle())
                                    .map {
                                        if (item.keystone.startsWith("{")) {
                                            var walletfile = KeyStoneUtil.getWalletFile(item.keystone)
                                            var toData = Bech32.decode(walletfile.address)
                                            if (toData.hrp != item.unit) {//第一版导出的是一个Keystore的地址统一是usdp
                                                val credentials = AllCredentials.create(
                                                        Wallet.decrypt(
                                                                intent.getStringExtra("pwd"),
                                                                walletfile
                                                        )
                                                )
                                                val ecKey = ECKey.fromPrivate(credentials.ecKeyPair.privateKey)
                                                val payloadBytes2 =
                                                        BitcoinCashBitArrayConverter.convertBits(ecKey.pubKeyHash, 8, 5, true)
                                                val address = Bech32.encode(item!!.unit, payloadBytes2)
                                                walletfile.address = address
                                                Gson().toJson(walletfile)
                                            } else {
                                                item.keystone
                                            }
                                        } else {
                                            var keyHax = AesUtils.decrypt(item.keystone, intent.getStringExtra("pwd"))
                                            val address = item.address
                                            var walletfile = KeyStoneUtil.createWalletFile(intent.getStringExtra("pwd"), org.web3j.crypto.ECKeyPair.create(Numeric.hexStringToByteArray(keyHax)), false, address)
                                            Gson().toJson(walletfile)
                                        }

                                    }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(object : ResourceSubscriber<String>() {
                                        override fun onComplete() {
                                        }

                                        override fun onNext(t: String?) {
                                            dismissProgressDialog()
                                            t ?: return
                                            keystrore(t)
                                            finish()
                                        }

                                        override fun onError(t: Throwable?) {
                                            dismissProgressDialog()
                                        }
                                    })

                        } else {
                            if (item.keystone.startsWith("{")) {
                                dismissProgressDialog()
                                keystrore(item.keystone)
                                finish()
                            } else {
                                Flowable.just("")
                                        .compose(bindToLifecycle())
                                        .map {
                                            var keyHax = AesUtils.decrypt(item.keystone, intent.getStringExtra("pwd"))
                                            val address = item.address
                                            var walletfile = KeyStoneUtil.createWalletFile(intent.getStringExtra("pwd"), org.web3j.crypto.ECKeyPair.create(Numeric.hexStringToByteArray(keyHax)), false, address)
                                            Gson().toJson(walletfile)
                                        }
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(object : ResourceSubscriber<String>() {
                                            override fun onComplete() {

                                            }

                                            override fun onNext(t: String) {
                                                finish()
                                                dismissProgressDialog()
                                                keystrore(t)
                                            }

                                            override fun onError(t: Throwable?) {
                                                dismissProgressDialog()
                                            }
                                        })

                            }

                        }
                    } else {
                        Flowable.just("")
                                .compose(bindToLifecycle())
                                .map {
                                    val ecKey = if (item.keystone.startsWith("{")) {
                                        var walletfile = KeyStoneUtil.getWalletFile(item.keystone)
                                        var credentials = AllCredentials.create(Wallet.decrypt(intent.getStringExtra("pwd"), walletfile))
                                        ECKey.fromPrivate(credentials.ecKeyPair.privateKey)
                                    } else {
                                        var keyHax = AesUtils.decrypt(item.keystone, intent.getStringExtra("pwd"))
                                        ECKey.fromPrivate(Numeric.hexStringToByteArray(keyHax))
                                    }
                                    when (item.unit) {
                                        WaltType.BTC.name, WaltType.BSV.name, WaltType.BCH.name, WaltType.USDT.name
                                            , WaltType.CXC.name, WaltType.QTUM.name
                                        -> {
                                            ecKey.getPrivateKeyAsWiF(Constant().currentNetworkParams)
                                        }
                                        WaltType.NEO.name -> ECKeyPair.create(ecKey.privKey).exportAsWIF()
                                        WaltType.ETH.name -> "0x${ecKey.privateKeyAsHex}"
                                        WaltType.LTC.name -> LTCUtils.getKey(ecKey.privKeyBytes)
                                        WaltType.DASH.name -> DashUtils.getKey(ecKey.privKeyBytes)
                                        WaltType.EOS.name -> ""
                                        WaltType.XLM.name -> {
                                            var c = KeyPair.fromSecretSeed(ecKey.privKeyBytes).secretSeed
                                            var s = StringBuffer()
                                            c.forEach { s.append(it.toUpperCase()) }
                                            s.toString()
                                        }
                                        else -> ecKey.privateKeyAsHex
                                    }
                                }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : ResourceSubscriber<String>() {
                                    override fun onComplete() {
                                    }

                                    override fun onNext(t: String?) {
                                        dismissProgressDialog()
                                        t ?: return
                                        privateKey(t)
                                        finish()
                                    }

                                    override fun onError(t: Throwable?) {
                                        dismissProgressDialog()
                                    }
                                })

                    }
                }
            }
        }

        rcv_coin.layoutManager = LinearLayoutManager(this)
        rcv_coin.adapter = adapter
        var list: MutableList<WInfo> = arrayListOf()
        Gson().fromJson<MutableMap<String, WInfo>>(
                intent.getStringExtra("data"),
                object : TypeToken<MutableMap<String, WInfo>>() {}.type
        ).forEach {
            list.add(it.value)
        }
        adapter!!.addNewData(list)
    }

    fun keystrore(key: String) {
        var intent = Intent(this, ExportKeyActivity::class.java)
        intent.putExtra("key", key)
        startActivity(intent)
    }

    fun privateKey(key: String) {
        var intent = Intent(this, ExportKeyActivity2::class.java)
        intent.putExtra("key", key)
        startActivity(intent)
    }

}
