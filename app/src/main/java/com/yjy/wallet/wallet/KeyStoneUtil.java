package com.yjy.wallet.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bitcoinj.core.Bech32;
import org.bitcoinj.core.ECKey;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class KeyStoneUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AllCredentials loadCredentials(String password, String source)
            throws IOException, CipherException {
        return loadCredentials(password, new File(source));
    }

    public static AllCredentials loadCredentials(String password, File source)
            throws IOException {
        WalletFile walletFile = objectMapper.readValue(source, WalletFile.class);
        try {
            return AllCredentials.create(Wallet.decrypt(password, walletFile));
        } catch (CipherException c) {
            return null;
        }
    }

    public static WalletFile getWalletFile(String content) {
        try {
            return objectMapper.readValue(content, WalletFile.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WalletFile createWalletFile(String pwd, ECKeyPair ecKeyPair, boolean useFullScrypt) throws CipherException {
        WalletFile walletFile;
        if (useFullScrypt) {
            walletFile = Wallet.createStandard(pwd, ecKeyPair);
        } else {
            walletFile = Wallet.createLight(pwd, ecKeyPair);
        }
        ECKey ecKey = ECKey.fromPrivate(ecKeyPair.getPrivateKey());
        byte[] payloadBytes2 = BitcoinCashBitArrayConverter.convertBits(ecKey.getPubKeyHash(), 8, 5, true);
        String address = Bech32.encode("usdp", payloadBytes2);
        walletFile.setAddress(address);
        return walletFile;
    }

    public static WalletFile createWalletFile(String pwd, ECKeyPair ecKeyPair, boolean useFullScrypt, String address) throws CipherException {
        WalletFile walletFile;
        if (useFullScrypt) {
            walletFile = Wallet.createStandard(pwd, ecKeyPair);
        } else {
            walletFile = Wallet.createLight(pwd, ecKeyPair);
        }
        walletFile.setAddress(address);
        return walletFile;
    }

    public static String generateWalletFile(
            String password, ECKeyPair ecKeyPair, File destinationDirectory, boolean useFullScrypt)
            throws CipherException, IOException {

        WalletFile walletFile;
        if (useFullScrypt) {
            walletFile = Wallet.createStandard(password, ecKeyPair);
        } else {
            walletFile = Wallet.createLight(password, ecKeyPair);
        }
        ECKey ecKey = ECKey.fromPrivate(ecKeyPair.getPrivateKey());
        byte[] payloadBytes2 = BitcoinCashBitArrayConverter.convertBits(ecKey.getPubKeyHash(), 8, 5, true);
        String address = Bech32.encode("usdp", payloadBytes2);
        walletFile.setAddress(address);
        String fileName = getWalletFileName(walletFile);

        File destination = new File(destinationDirectory, fileName);

        objectMapper.writeValue(destination, walletFile);

        return fileName;
    }

    public static String generateWalletFile(WalletFile walletFile, File destinationDirectory) throws IOException {
        String fileName = getWalletFileName(walletFile);
        File destination = new File(destinationDirectory, fileName);
        objectMapper.writeValue(destination, walletFile);
        return fileName;
    }

    public static void generateWalletFile1(WalletFile walletFile, File destinationDirectory) throws IOException {
        objectMapper.writeValue(destinationDirectory, walletFile);
    }

    private static String getWalletFileName(WalletFile walletFile) {
        return timestamp(new Date()) + walletFile.getAddress() + ".json";
    }

    /**
     * Formats a timestamp string from the given date.
     *
     * @param date a date to be formatted
     * @return a timestamp string for the given date
     */
    static String timestamp(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.S'Z--'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }
}
