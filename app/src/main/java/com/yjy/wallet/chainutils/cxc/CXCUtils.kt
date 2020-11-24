package com.yjy.wallet.chainutils.cxc

import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.tinker.util.BaseApplicationContext
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import org.bitcoinj.core.*
import org.bitcoinj.script.Script

class CXCUtils {

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
    fun sign(fromAddress: String, toAddress: String, ecKey: ECKey, amount: Long, utxos: List<UTXO>?, remark: String): Transaction {
        val networkParameters = Constant().currentNetworkParams
        val transaction = Transaction(networkParameters)
        var changeAmount = 0L
        var utxoAmount = 0L
        //获取未消费列表
        if (utxos == null || utxos.isEmpty()) {
            throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.coininfo_no_balance))
        }
        //每次消费所有utxo 减少粉尘 fee固定
        for (utxo in utxos) {
            utxoAmount = utxoAmount.plus(utxo.value.value)
        }
        //消费列表总金额 - 已经转账的金额 - 手续费 就等于需要返回给自己的金额了
        changeAmount = utxoAmount - (amount + Coin.parseCoin((0.0001 / 100).toString()).longValue())
        //余额判断
        if (changeAmount < 0) {
            throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.send_amout_not_balance))
        }
        transaction.addOutput(Coin.valueOf(amount), Address.fromString(networkParameters, toAddress))
        //输出-转给自己(找零)
        if (changeAmount > 0) {
            transaction.addOutput(Coin.valueOf(changeAmount), Address.fromString(networkParameters, fromAddress))
        }
        //输入未消费列表项
        for (utxo in utxos) {
            val outPoint = TransactionOutPoint(networkParameters, utxo.index, utxo.hash)
            transaction.addSignedInput(outPoint, utxo.script, ecKey, Transaction.SigHash.ALL, true)
        }
        return transaction
    }

    @Throws(BackErrorException::class)
    fun signToken(fromAddress: String, toAddress: String, ecKey: ECKey, amount: Long, utxos: List<UTXO>?, remark: String): Transaction {
        val networkParameters = Constant().currentNetworkParams
        val transaction = Transaction(networkParameters)
        var changeAmount = 0L
        var utxoAmount = 0L
        //获取未消费列表
        if (utxos == null || utxos.isEmpty()) {
            throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.coininfo_no_balance))
        }
        //每次消费所有utxo 减少粉尘 fee固定
        for (utxo in utxos) {
            utxoAmount = utxoAmount.plus(utxo.value.value)
        }
        //消费列表总金额 - 已经转账的金额 - 手续费 就等于需要返回给自己的金额了
        changeAmount = utxoAmount - amount
        //余额判断
        if (changeAmount < 0) {
            throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.send_amout_not_balance))
        }
        //构建usdt的输出脚本 注意这里的金额是要乘10的8次方
        //583244-1358-57077 cpc  96-300-2319 ETH 96-721-64767 BTC 372121-1930-55680	 TEP
        //76a914048d701b628cd15c4f30c199976af74f1ef63cba88ac1c73706b7193cb187abb0f002fc9e5e49872bfdef5002d31010000000075
        val usdtHex = "6a146f6d6e69" + String.format("%016x", 31) + String.format("%016x", amount)
        transaction.addOutput(Coin.valueOf(0L), Script(Utils.HEX.decode(usdtHex)))
        transaction.addOutput(Coin.valueOf(0), Address.fromString(networkParameters, toAddress))
        //输出-转给自己(找零)
        if (changeAmount > 0) {
            transaction.addOutput(Coin.valueOf(changeAmount), Address.fromString(networkParameters, fromAddress))
        }
        //输入未消费列表项
        for (utxo in utxos) {
            val outPoint = TransactionOutPoint(networkParameters, utxo.index, utxo.hash)
            transaction.addSignedInput(outPoint, utxo.script, ecKey, Transaction.SigHash.ALL, true)
        }
        return transaction
    }
}
