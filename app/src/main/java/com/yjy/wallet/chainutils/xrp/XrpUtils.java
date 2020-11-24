package com.yjy.wallet.chainutils.xrp;

/**
 * weiweiyu
 * 2019/11/11
 * 575256725@qq.com
 * 13115284785
 */

import com.ripple.config.Config;
import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.types.known.tx.signed.SignedTransaction;
import com.ripple.core.types.known.tx.txns.Payment;
import com.ripple.crypto.ecdsa.K256KeyPair;


public class XrpUtils {
    /**
     * 签名
     *
     * @param value
     * @return tx_blob
     */
    public String sign(String fromAddress, K256KeyPair privateKey, String toAddress, Long value, Long fee, String accountSequence, String ledgerCurrentIndex) {
        Payment payment = new Payment();
        payment.as(AccountID.Account, fromAddress);
        payment.as(AccountID.Destination, toAddress);
        payment.as(UInt32.DestinationTag, "1");
        payment.as(Amount.Amount, value.toString());
        payment.as(UInt32.Sequence, accountSequence);
        payment.as(UInt32.LastLedgerSequence, ledgerCurrentIndex + 4);
        payment.as(Amount.Fee, fee.toString());
        SignedTransaction signed = payment.sign(privateKey);
        if (signed != null) {
            return signed.tx_blob;
        }
        return null;
    }

    public String getMKey(byte[] bytes) {
        String keyStr = Config.getB58IdentiferCodecs().encodeFamilySeed(bytes);
        return keyStr;
    }

}