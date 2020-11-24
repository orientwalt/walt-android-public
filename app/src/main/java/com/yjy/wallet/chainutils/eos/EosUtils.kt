package com.yjy.wallet.chainutils.eos

//import com.memtrip.eos.abi.writer.bytewriter.DefaultByteWriter
//import com.memtrip.eos.abi.writer.compression.CompressionType
//import com.memtrip.eos.chain.actions.transaction.AbiBinaryGenTransactionWriter
//import com.memtrip.eos.chain.actions.transaction.TransactionContext
//import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
//import com.memtrip.eos.chain.actions.transaction.abi.SignedTransactionAbi
//import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
//import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
//import com.memtrip.eos.chain.actions.transaction.account.BuyRamChain
//import com.memtrip.eos.chain.actions.transaction.account.DelegateBandwidthChain
//import com.memtrip.eos.chain.actions.transaction.account.SellRamChain
//import com.memtrip.eos.chain.actions.transaction.account.UnDelegateBandwidthChain
//import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamArgs
//import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamBody
//import com.memtrip.eos.chain.actions.transaction.account.actions.delegatebw.DelegateBandwidthArgs
//import com.memtrip.eos.chain.actions.transaction.account.actions.delegatebw.DelegateBandwidthBody
//import com.memtrip.eos.chain.actions.transaction.account.actions.sellram.SellRamArgs
//import com.memtrip.eos.chain.actions.transaction.account.actions.sellram.SellRamBody
//import com.memtrip.eos.chain.actions.transaction.account.actions.undelegatebw.UnDelegateBandwidthArgs
//import com.memtrip.eos.chain.actions.transaction.account.actions.undelegatebw.UnDelegateBandwidthBody
//import com.memtrip.eos.chain.actions.transaction.transfer.TransferChain
//import com.memtrip.eos.chain.actions.transaction.transfer.actions.TransferArgs
//import com.memtrip.eos.chain.actions.transaction.transfer.actions.TransferBody
//import com.memtrip.eos.core.block.BlockIdDetails
//import com.memtrip.eos.core.crypto.EosPrivateKey
//import com.memtrip.eos.core.crypto.signature.PrivateKeySigning
//import com.memtrip.eos.core.hex.DefaultHexWriter
//import com.memtrip.eos.http.rpc.model.signing.PushTransaction

/**
 * weiweiyu
 * 2020/1/17
 * 575256725@qq.com
 * 13115284785
 */

