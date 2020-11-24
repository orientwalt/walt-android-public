package com.yjy.wallet.chainutils.usdp

import android.annotation.SuppressLint
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.wallet.WInfo
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.nio.charset.StandardCharsets


class USDPSign {
    companion object {
        //创建未签名广播json
        fun getNotSignTransaction(from: String, to: String, amount: String, unit: String, remark: String, fee: String, type: Boolean): UsdpTransaction {
            val bigDecimal = BigDecimal(amount.toDouble().times(100000000))
            val satoshi=bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).toString()
            var amountList = mutableListOf<AmountItem>()
            amountList.add(AmountItem(satoshi, unit))
            val msgValue = MsgValue2(amountList, from, to)
//            val msg = Msg("htdfservice/send", msgValue)
            val msg = Msg(if (type) "hetservice/send" else "htdfservice/send", msgValue)
            var msgList = mutableListOf<Msg>()
            msgList.add(msg)
            val feeList = mutableListOf<AmountItem>()
            feeList.add(AmountItem(fee, unit))
            val fee1 = Fees()
            fee1.gas = "200000"
            fee1.amount = feeList
            val usdpValue = UsdpValue(msgList, fee1, null, remark)
            return UsdpTransaction("auth/StdTx", usdpValue)
        }

        //生成签名广播json
        @SuppressLint("NewApi")
        fun getSignTransaction(myWallet: WInfo, key: ECKey, usdpTransaction: UsdpTransaction): UsdpTransaction {
//            var str = Gson().toJson(usdpTransaction.value.msg[0].value)
//            var msg = Gson().fromJson<MsgValue>(str, MsgValue::class.java)
            //创建需要签名的json字符串
            var signJson = JSONObject()
            signJson.put("account_number", myWallet.account_number)//账号id
            signJson.put("chain_id", if (main) "mainchain" else "testchain")//节点名称
            signJson.put("fee", JSONObject(Gson().toJson(usdpTransaction.value.fee)))
            signJson.put("memo", usdpTransaction.value.memo)
            signJson.put("msgs", JSONArray().put(JSONObject(Gson().toJson(usdpTransaction.value.msg[0].value))))
            signJson.put("sequence", myWallet.sequence)
            val sha256Hash = Sha256Hash.of(signJson.toString().replace("\\\\", "").toByteArray(StandardCharsets.UTF_8))
            val sig = key.sign(sha256Hash)
            var r = Utils.bigIntegerToBytes(sig.r, 32)
            var s = Utils.bigIntegerToBytes(sig.s, 32)
            val concatenatedBytes = ByteArray(r.size + s.size)
            System.arraycopy(r, 0, concatenatedBytes, 0, r.size)
            System.arraycopy(s, 0, concatenatedBytes, r.size, s.size)
            val signature = android.util.Base64.encodeToString(concatenatedBytes, android.util.Base64.DEFAULT)
            val pKey = PubKey("tendermint/PubKeySecp256k1", Base64.encodeToString(key.pubKey, Base64.DEFAULT))
            var sList = mutableListOf<Signatures>()
            sList.add(Signatures(signature, pKey))
            usdpTransaction.value.signatures = sList
            return usdpTransaction
        }

        //广播json编码
        fun getTx(signTransaction: UsdpTransaction): String {
            var gson = GsonBuilder().serializeNulls().disableHtmlEscaping().create()
            return Utils.HEX.encode(gson.toJson(signTransaction).toByteArray())
        }
    }
}