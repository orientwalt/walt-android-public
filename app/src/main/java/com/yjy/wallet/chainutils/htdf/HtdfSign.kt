package com.yjy.wallet.chainutils.htdf

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Base64
import com.google.gson.Gson
import com.yjy.wallet.Constant.Companion.main
import com.yjy.wallet.chainutils.usdp.AmountItem
import com.yjy.wallet.chainutils.usdp.PubKey
import com.yjy.wallet.chainutils.usdp.Signatures
import com.yjy.wallet.wallet.BitcoinCashBitArrayConverter
import com.yjy.wallet.wallet.WInfo
import org.bitcoinj.core.Bech32
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils
import org.json.JSONArray
import org.json.JSONObject
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.StandardCharsets


class HtdfSign {
    companion object {
        //创建未签名广播json
        fun getNotSignTransaction(from: String, to: String, amount: String, unit: String, remark: String, fee: Long, geswant: Long, data: String): HtdfTransaction {
            val bigDecimal = BigDecimal(amount.toDouble().times(100000000))
            val satoshi = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).toString()
            var amountList = mutableListOf<AmountItem>()
            amountList.add(AmountItem(satoshi, unit))
            val msgValue = MsgValue(amountList, from, to, data, fee.toString(), geswant.toString())
            val msg = Msg("htdfservice/send", msgValue)
            var msgList = mutableListOf<Msg>()
            msgList.add(msg)
            val fee1 = Fees()
            fee1.gas_price = fee.toString()
            fee1.gas_wanted = geswant.toString()
            val usdpValue = HtdfValue(msgList, fee1, null, remark)
            return HtdfTransaction("auth/StdTx", usdpValue)
        }

        //转账
        fun getSignTransaction(myWallet: WInfo, key: ECKey, usdpTransaction: HtdfTransaction?): HtdfTransaction? {
            return getSignTransaction(myWallet, key, usdpTransaction, "")
        }

        //生成签名广播json
        @SuppressLint("NewApi")
        fun getSignTransaction(myWallet: WInfo, key: ECKey, usdpTransaction: HtdfTransaction?, data: String): HtdfTransaction? {
            usdpTransaction ?: return null
            if (!TextUtils.isEmpty(data)) {
                usdpTransaction.value.msg[0].value.Data = data
            }
            //创建需要签名的json字符串
            var signJson = JSONObject()
            signJson.put("account_number", myWallet.account_number)//账号id
            signJson.put("chain_id", if (main) "mainchain" else "testchain")//节点名称
            signJson.put("fee", JSONObject(Gson().toJson(usdpTransaction.value.fee)))
            signJson.put("memo", usdpTransaction.value.memo)
            var msgJson = JSONObject(Gson().toJson(usdpTransaction.value.msg[0].value))
            msgJson.put("GasPrice", msgJson.getString("GasPrice").toLong())
            msgJson.put("GasWanted", msgJson.getString("GasWanted").toLong())
            signJson.put("msgs", JSONArray().put(msgJson))
            signJson.put("sequence", myWallet.sequence)
            val signature = sign(signJson.toString(), key)
            val pKey = PubKey("tendermint/PubKeySecp256k1", Base64.encodeToString(key.pubKey, Base64.DEFAULT))
            var sList = mutableListOf<Signatures>()
            sList.add(Signatures(signature, pKey))
            usdpTransaction.value.signatures = sList
            return usdpTransaction
        }

        //hrc-20代币转账
        @SuppressLint("NewApi")
        fun contract(myWallet: WInfo, key: ECKey, usdpTransaction: HtdfTransaction?, to: String, amount: Double): HtdfTransaction? {
            usdpTransaction ?: return null
            val value = getValue(amount, myWallet.decimal)
            var adddata = Bech32.decode(to).data
            var d = BitcoinCashBitArrayConverter.convertBits(adddata, 5, 8, true)
            val function = Function(
                    "transfer",
                    listOf(Address(Numeric.toHexString(d)), Uint256(value)),
                    emptyList())
            //创建RawTransaction交易对象
            var data = FunctionEncoder.encode(function)
            return getSignTransaction(myWallet, key, usdpTransaction, data.substring(2))
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

        //委托
        @SuppressLint("NewApi")
        fun mortgage(myWallet: WInfo, key: ECKey, to: String, price: String, memo: String): String {
            val bigDecimal = BigDecimal(price.toDouble().times(100000000))
            val satoshi = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).toString()
            var jsonstr = "{\"account_number\":\"${myWallet.account_number}\"," +
                    "\"chain_id\":\"${if (main) "mainchain" else "testchain"}\"," +
                    "\"fee\":{\"gas_price\":\"100\",\"gas_wanted\":\"30000\"}," +
                    "\"memo\":\"${memo}\"," +
                    "\"msgs\":[{" +
                    "\"type\":\"htdf/MsgDelegate\"," +
                    "\"value\":{" +
                    "\"amount\":{\"amount\":\"${satoshi}\",\"denom\":\"satoshi\"}," +
                    "\"delegator_address\":\"${myWallet.address}\"," +
                    "\"validator_address\":\"${to}\"" +
                    "}" +
                    "}]," +
                    "\"sequence\":\"${myWallet.sequence}\"}"
            val signature = sign(jsonstr, key)
            var json = "{\"type\":\"auth/StdTx\"," +
                    "\"value\":{\"fee\":{\"gas_price\":\"100\",\"gas_wanted\":\"30000\"},\"memo\":\"${memo}\"," +
                    "\"msg\":[{\"type\":\"htdf/MsgDelegate\"," +
                    "\"value\":{\"amount\":{\"amount\":\"${satoshi}\",\"denom\":\"satoshi\"}," +
                    "\"delegator_address\":\"${myWallet.address}\",\"type\":\"htdf/MsgDelegate\"," +
                    "\"validator_address\":\"${to}\"}}]," +
                    "\"signatures\":[{\"pub_key\":{\"type\":\"tendermint/PubKeySecp256k1\"," +
                    "\"value\":\"${Base64.encodeToString(key.pubKey, Base64.DEFAULT).replace("\n", "")}\"}," +
                    "\"signature\":\"${signature}\"}]}}"
            return json
        }

