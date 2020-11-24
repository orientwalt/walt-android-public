package com.yjy.wallet.chainutils.btc

import android.text.TextUtils
import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.tinker.util.BaseApplicationContext
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import org.bitcoinj.core.*
import org.bitcoinj.script.Script
import org.bitcoinj.script.ScriptBuilder
import org.web3j.utils.Numeric
import java.util.*

class BTCUtils {

    fun getAddressHax(address:String):String{
        var s= ScriptBuilder.createOutputScript(Address.fromString(Constant().currentNetworkParams,address)).program
        return Numeric.toHexStringNoPrefix(s)
    }
    fun sign(fromAddress: String, toAddress: String, ecKey: ECKey, amount: Long, fee: Long, utxos: List<UTXO>, rmarck: String): Transaction {
        val networkParameters = Constant().currentNetworkParams
        return sign(networkParameters, fromAddress, toAddress, ecKey, amount, fee, utxos, rmarck)
    }

    /**
     * btc交易签名
     *
     * @param fromAddress
     * @param toAddress
     * @param privateKey
     * @param amount
     * @param fee
     * @param utxos
     * @return
     * @throws Exception
     */
    @Throws(BackErrorException::class)
    fun sign(networkParameters: NetworkParameters, fromAddress: String, toAddress: String, ecKey: ECKey, amount: Long, fee: Long, utxos: List<UTXO>?, rmarck: String): Transaction {
        val transaction = Transaction(networkParameters)
        if (!TextUtils.isEmpty(rmarck))
            transaction.memo = rmarck
        var fees = 0L
        var utxoSize = 0L
        var changeAmount = 0L
        var utxoAmount = 0L
        val needUtxos = ArrayList<UTXO>()
        //获取未消费列表
        if (utxos == null || utxos.isEmpty()) {
            throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.coininfo_no_balance))
        }
        //遍历未花费列表，组装合适的item
        for (utxo in utxos) {
            utxoSize++
            if (utxoAmount >= (amount + fees)) {
                break
            } else {
                needUtxos.add(utxo)
                utxoAmount = utxoAmount.plus(utxo.value.value)
                //size = inputsNum * 148 + outputsNum * 34 + 10 (+/-) 40
                fees = (utxoSize * 148 + 2 * 34 + 10) * fee
            }
        }
//        BTC小于0.00001免手续费
//        if (Coin.valueOf(amount).toFriendlyString().toDouble() < 0.00001) {
//            fees = 0
//        }
        //消费列表总金额 - 已经转账的金额 - 手续费 就等于需要返回给自己的金额了
        changeAmount = utxoAmount - (amount + fees)
        //余额判断
        if (changeAmount < 0) {
            throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.send_amout_not_balance))
        }
        transaction.addOutput(Coin.valueOf(amount), Address.fromString(networkParameters, toAddress))
        //输出-转给自己(找零)
        if (changeAmount > 546) {//太少了就给矿工
            transaction.addOutput(Coin.valueOf(changeAmount), Address.fromString(networkParameters, fromAddress))
        }
        //输入未消费列表项
        for (utxo in needUtxos) {
            val outPoint = TransactionOutPoint(networkParameters, utxo.index, utxo.hash)
            transaction.addSignedInput(outPoint, utxo.script, ecKey, Transaction.SigHash.ALL, true)
        }
        //        byte[] bytes = transaction.bitcoinSerialize();
        //        String hash = Hex.toHexString(transaction.bitcoinSerialize());
        return transaction
    }

    /**
     * usdt 离线签名
     *
     * @param privateKey：私钥
     * @param toAddress：接收地址
     * @param amount:转账金额
     * @return
     */
    @Throws(BackErrorException::class)
    fun omniSign(fromAddress: String, toAddress: String, ecKey: ECKey, amount1: Long, fee: Long, propertyid: Int?, utxos: List<UTXO>?, remark: String): Transaction {
        val networkParameters = Constant().currentNetworkParams
        val transaction = Transaction(networkParameters)
        if (!TextUtils.isEmpty(remark))
            transaction.memo = remark
        var fees = 0L
        var utxoSize = 0L
        var changeAmount = 0L
        var utxoAmount = 0L
        val needUtxos = ArrayList<UTXO>()
        //获取未消费列表
        if (utxos == null || utxos.isEmpty()) {
            throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.coininfo_no_balance2))
        }
        //遍历未花费列表，组装合适的item
        val miniBtc = 546L
        for (utxo in utxos) {
            utxoSize++
            if (utxoAmount >= (miniBtc + fees)) {
                break
            } else {
                needUtxos.add(utxo)
                utxoAmount = utxoAmount.plus(utxo.value.value)
                //size = inputsNum * 148 + outputsNum * 34 + 10 (+/-) 40
                fees = (utxoSize * 148 + 2 * 34 + 10) * fee
            }
        }
//        BTC小于0.00001免手续费
//        if (Coin.valueOf(amount).toFriendlyString().toDouble() < 0.00001) {
//            fees = 0
//        }
        //消费列表总金额 - 已经转账的金额 - 手续费 就等于需要返回给自己的金额了
        changeAmount = utxoAmount - (miniBtc + fees)
        //余额判断
        if (changeAmount < 0) {
            throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.coininfo_no_balance2))
        }

        //这是比特币的限制最小转账金额，所以很多usdt转账会收到一笔0.00000546的btc

        transaction.addOutput(Coin.valueOf(miniBtc), Address.fromString(networkParameters, toAddress))

        //构建usdt的输出脚本 注意这里的金额是要乘10的8次方
        val usdtHex = "6a146f6d6e69" + String.format("%016x", propertyid) + String.format("%016x", amount1)
        transaction.addOutput(Coin.valueOf(0L), Script(Utils.HEX.decode(usdtHex)))

        //输出-转给自己(找零)
        if (changeAmount > 546) {//太少了就给矿工
            transaction.addOutput(Coin.valueOf(changeAmount), Address.fromString(networkParameters, fromAddress))
        }
        //输入未消费列表项
        for (utxo in needUtxos) {
            val outPoint = TransactionOutPoint(networkParameters, utxo.index, utxo.hash)
            transaction.addSignedInput(outPoint, utxo.script, ecKey, Transaction.SigHash.ALL, true)
        }
        //        byte[] bytes = transaction.bitcoinSerialize();
        //        String hash = Hex.toHexString(transaction.bitcoinSerialize());
        return transaction

    }

    @Throws(BackErrorException::class)
    fun getFee(amount: Long, fee: Long, utxos: List<UTXO>): Long {
        if (utxos.isEmpty()) {
            return (148 + 2 * 34 + 10) * fee
        }
        var fees = 0L
        var utxoSize = 0L
        var utxoAmount = 0L
        for (utxo in utxos) {
            utxoSize++
            if (utxoAmount >= (amount + fees)) {
                break
            } else {
                utxoAmount = utxoAmount.plus(utxo.value.value)
                //size = inputsNum * 148 + outputsNum * 34 + 10 (+/-) 40
                fees = (utxoSize * 148 + 2 * 34 + 10) * fee
            }
        }
        return fees
    }
}