class EosUtils {

//    companion object {
//        @Throws(Exception::class)
//        fun transfer(pk: String, contractAccount: String, from: String, to: String,
//                     quantity: String, memo: String, time: Long, chainId: String, blockTime: String,
//                     lastIrreversibleBlockNum: String): String? {
//            val signatureProviderPrivateKey = EosPrivateKey(pk)
//            val args = TransferChain.Args(from, to, quantity, memo)
//            val d = Date(TimeUtils.strToTimeEos(blockTime) + time * 1000)
//            val transactionContext = TransactionContext(from, signatureProviderPrivateKey, d)
//            val data = AbiBinaryGenTransactionWriter(CompressionType.NONE)
//                    .squishTransferBody(TransferBody(TransferArgs(
//                            args.fromAccount,
//                            args.toAccount,
//                            args.quantity,
//                            args.memo))).toHex()
//            val actions = listOf(ActionAbi(contractAccount, "transfer",
//                    listOf(TransactionAuthorizationAbi(
//                            transactionContext.authorizingAccountName,
//                            "active")), data))
//            val transaction = transaction(d, BlockIdDetails(lastIrreversibleBlockNum), actions)
//            val signature = PrivateKeySigning().sign(
//                    AbiBinaryGenTransactionWriter(DefaultByteWriter(1048), DefaultHexWriter(), CompressionType.NONE).squishSignedTransactionAbi(
//                            SignedTransactionAbi(chainId, transaction, emptyList())
//                    ).toBytes(), signatureProviderPrivateKey)
//            val push = PushTransaction(listOf(signature), "none", "", AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(transaction).toHex())
//            return Gson().toJson(push)
//        }
//
//        //抵押
//        @Throws(Exception::class)
//        fun delegatebw(pk: String, from: String, to: String,
//                       netQuantity: String, cpuQuantity: String, time: Long, chainId: String, blockTime: String,
//                       lastIrreversibleBlockNum: String): String? {
//            val signatureProviderPrivateKey = EosPrivateKey(pk)
//            val args = DelegateBandwidthChain.Args(from, to, netQuantity, cpuQuantity, false)
//            val d = Date(TimeUtils.strToTimeEos(blockTime) + time * 1000)
//            val transactionContext = TransactionContext(from, signatureProviderPrivateKey, d)
//            val data = AbiBinaryGenTransactionWriter(CompressionType.NONE).squishDelegateBandwidthBody(
//                    DelegateBandwidthBody(
//                            DelegateBandwidthArgs(
//                                    args.from,
//                                    args.receiver,
//                                    args.netQuantity,
//                                    args.cpuQuantity,
//                                    if (args.transfer) 1 else 0))
//            ).toHex()
//            val actions = listOf(ActionAbi("eosio", "delegatebw",
//                    listOf(TransactionAuthorizationAbi(
//                            transactionContext.authorizingAccountName,
//                            "active")), data))
//            val transaction = transaction(d, BlockIdDetails(lastIrreversibleBlockNum), actions)
//            val signature = PrivateKeySigning().sign(
//                    AbiBinaryGenTransactionWriter(DefaultByteWriter(1048), DefaultHexWriter(), CompressionType.NONE).squishSignedTransactionAbi(
//                            SignedTransactionAbi(chainId, transaction, emptyList())
//                    ).toBytes(), signatureProviderPrivateKey)
//            val push = PushTransaction(listOf(signature), "none", "", AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(transaction).toHex())
//            return Gson().toJson(push)
//        }
//
//
//        //赎回
//        @Throws(Exception::class)
//        fun undelegatebw(pk: String, from: String, to: String,
//                         netQuantity: String, cpuQuantity: String, time: Long, chainId: String, blockTime: String,
//                         lastIrreversibleBlockNum: String): String? {
//            val signatureProviderPrivateKey = EosPrivateKey(pk)
//            val args = UnDelegateBandwidthChain.Args(from, to, netQuantity, cpuQuantity)
//            val d = Date(TimeUtils.strToTimeEos(blockTime) + time * 1000)
//            val transactionContext = TransactionContext(from, signatureProviderPrivateKey, d)
//            val data = AbiBinaryGenTransactionWriter(CompressionType.NONE).squishUnDelegateBandwidthBody(
//                    UnDelegateBandwidthBody(
//                            UnDelegateBandwidthArgs(
//                                    args.from,
//                                    args.receiver,
//                                    args.netQuantity,
//                                    args.cpuQuantity))
//            ).toHex()
//            val actions = listOf(ActionAbi("eosio", "undelegatebw",
//                    listOf(TransactionAuthorizationAbi(
//                            transactionContext.authorizingAccountName,
//                            "active")), data))
//            val transaction = transaction(d, BlockIdDetails(lastIrreversibleBlockNum), actions)
//            val signature = PrivateKeySigning().sign(
//                    AbiBinaryGenTransactionWriter(DefaultByteWriter(1048), DefaultHexWriter(), CompressionType.NONE).squishSignedTransactionAbi(
//                            SignedTransactionAbi(chainId, transaction, emptyList())
//                    ).toBytes(), signatureProviderPrivateKey)
//            val push = PushTransaction(listOf(signature), "none", "", AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(transaction).toHex())
//            return Gson().toJson(push)
//        }
//
//        //赎回
//        @Throws(Exception::class)
//        fun buyram(pk: String, receiver: String,
//                   cpuQuantity: String, time: Long, chainId: String, blockTime: String,
//                   lastIrreversibleBlockNum: String): String? {
//            val signatureProviderPrivateKey = EosPrivateKey(pk)
//            val args = BuyRamChain.Args(receiver, cpuQuantity)
//            val d = Date(TimeUtils.strToTimeEos(blockTime) + time * 1000)
//            val transactionContext = TransactionContext(receiver, signatureProviderPrivateKey, d)
//            val data = AbiBinaryGenTransactionWriter(CompressionType.NONE).squishBuyRamBody(
//                    BuyRamBody(
//                            BuyRamArgs(
//                                    transactionContext.authorizingAccountName,
//                                    args.receiver,
//                                    args.quantity)
//                    )
//            ).toHex()
//            val actions = listOf(ActionAbi("eosio", "buyram",
//                    listOf(TransactionAuthorizationAbi(
//                            transactionContext.authorizingAccountName,
//                            "active")), data))
//            val transaction = transaction(d, BlockIdDetails(lastIrreversibleBlockNum), actions)
//            val signature = PrivateKeySigning().sign(
//                    AbiBinaryGenTransactionWriter(DefaultByteWriter(1048), DefaultHexWriter(), CompressionType.NONE).squishSignedTransactionAbi(
//                            SignedTransactionAbi(chainId, transaction, emptyList())
//                    ).toBytes(), signatureProviderPrivateKey)
//            val push = PushTransaction(listOf(signature), "none", "", AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(transaction).toHex())
//            return Gson().toJson(push)
//        }
//
//        //赎回
//        @Throws(Exception::class)
//        fun sellram(pk: String, receiver: String,
//                    ram: Long, time: Long, chainId: String, blockTime: String,
//                    lastIrreversibleBlockNum: String): String? {
//            val signatureProviderPrivateKey = EosPrivateKey(pk)
//            val args = SellRamChain.Args(ram)
//            val d = Date(TimeUtils.strToTimeEos(blockTime) + time * 1000)
//            val transactionContext = TransactionContext(receiver, signatureProviderPrivateKey, d)
//            val data = AbiBinaryGenTransactionWriter(CompressionType.NONE).squishSellRamBody(
//                    SellRamBody(
//                            SellRamArgs(
//                                    transactionContext.authorizingAccountName,
//                                    args.quantity)
//                    )
//            ).toHex()
//            val actions = listOf(ActionAbi("eosio", "sellram",
//                    listOf(TransactionAuthorizationAbi(
//                            transactionContext.authorizingAccountName,
//                            "active")), data))
//            val transaction = transaction(d, BlockIdDetails(lastIrreversibleBlockNum), actions)
//            val signature = PrivateKeySigning().sign(
//                    AbiBinaryGenTransactionWriter(DefaultByteWriter(1048), DefaultHexWriter(), CompressionType.NONE).squishSignedTransactionAbi(
//                            SignedTransactionAbi(chainId, transaction, emptyList())
//                    ).toBytes(), signatureProviderPrivateKey)
//            val push = PushTransaction(listOf(signature), "none", "", AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(transaction).toHex())
//            return Gson().toJson(push)
//        }
//
//        private fun transaction(
//                expirationDate: Date,
//                blockIdDetails: BlockIdDetails,
//                actions: List<ActionAbi>
//        ): TransactionAbi {
//            return TransactionAbi(
//                    expirationDate,
//                    blockIdDetails.blockNum,
//                    blockIdDetails.blockPrefix,
//                    0,
//                    0,
//                    0,
//                    emptyList(),
//                    actions,
//                    emptyList(),
//                    emptyList(),
//                    emptyList())
//        }
//    }
}