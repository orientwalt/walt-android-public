package com.yjy.wallet.chainutils.eth

import android.annotation.SuppressLint
import com.weiyu.baselib.util.BLog
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger

/**
 *Created by weiweiyu
 *on 2019/5/31
 * ETH钱包查询转账
 */
class ETHUtils {

    //to地址,金额,from私钥
    @SuppressLint("CheckResult")
    fun sign(toAddress: String, amount: Double, key: Credentials, fee: Int, memo: String, limit: BigInteger, nonce: BigInteger?): String {
        var gasprice = Convert.toWei(fee.toString(), Convert.Unit.GWEI).toBigInteger()
        val value = Convert.toWei(amount.toString(), Convert.Unit.ETHER).toBigInteger()
        BLog.d("-------------------------------$value")
        val rawTransaction = RawTransaction.createTransaction(
                nonce, gasprice,
                limit, toAddress,
                value, memo)
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, key)
        return Numeric.toHexString(signedMessage)
    }

    //to地址,金额,from私钥
    @SuppressLint("CheckResult")
    fun signERC(toAddress: String, amount: Double, key: Credentials, fee: Int, contractaddress: String, decimal: Int, limit: BigInteger, nonce: BigInteger?): String {
        var gasprice = Convert.toWei(fee.toString(), Convert.Unit.GWEI).toBigInteger()
        val value = getValue(amount, decimal)
        val function = Function(
                "transfer",
                listOf(Address(toAddress), Uint256(value)),
                emptyList())
        //创建RawTransaction交易对象
        var encodedFunction = FunctionEncoder.encode(function)
        val rawTransaction = RawTransaction.createTransaction(nonce, gasprice, limit, contractaddress, encodedFunction)
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, key)
        return Numeric.toHexString(signedMessage)

    }

    fun getValue(amount: Double, decimal: Int): BigInteger {
        return if (decimal <= 6) {
            var s = BigDecimal.TEN.pow(decimal).toDouble().times(amount).toBigDecimal().setScale(0, BigDecimal.ROUND_HALF_UP).toBigInteger()
            s
        } else {
            var m = com.weiyu.baselib.util.Utils.toSubStringDegistForChart(amount, 6, true).replace(",", "").replace(".", "")
            var s = BigDecimal(m)
            var n = BigDecimal.TEN.pow(decimal - 6)
            s.times(n).toBigInteger()
        }
    }
}