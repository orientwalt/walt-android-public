package com.yjy.wallet.chainutils.bch;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Message;
import org.bitcoinj.core.MessageSerializer;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.UnsafeByteArrayOutputStream;
import org.bitcoinj.core.VarInt;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptError;
import org.bitcoinj.script.ScriptException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkState;
import static org.bitcoinj.core.Utils.uint32ToByteStreamLE;
import static org.bitcoinj.core.Utils.uint64ToByteStreamLE;

/**
 * weiweiyu
 * 2019/8/10
 * 575256725@qq.com
 * 13115284785
 */
public class BCHTransaction extends Transaction {

    public BCHTransaction(NetworkParameters params) {
        super(params);
    }

    public BCHTransaction(NetworkParameters params, byte[] payloadBytes) throws ProtocolException {
        super(params, payloadBytes);
    }

    public BCHTransaction(NetworkParameters params, byte[] payload, int offset) throws ProtocolException {
        super(params, payload, offset);
    }

    public BCHTransaction(NetworkParameters params, byte[] payload, int offset, @Nullable Message parent, MessageSerializer setSerializer, int length, @Nullable byte[] hashFromHeader) throws ProtocolException {
        super(params, payload, offset, parent, setSerializer, length, hashFromHeader);
    }

    public BCHTransaction(NetworkParameters params, byte[] payload, @Nullable Message parent, MessageSerializer setSerializer, int length) throws ProtocolException {
        super(params, payload, parent, setSerializer, length);
    }

    public TransactionInput addSignedInput(TransactionOutPoint prevOut, Coin amount, Script scriptPubKey, ECKey sigKey,
                                           SigHash sigHash, boolean anyoneCanPay, boolean forkId) throws ScriptException {
        // Verify the API user didn't try to do operations out of order.
        checkState(!getOutputs().isEmpty(), "Attempting to sign tx without outputs.");
        TransactionInput input = new TransactionInput(params, this, new byte[]{}, prevOut);
        addInput(input);
        Sha256Hash hash = forkId ?
                hashForSignatureWitness(getInputs().size() - 1, scriptPubKey, amount, sigHash, anyoneCanPay) :
                hashForSignature(getInputs().size() - 1, scriptPubKey, sigHash, anyoneCanPay);

        ECKey.ECDSASignature ecSig = sigKey.sign(hash);
        TransactionSignature txSig = new TransactionSignature(ecSig, sigHash, anyoneCanPay, forkId);
        if (scriptPubKey.isSentToRawPubKey()) {
            byte[] sigBytes = txSig.encodeToBitcoin();
            input.setScriptSig(new ScriptBuilder().data(sigBytes).build());
        } else if (scriptPubKey.isSentToAddress()) {
            byte[] pubkeyBytes = sigKey.getPubKey();
            byte[] sigBytes = txSig.encodeToBitcoin();
            input.setScriptSig( new ScriptBuilder().data(sigBytes).data(pubkeyBytes).build());
        }else
            throw new ScriptException(ScriptError.SCRIPT_ERR_UNKNOWN_ERROR, "Don't know how to sign for this kind of scriptPubKey: " + scriptPubKey);
        return input;
    }
    public synchronized Sha256Hash hashForSignatureWitness(
            int inputIndex,
            Script scriptCode,
            Coin prevValue,
            SigHash type,
            boolean anyoneCanPay)
    {
        byte[] connectedScript = scriptCode.getProgram();
        return hashForSignatureWitness(inputIndex, connectedScript, prevValue, type, anyoneCanPay);
    }
    public synchronized Sha256Hash hashForSignatureWitness(
            int inputIndex,
            byte[] connectedScript,
            Coin prevValue,
            SigHash type,
            boolean anyoneCanPay)
    {
        byte sigHashType = (byte) TransactionSignature.calcSigHashValue(type, anyoneCanPay, true);
        ByteArrayOutputStream bos = new UnsafeByteArrayOutputStream(length == UNKNOWN_LENGTH ? 256 : length + 4);
        try {
            byte[] hashPrevouts = new byte[32];
            byte[] hashSequence = new byte[32];
            byte[] hashOutputs = new byte[32];
            anyoneCanPay = (sigHashType & SIGHASH_ANYONECANPAY_VALUE) == SIGHASH_ANYONECANPAY_VALUE;

            if (!anyoneCanPay) {
                ByteArrayOutputStream bosHashPrevouts = new UnsafeByteArrayOutputStream(256);
                for (int i = 0; i < this.getInputs().size(); ++i) {
                    bosHashPrevouts.write(this.getInputs().get(i).getOutpoint().getHash().getReversedBytes());
                    uint32ToByteStreamLE(this.getInputs().get(i).getOutpoint().getIndex(), bosHashPrevouts);
                }
                hashPrevouts = Sha256Hash.hashTwice(bosHashPrevouts.toByteArray());
            }

            if (!anyoneCanPay && type != SigHash.SINGLE && type != SigHash.NONE) {
                ByteArrayOutputStream bosSequence = new UnsafeByteArrayOutputStream(256);
                for (int i = 0; i < this.getInputs().size(); ++i) {
                    uint32ToByteStreamLE(this.getInputs().get(i).getSequenceNumber(), bosSequence);
                }
                hashSequence = Sha256Hash.hashTwice(bosSequence.toByteArray());
            }

            if (type != SigHash.SINGLE && type != SigHash.NONE) {
                ByteArrayOutputStream bosHashOutputs = new UnsafeByteArrayOutputStream(256);
                for (int i = 0; i < this.getOutputs().size(); ++i) {
                    uint64ToByteStreamLE(
                            BigInteger.valueOf(this.getOutputs().get(i).getValue().getValue()),
                            bosHashOutputs
                    );
                    bosHashOutputs.write(new VarInt(this.getOutputs().get(i).getScriptBytes().length).encode());
                    bosHashOutputs.write(this.getOutputs().get(i).getScriptBytes());
                }
                hashOutputs = Sha256Hash.hashTwice(bosHashOutputs.toByteArray());
            } else if (type == SigHash.SINGLE && inputIndex < getOutputs().size()) {
                ByteArrayOutputStream bosHashOutputs = new UnsafeByteArrayOutputStream(256);
                uint64ToByteStreamLE(
                        BigInteger.valueOf(this.getOutputs().get(inputIndex).getValue().getValue()),
                        bosHashOutputs
                );
                bosHashOutputs.write(new VarInt(this.getOutputs().get(inputIndex).getScriptBytes().length).encode());
                bosHashOutputs.write(this.getOutputs().get(inputIndex).getScriptBytes());
                hashOutputs = Sha256Hash.hashTwice(bosHashOutputs.toByteArray());
            }
            uint32ToByteStreamLE(getVersion(), bos);
            bos.write(hashPrevouts);
            bos.write(hashSequence);
            bos.write(getInputs().get(inputIndex).getOutpoint().getHash().getReversedBytes());
            uint32ToByteStreamLE(getInputs().get(inputIndex).getOutpoint().getIndex(), bos);
            bos.write(new VarInt(connectedScript.length).encode());
            bos.write(connectedScript);
            uint64ToByteStreamLE(BigInteger.valueOf(prevValue.getValue()), bos);
            uint32ToByteStreamLE(getInputs().get(inputIndex).getSequenceNumber(), bos);
            bos.write(hashOutputs);
            uint32ToByteStreamLE(getLockTime(), bos);
            uint32ToByteStreamLE(0x000000ff & sigHashType, bos);
        } catch (IOException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }

        return Sha256Hash.twiceOf(bos.toByteArray());
    }

}
