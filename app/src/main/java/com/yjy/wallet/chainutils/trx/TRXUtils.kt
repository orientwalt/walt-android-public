package com.yjy.wallet.chainutils.trx

import com.google.protobuf.Any
import com.google.protobuf.ByteString
import org.bitcoinj.core.Base58
import org.tron.common.crypto.ECKey
import org.tron.common.crypto.Hash
import org.tron.common.crypto.Sha256Hash
import org.tron.protos.Contract
import org.tron.protos.Protocol
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.util.*

/**
 * weiweiyu
 * 2019/8/6
 * 575256725@qq.com
 * 13115284785
 */
class TRXUtils {
    companion object {
        fun getAddress(privateKey: BigInteger): String {
            var pubKey = ECKey.publicKeyFromPrivate(privateKey, false)
            val hash = Hash.sha3(Arrays.copyOfRange(pubKey, 1, pubKey.size))
            val address = Arrays.copyOfRange(hash, 12, hash.size)
            return Base58.encodeChecked(0x41, address)
        }

        fun getAddressHax(privateKey: BigInteger): String {
            var pubKey = ECKey.publicKeyFromPrivate(privateKey, false)
            val hash = Hash.sha3(Arrays.copyOfRange(pubKey, 1, pubKey.size))
            val address = Arrays.copyOfRange(hash, 12, hash.size)
            return "41" + Numeric.toHexStringNoPrefix(address)
        }

        fun getByteAddress(privateKey: BigInteger): ByteArray {
            return Numeric.hexStringToByteArray(getAddressHax(privateKey))
        }

        fun getByteAddreefrom58(address58: String): ByteArray {
            val byte = Base58.decode(address58)
            val byte32 = ByteArray(byte.size - 4)
            System.arraycopy(byte, 0, byte32, 0, byte.size - 4)
            return byte32
        }

        /**
         * 离线签名交易
         *
         * @param privateStr
         * 私钥明文
         * @param toAddr
         * 转入地址
         * @param amount
         * 金额
         * @param blockTimestamp
         * 最新区块时间
         * @param blockNumber
         * 最新区块高度
         * @param blockHash
         * 最新区块hash
         * @return
         */
        fun sign(privateStr: BigInteger, toAddr: String, amount: Long, blockTimestamp: Long, blockHeight: Long,
                 blockHash: String): String {
            val ecKey = ECKey.fromPrivate(privateStr)
            val from = getByteAddress(ecKey.privKey)
            val to = getByteAddreefrom58(toAddr)
            var transaction = createTransaction(from, to, amount, blockTimestamp, blockHeight,
                    Numeric.hexStringToByteArray(blockHash))
            val sha256Hash = Sha256Hash.of(transaction?.rawData?.toByteArray())
            val signature = ecKey.sign(sha256Hash.bytes)
            val sig = ByteString.copyFrom(signature.toByteArray())
            val sig1 = Numeric.toHexStringNoPrefix(signature.toByteArray())
            transaction = transaction?.toBuilder()?.addSignature(sig)?.build()
            val txid = Numeric.toHexStringNoPrefix(Sha256Hash.hash(transaction?.rawData?.toByteArray()))
            var refByte = Numeric.toHexStringNoPrefix(transaction?.rawData?.refBlockBytes?.toByteArray())
            var refHash = Numeric.toHexStringNoPrefix(transaction?.rawData?.refBlockHash?.toByteArray())
            var expiration = transaction?.rawData?.expiration
            var timestamp = transaction?.rawData?.timestamp
            val haxFrom = getAddressHax(privateStr)
            val haxTo = Numeric.toHexStringNoPrefix(to)
            return "{\"signature\":[\"$sig1\"],\"txID\":\"$txid\",\"raw_data\":{\"contract\":[{\"parameter\":{\"value\":{\"amount\":$amount,\"owner_address\":\"$haxFrom\",\"to_address\":\"$haxTo\"},\"type_url\":\"type.googleapis.com/protocol.TransferContract\"},\"type\":\"TransferContract\"}],\"ref_block_bytes\":\"$refByte\",\"ref_block_hash\":\"$refHash\",\"expiration\":$expiration,\"timestamp\":$timestamp}}"
        }

        fun createTransaction(from: ByteArray, to: ByteArray, amount: Long, blockTimestamp: Long, blockHeight: Long, blockHash: ByteArray): Protocol.Transaction? {
            val transactionBuilder = Protocol.Transaction.newBuilder()
            val contractBuilder = Protocol.Transaction.Contract.newBuilder()
            val transferContractBuilder = Contract.TransferContract.newBuilder()
            transferContractBuilder.amount = amount
            val bsTo = ByteString.copyFrom(to)
            val bsOwner = ByteString.copyFrom(from)
            transferContractBuilder.toAddress = bsTo
            transferContractBuilder.ownerAddress = bsOwner

            try {
                val any = Any.pack(transferContractBuilder.build())
                contractBuilder.parameter = any
            } catch (var12: Exception) {
                return null
            }

            contractBuilder.type = Protocol.Transaction.Contract.ContractType.TransferContract
            transactionBuilder.rawDataBuilder.addContract(contractBuilder).setTimestamp(System.currentTimeMillis()).expiration = blockTimestamp + 10 * 60 * 60 * 1000
            val transaction = transactionBuilder.build()
            return setReference(transaction, blockHeight, blockHash)
        }

        fun setReference(transaction: Protocol.Transaction, blockHeight: Long, blockHash: ByteArray): Protocol.Transaction {
            val refBlockNum = org.tron.common.utils.ByteArray.fromLong(blockHeight)
            val rawData = transaction.rawData.toBuilder()
                    .setRefBlockHash(ByteString.copyFrom(org.tron.common.utils.ByteArray.subArray(blockHash, 8, 16)))
                    .setRefBlockBytes(ByteString.copyFrom(org.tron.common.utils.ByteArray.subArray(refBlockNum, 6, 8)))
                    .build()
            return transaction.toBuilder().setRawData(rawData).build()
        }
    }
}