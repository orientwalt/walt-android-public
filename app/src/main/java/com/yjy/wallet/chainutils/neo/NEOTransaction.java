package com.yjy.wallet.chainutils.neo;

/**
 * weiweiyu
 * 2019/11/19
 * 575256725@qq.com
 * 13115284785
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.neow3j.crypto.transaction.RawInvocationScript;
import io.neow3j.crypto.transaction.RawTransactionAttribute;
import io.neow3j.crypto.transaction.RawTransactionInput;
import io.neow3j.crypto.transaction.RawVerificationScript;
import io.neow3j.io.BinaryReader;
import io.neow3j.io.BinaryWriter;
import io.neow3j.io.NeoSerializable;
import io.neow3j.model.types.TransactionType;
import io.neow3j.utils.ArrayUtils;
import io.neow3j.utils.Numeric;

/**
 * <p>Transaction class used for signing transactions locally.</p>
 */
@SuppressWarnings("unchecked")
public abstract class NEOTransaction extends NeoSerializable {

    private static final Logger LOG = LoggerFactory.getLogger(NEOTransaction.class);

    private TransactionType transactionType;
    private byte version;
    private List<RawTransactionAttribute> attributes;
    private List<RawTransactionInput> inputs;
    private List<NEOTransactionOutput> outputs;
    private List<NEOScript> scripts;

    public NEOTransaction() {
    }

    protected NEOTransaction(NEOTransaction.Builder builder) {
        this.transactionType = builder.transactionType;
        this.version = builder.version;
        this.attributes = builder.attributes;
        this.inputs = builder.inputs;
        this.outputs = builder.outputs;
        this.scripts = builder.scripts;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public byte getVersion() {
        return version;
    }

    public List<RawTransactionAttribute> getAttributes() {
        return attributes;
    }

    public List<RawTransactionInput> getInputs() {
        return inputs;
    }

    public List<NEOTransactionOutput> getOutputs() {
        return outputs;
    }

    public List<NEOScript> getScripts() {
        return scripts;
    }

    /**
     * Adds the given invocation script (e.g. signatures) and the verification script to this
     * transaction's list of witnesses.
     *
     * @param invocationScript   The invocation script of the witness.
     * @param verificationScript The verification script of the witness.
     */
    public void addScript(RawInvocationScript invocationScript, RawVerificationScript verificationScript) {
        addScript(new NEOScript(invocationScript, verificationScript));
    }

    public void addScript(NEOScript script) {
        if (script.getScriptHash() == null || script.getScriptHash().length() == 0) {
            throw new IllegalArgumentException("The script hash of the given script is " +
                    "empty. Please set the script hash.");
        }
        this.scripts.add(script);
        this.scripts.sort(Comparator.comparing(NEOScript::getScriptHash));
    }

    public String getTxId() {
        byte[] hash = Hash.sha256(Hash.sha256(toArrayWithoutScripts()));
        return Numeric.toHexStringNoPrefix(ArrayUtils.reverseArray(hash));
    }

    public int getSize() {
        // TODO 2019-08-05 claude:
        // Implement more efficiently, e.g. with fixed byte values and calls to the getSize() of
        // the transaction components.
        return toArray().length;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.transactionType = TransactionType.valueOf(reader.readByte());
        this.version = reader.readByte();
        try {
            deserializeExclusive(reader);
            this.attributes = reader.readSerializableList(RawTransactionAttribute.class);
            this.inputs = reader.readSerializableList(RawTransactionInput.class);
            this.outputs = reader.readSerializableList(NEOTransactionOutput.class);
            this.scripts = reader.readSerializableList(NEOScript.class);
        } catch (IllegalAccessException e) {
            LOG.error("Can't access the specified object.", e);
        } catch (InstantiationException e) {
            LOG.error("Can't instantiate the specified object type.", e);
        }
    }

    private void serializeWithoutScripts(BinaryWriter writer) throws IOException {
        writer.writeByte(this.transactionType.byteValue());
        writer.writeByte(this.version);
        serializeExclusive(writer);
        writer.writeSerializableVariable(this.attributes);
        writer.writeSerializableVariable(this.inputs);
        writer.writeSerializableVariable(this.outputs);
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        serializeWithoutScripts(writer);
        writer.writeSerializableVariable(this.scripts);
    }

    public abstract void serializeExclusive(BinaryWriter writer) throws IOException;

    public abstract void deserializeExclusive(BinaryReader reader) throws IOException, IllegalAccessException, InstantiationException;

    /**
     * Serializes this transaction to a raw byte array without any scripts. This is required if the
     * serialized transaction gets signed, e.g. by an external keypair/provider.
     *
     * @return the serialized transaction
     */
    public byte[] toArrayWithoutScripts() {
        try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
            try (BinaryWriter writer = new BinaryWriter(ms)) {
                serializeWithoutScripts(writer);
                writer.flush();
                return ms.toByteArray();
            }
        } catch (IOException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     * Serializes this transaction to a raw byte array including scripts (witnesses/signatures).
     * The byte array can be sent as a transaction with the `sendrawtransaction` RPC method.
     *
     * @return the serialized transaction.
     */
    @Override
    public byte[] toArray() {
        return super.toArray();
    }

    protected static abstract class Builder<T extends NEOTransaction.Builder<T>> {

        private TransactionType transactionType;
        private byte version;
        private List<RawTransactionAttribute> attributes;
        private List<RawTransactionInput> inputs;
        private List<NEOTransactionOutput> outputs;
        private List<NEOScript> scripts;

        protected Builder() {
            this.version = TransactionType.DEFAULT_VERSION;
            this.attributes = new ArrayList<>();
            this.inputs = new ArrayList<>();
            this.outputs = new ArrayList<>();
            this.scripts = new ArrayList<>();
        }

        protected T transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            this.version = transactionType.version();
            return (T) this;
        }

        public T version(byte version) {
            this.version = version;
            return (T) this;
        }

        public T attributes(List<RawTransactionAttribute> attributes) {
            this.attributes.addAll(attributes);
            return (T) this;
        }

        public T attribute(RawTransactionAttribute attribute) {
            return attributes(Arrays.asList(attribute));
        }

        public T inputs(List<RawTransactionInput> inputs) {
            this.inputs.addAll(inputs);
            return (T) this;
        }

        public T input(RawTransactionInput input) {
            return inputs(Arrays.asList(input));
        }

        public T outputs(List<NEOTransactionOutput> outputs) {
            this.outputs.addAll(outputs);
            return (T) this;
        }

        public T output(NEOTransactionOutput output) {
            return outputs(Arrays.asList(output));
        }

        public T scripts(List<NEOScript> scripts) {
            for (NEOScript script : scripts) {
                if (script.getScriptHash() == null || script.getScriptHash().length() == 0) {
                    throw new IllegalArgumentException("The script hash of the given script is " +
                            "empty. Please set the script hash.");
                }
            }

            this.scripts.addAll(scripts);
            this.scripts.sort(Comparator.comparing(NEOScript::getScriptHash));
            return (T) this;
        }

        public T script(NEOScript script) {
            return scripts(Collections.singletonList(script));
        }
        public T script2(NEOScript script) {
            this.scripts.add(script);
            return (T) this;
        }
        public abstract NEOTransaction build();
    }
}
