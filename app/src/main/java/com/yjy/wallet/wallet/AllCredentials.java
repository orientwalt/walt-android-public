package com.yjy.wallet.wallet;

import org.bitcoinj.core.Bech32;
import org.bitcoinj.core.ECKey;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

public class AllCredentials {

    private final ECKeyPair ecKeyPair;
    private final String address;

    private AllCredentials(ECKeyPair ecKeyPair, String address) {
        this.ecKeyPair = ecKeyPair;
        this.address = address;
    }

    public ECKeyPair getEcKeyPair() {
        return ecKeyPair;
    }

    public String getAddress() {
        return address;
    }

    public static AllCredentials create(ECKeyPair ecKeyPair) {
        ECKey ecKey = ECKey.fromPrivate(ecKeyPair.getPrivateKey());
        byte[] payloadBytes2 = BitcoinCashBitArrayConverter.convertBits(ecKey.getPubKeyHash(), 8, 5, true);
        String address = Bech32.encode("usdp", payloadBytes2);
        return new AllCredentials(ecKeyPair, address);
    }

    public static AllCredentials create(String privateKey, String publicKey) {
        return create(new ECKeyPair(Numeric.toBigInt(privateKey), Numeric.toBigInt(publicKey)));
    }

    public static AllCredentials create(String privateKey) {
        return create(ECKeyPair.create(Numeric.toBigInt(privateKey)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AllCredentials that = (AllCredentials) o;

        if (ecKeyPair != null ? !ecKeyPair.equals(that.ecKeyPair) : that.ecKeyPair != null) {
            return false;
        }

        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode() {
        int result = ecKeyPair != null ? ecKeyPair.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
