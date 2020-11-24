package com.yjy.wallet.chainutils.xlm;

import com.weiyu.baselib.util.BLog;
import com.yjy.wallet.Constant;

import org.bitcoinj.core.ECKey;
import org.stellar.sdk.Account;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Transaction;

/**
 * weiweiyu
 * 2020/6/8
 * 575256725@qq.com
 * 13115284785
 */
public class XlmUtils {
    public static Transaction sign(String amount, ECKey key, String to, Long sequenceNumber, String memo) {

        KeyPair source = KeyPair.fromSecretSeed(key.getPrivKeyBytes()); //from privateKey
        //官方上传私钥做法
        //AccountResponse sourceAccount = server.accounts().account(source);
        //Transaction transaction = new Transaction.Builder(sourceAccount)
        KeyPair destination = KeyPair.fromAccountId(to); //to address
        Account account = new Account(source.getAccountId(), sequenceNumber);
        Network net = Network.PUBLIC;
        if (!Constant.Companion.getMain()) {
            net = Network.TESTNET;
        }
        BLog.StaticParams.d("----------------------"+amount);
        Transaction transaction = new Transaction.Builder(account, net)
                .addOperation(new PaymentOperation.Builder(destination.getAccountId(), new AssetTypeNative(), amount).build())
                .addMemo(Memo.text(memo))
                .setBaseFee(100000)
                .setTimeout(0)
                .build();

        transaction.sign(source);

        //官方广播方法没有返回签名脚本，直接拿transaction对象广播
        //        try {
//            SubmitTransactionResponse response = server.submitTransaction(transaction);
//            System.out.println("Success!");
//            System.out.println(response.getHash());
//        } catch (Exception e) {
//            System.out.println("Something went wrong!");
//            System.out.println(e.getMessage());
//        }

        //我这里根据自己业务拆分了，看各位需要。
        return transaction;
    }
}