        @SuppressLint("NewApi")
        fun reward(myWallet: WInfo, key: ECKey, to: String, memo: String): String {
            var jsonstr = "{\"account_number\":\"${myWallet.account_number}\"," +
                    "\"chain_id\":\"${if (main) "mainchain" else "testchain"}\"," +
                    "\"fee\":{\"gas_price\":\"100\",\"gas_wanted\":\"30000\"}," +
                    "\"memo\":\"${memo}\"," +
                    "\"msgs\":[{" +
                    "\"type\":\"htdf/MsgWithdrawDelegationReward\"," +
                    "\"value\":{" +
                    "\"delegator_address\":\"${myWallet.address}\"," +
                    "\"validator_address\":\"${to}\"" +
                    "}" +
                    "}]," +
                    "\"sequence\":\"${myWallet.sequence}\"}"
            val signature = sign(jsonstr, key)
            var json = "{\"type\":\"auth/StdTx\"," +
                    "\"value\":{\"fee\":{\"gas_price\":\"100\",\"gas_wanted\":\"30000\"},\"memo\":\"$memo\"," +
                    "\"msg\":[" +
                    "{\"type\":\"htdf/MsgWithdrawDelegationReward\"," +
                    "\"value\":{" +
                    "\"delegator_address\":\"${myWallet.address}\",\"type\":\"htdf/MsgWithdrawDelegationReward\"," +
                    "\"validator_address\":\"${to}\"}" +
                    "}" +
                    "]," +
                    "\"signatures\":[{\"pub_key\":{\"type\":\"tendermint/PubKeySecp256k1\"," +
                    "\"value\":\"${Base64.encodeToString(key.pubKey, Base64.DEFAULT).replace("\n", "")}\"}," +
                    "\"signature\":\"${signature}\"}]}}"
            return json
        }

