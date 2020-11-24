package com.yjy.wallet.chainutils.bch

import android.text.TextUtils
import com.github.kiulian.converter.AddressConverter
import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.tinker.util.BaseApplicationContext
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import org.bitcoinj.core.*
import java.util.*

class BCHUtils {

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
    fun sign(fromAddress: String, toAddress1: String, ecKey: ECKey, amount: Long, fee: Long, utxos: List<UTXO>?, remark: String): Transaction {
        val networkParameters = Constant().currentNetworkParams
//        val fromAddress = AddressConverter.toCashAddress(fromAddress1)//BCH类型地址
        var toAddress = toAddress1
        if (toAddress.startsWith("bitcoincash")) {
            toAddress = AddressConverter.toLegacyAddress(toAddress1)
        }
        val transaction = BCHTransaction(networkParameters)
        if (!TextUtils.isEmpty(remark))
            transaction.memo = remark
        transaction.setVersion(2)
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
        if (changeAmount >= 546) {
            transaction.addOutput(Coin.valueOf(changeAmount), Address.fromString(networkParameters, fromAddress))
        }
        //输入未消费列表项
        for (utxo in needUtxos) {
            val outPoint = TransactionOutPoint(networkParameters, utxo.index, utxo.hash)
            transaction.addSignedInput(outPoint, utxo.value, utxo.script, ecKey, Transaction.SigHash.ALL, true, true)
        }
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
