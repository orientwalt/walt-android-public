package com.yjy.wallet.chainutils.neo

import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.tinker.util.BaseApplicationContext
import com.yjy.wallet.R
import com.yjy.wallet.bean.neo.BalanceItem
import io.neow3j.constants.NeoConstants
import io.neow3j.contract.ScriptBuilder
import io.neow3j.contract.ScriptHash
import io.neow3j.crypto.Base58
import io.neow3j.crypto.exceptions.AddressFormatException
import io.neow3j.crypto.transaction.RawInvocationScript
import io.neow3j.crypto.transaction.RawTransactionInput
import io.neow3j.crypto.transaction.RawVerificationScript
import io.neow3j.utils.ArrayUtils
import io.neow3j.utils.Keys
import io.neow3j.utils.Numeric
import org.web3j.crypto.Hash
import java.math.BigInteger
import java.util.*


/**
 * weiweiyu
 * 2019/10/11
 * 575256725@qq.com
 * 13115284785
 */
class NEOSign {
    companion object {
        fun getAddress(pKey: BigInteger): String {
            return getAddress(Numeric.toHexStringNoPrefix(pKey))
        }

        fun getAddress(publicKeyWithNoPrefix: String): String {
            return getAddress(Numeric.hexStringToByteArray(publicKeyWithNoPrefix))
        }

        fun getAddress(publicKey: ByteArray): String {
            var byte = Keys.getVerificationScriptFromPublicKey(publicKey)
            return toAddress(ScriptHash(Hash.sha256hash160(byte)))
        }

        fun toAddress(scriptHash: ScriptHash): String {
            val data = ByteArray(1)
            data[0] = NeoConstants.COIN_VERSION
            val dataAndScriptHash = ArrayUtils.concatenate(data, scriptHash.toArray())
            val checksum = Hash.sha256(Hash.sha256(dataAndScriptHash))
            val first4BytesCheckSum = ByteArray(4)
            System.arraycopy(checksum, 0, first4BytesCheckSum, 0, 4)
            val dataToEncode = ArrayUtils.concatenate(dataAndScriptHash, first4BytesCheckSum)
            return Base58.encode(dataToEncode)
        }

        fun isValidAddress(address: String): Boolean {
            val data: ByteArray
            try {
                data = Base58.decode(address)
            } catch (e: AddressFormatException) {
                return false
            }

            if (data.size != 25) return false
            if (data[0] != NeoConstants.COIN_VERSION) return false
            val checksum = Hash.sha256(sha256(data, 0, 21))
            for (i in 0..3) {
                if (data[data.size - 4 + i] != checksum[i]) return false
            }
            return true
        }

        fun fromAddress(address: String): ScriptHash {
            if (!isValidAddress(address)) {
                throw IllegalArgumentException("Not a valid NEO address.")
            }
            val buffer = ByteArray(20)
            System.arraycopy(Base58.decode(address), 1, buffer, 0, 20)
            return ScriptHash(buffer)
        }

        fun sha256(input: ByteArray, offset: Int, length: Int): ByteArray {
            var input = input
            if (offset != 0 || length != input.size) {
                val array = ByteArray(length)
                System.arraycopy(input, offset, array, 0, length)
                input = array
            }
            return Hash.sha256(input)
        }

        fun getPrivateKeyFromWIF(wif: String?): ByteArray {
            if (wif == null) {
                throw NullPointerException()
            }

            val data = Base58.decode(wif)

            if (data.size != 38 || data[0] != 0x80.toByte() || data[33].toInt() != 0x01) {
                throw IllegalArgumentException()
            }

            val checksum = Hash.sha256(sha256(data, 0, data.size - 4))

            for (i in 0..3) {
                if (data[data.size - 4 + i] != checksum[i]) {
                    throw IllegalArgumentException()
                }
            }

            val privateKey = ByteArray(32)
            System.arraycopy(data, 1, privateKey, 0, privateKey.size)
            Arrays.fill(data, 0.toByte())
            return privateKey
        }

        @Throws(Exception::class)
        fun sign(key: ECKeyPair, to: String, value: String, assetId: String, list: MutableList<BalanceItem>): String {
            val address = getAddress(key.publicKey)
            var b = NEOContractTransaction.Builder()
            var inputsAmount = 0.0
            list.forEach {
                var info = it.unspent
                if (info.isNotEmpty()) {
                    inputsAmount += info[0].value
                    var txid = info[0].txid
                    var index = info[0].n
                    b.input(RawTransactionInput(txid, index))
                }
            }
            var changeAmount = inputsAmount - value.toDouble()
            if (changeAmount < 0) {
                throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.send_amout_not_balance))
            }
            b.output(NEOTransactionOutput(assetId, value.toDouble(), to))
            if (changeAmount > 0) {
                b.output(NEOTransactionOutput(assetId, changeAmount, address))
            }
            var signature = Sign.signMessage(b.build().toArrayWithoutScripts(), key)
            val script = ScriptBuilder().pushData(signature.concatenated).toArray()
            b.script2(NEOScript(RawInvocationScript(script), RawVerificationScript.fromPublicKey(key.publicKey)))
            return Numeric.toHexStringNoPrefix(b.build().toArray())
        }
    }
}