package com.yjy.wallet.chainutils.qtum

import android.text.TextUtils
import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.tinker.util.BaseApplicationContext
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import org.bitcoinj.core.*

/**
 * weiweiyu
 * 2019/8/6
 * 575256725@qq.com
 * 13115284785
 */
class QTUMUtils {
    companion object {
        fun getAddress(pubKeyHash: ByteArray): String {
            return Base58.encodeChecked(if (Constant.main) 0x3A else 78, pubKeyHash)
        }

        @Throws(Exception::class)
        fun sign(fromAddress: String, toAddress: String, ecKey: ECKey, amount: Long, utxos: List<UTXO>, remark: String): Transaction {
            val networkParameters = if (Constant.main)QtumMainNetParams.get() else QtumTestNetParams.get()
            return sign(networkParameters, fromAddress, toAddress, ecKey, amount, utxos, remark)
        }

        @Throws(BackErrorException::class)
        fun sign(networkParameters: NetworkParameters, fromAddress: String, toAddress: String, ecKey: ECKey, amount: Long, utxos: List<UTXO>, rmarck: String): Transaction {
            val transaction = Transaction(networkParameters)
            if (!TextUtils.isEmpty(rmarck))
                transaction.memo = rmarck
            transaction.setVersion(2)
            var fees = (utxos.size.toLong() * 148 + 2 * 34 + 10) * 500
            var changeAmount = 0L
            var utxoAmount = 0L
            //获取未消费列表
            if (utxos.isEmpty()) {
                throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.coininfo_no_balance))
            }
            //遍历未花费列表，组装合适的item
            for (utxo in utxos) {
                utxoAmount = utxoAmount.plus(utxo.value.value)
            }

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
            for (utxo in utxos) {
                val outPoint = TransactionOutPoint(networkParameters, utxo.index, utxo.hash)
                transaction.addSignedInput(outPoint, utxo.script, ecKey, Transaction.SigHash.ALL, true)
            }
            return transaction
        }
    }
}