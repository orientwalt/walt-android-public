package com.yjy.wallet.chainutils.neo;

import java.io.IOException;

import io.neow3j.io.BinaryReader;
import io.neow3j.io.BinaryWriter;
import io.neow3j.model.types.TransactionType;

/**
 * weiweiyu
 * 2019/11/19
 * 575256725@qq.com
 * 13115284785
 */
public class NEOContractTransaction extends NEOTransaction {
    public NEOContractTransaction() {
    }

    protected NEOContractTransaction(Builder builder) {
        super(builder);
    }

    @Override
    public void serializeExclusive(BinaryWriter writer) throws IOException {

    }

    @Override
    public void deserializeExclusive(BinaryReader reader) throws IOException, IllegalAccessException, InstantiationException {

    }

    public static class Builder extends NEOTransaction.Builder<Builder> {

        public Builder() {
            super();
            transactionType(TransactionType.CONTRACT_TRANSACTION);
        }

        @Override
        public NEOContractTransaction build() {
            return new NEOContractTransaction(this);
        }
    }
}