        @SuppressLint("NewApi")
        fun Undelegate(myWallet: WInfo, key: ECKey, to: String, price: String, memo: String): String {
            val bigDecimal = BigDecimal(price.toDouble().times(100000000))
            val satoshi = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).toString()
            var jsonstr = "{\"account_number\":\"${myWallet.account_number}\"," +
                    "\"chain_id\":\"${if (main) "mainchain" else "testchain"}\"," +
                    "\"fee\":{\"gas_price\":\"100\",\"gas_wanted\":\"30000\"}," +
                    "\"memo\":\"${memo}\"," +
                    "\"msgs\":[{" +
                    "\"type\":\"htdf/MsgUndelegate\"," +
                    "\"value\":{" +
                    "\"amount\":{\"amount\":\"${satoshi}\",\"denom\":\"satoshi\"}," +
                    "\"delegator_address\":\"${myWallet.address}\"," +
                    "\"validator_address\":\"${to}\"" +
                    "}" +
                    "}]," +
                    "\"sequence\":\"${myWallet.sequence}\"}"
            val signature = sign(jsonstr, key)
            var json = "{\"type\":\"auth/StdTx\"," +
                    "\"value\":{\"fee\":{\"gas_price\":\"100\",\"gas_wanted\":\"30000\"},\"memo\":\"${memo}\"," +
                    "\"msg\":[{\"type\":\"htdf/MsgUndelegate\"," +
                    "\"value\":{\"amount\":{\"amount\":\"${satoshi}\",\"denom\":\"satoshi\"}," +
                    "\"delegator_address\":\"${myWallet.address}\",\"type\":\"htdf/MsgUndelegate\"," +
                    "\"validator_address\":\"${to}\"}}]," +
                    "\"signatures\":[{\"pub_key\":{\"type\":\"tendermint/PubKeySecp256k1\"," +
                    "\"value\":\"${Base64.encodeToString(key.pubKey, Base64.DEFAULT).replace("\n", "")}\"}," +
                    "\"signature\":\"${signature}\"}]}}"
            return json
        }

        //修改节点资料
        @SuppressLint("NewApi")
        fun update(key: ECKey): String {
            var jsonstr = "{\"account_number\":\"${35}\",\"chain_id\":\"${if (main) "mainchain" else "testchain"}\",\"fee\":{\"gas_price\":\"100\",\"gas_wanted\":\"200000\"},\"memo\":\"\",\"msgs\":[{\"type\":\"htdf/MsgEditValidator\",\"value\":{\"Description\":{\"details\":\"To infinity and beyond!\",\"identity\":\"23870f5bb12ba2c4967c46db\",\"moniker\":\"xxx\",\"website\":\"https://htdf.network\"},\"address\":\"htdfvaloper1v8j6r7ttfac07nuhy8uhxgumy7442ck532287d\",\"commission_rate\":\"0.150000000000000000\",\"min_self_delegation\":null}}],\"sequence\":\"${6}\"}"
            val signature = sign(jsonstr, key)
            var json = "{\"type\":\"auth/StdTx\",\"value\":{\"msg\":[{\"type\":\"htdf/MsgEditValidator\",\"value\":{\"Description\":{\"moniker\":\"xxx\",\"identity\":\"23870f5bb12ba2c4967c46db\",\"website\":\"https://htdf.network\",\"details\":\"To infinity and beyond!\"},\"address\":\"htdfvaloper1v8j6r7ttfac07nuhy8uhxgumy7442ck532287d\",\"commission_rate\":\"0.150000000000000000\",\"min_self_delegation\":null}}],\"fee\": {\"gas_wanted\":\"200000\",\"gas_price\":\"100\"},\"signatures\":[{\"pub_key\":{\"type\":\"tendermint/PubKeySecp256k1\",\"value\":\"${Base64.encodeToString(key.pubKey, Base64.DEFAULT).replace("\n", "")}\"},\"signature\":\"${signature}\"}],\"memo\":\"\"}}"
            return json
        }

        //修改节点资料
        @SuppressLint("NewApi")
        fun reward(key: ECKey): String {
            var jsonstr = "{\"account_number\":\"0\",\"chain_id\":\"testchain\",\"fee\":{\"gas_price\":\"100\",\"gas_wanted\":\"200000\"},\"memo\":\"\",\"msgs\":[{\"type\":\"htdf/MsgWithdrawDelegationReward\",\"value\":{\"delegator_address\":\"htdf10fjsnx05ewesqjlmy5pesxzwa2t7z4e6vvqxvj\",\"validator_address\":\"htdfvaloper10fjsnx05ewesqjlmy5pesxzwa2t7z4e6x4clme\"}},{\"type\":\"htdf/MsgWithdrawValidatorCommission\",\"value\":{\"validator_address\":\"htdfvaloper10fjsnx05ewesqjlmy5pesxzwa2t7z4e6x4clme\"}}],\"sequence\":\"1\"}"
            val signature = sign(jsonstr, key)
            var json = "{\"type\":\"auth/StdTx\",\"value\":{\"msg\":[{\"type\":\"htdf/MsgWithdrawDelegationReward\",\"value\":{\"delegator_address\":\"htdf10fjsnx05ewesqjlmy5pesxzwa2t7z4e6vvqxvj\",\"validator_address\":\"htdfvaloper10fjsnx05ewesqjlmy5pesxzwa2t7z4e6x4clme\"}},{\"type\":\"htdf/MsgWithdrawValidatorCommission\",\"value\":{\"validator_address\":\"htdfvaloper10fjsnx05ewesqjlmy5pesxzwa2t7z4e6x4clme\"}}],\"fee\":{\"gas_wanted\":\"200000\",\"gas_price\":\"100\"},\"signatures\":[{\"pub_key\":{\"type\":\"tendermint/PubKeySecp256k1\",\"value\":\"AuOcHxMN+Q31L1Cme1MxkEVZlSx5aaHcT3p8j0BE9pQu\"},\"signature\":\"LiS6g4WVa3h3/TnV9GrasPUF/H59M2NVTKznHo8rB8NqIkwrkDrzhKTPwXVQ85rweLXKUSfv3u4ww1fmhMIB9w==\"}],\"memo\":\"\"}}"
            return json
        }

        fun sign(signStr: String, key: ECKey): String {
            val sha256Hash = Sha256Hash.of(signStr.replace("\\\\", "").toByteArray(StandardCharsets.UTF_8))
            val sig = key.sign(sha256Hash)
            var r = Utils.bigIntegerToBytes(sig.r, 32)
            var s = Utils.bigIntegerToBytes(sig.s, 32)
            val concatenatedBytes = ByteArray(r.size + s.size)
            System.arraycopy(r, 0, concatenatedBytes, 0, r.size)
            System.arraycopy(s, 0, concatenatedBytes, r.size, s.size)
            var signStr = Base64.encodeToString(concatenatedBytes, Base64.DEFAULT)
            return signStr.replace("\n", "")

        }
    }
}