package com.weiyu.baselib.util;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Des3 {
    /* 加密使用的 key */
    public static final String AES_KEY = "2E63C38FA967B2F14BAA3DAF";
    /* 加密使用的 IV */
    private static final String AES_IV = "HTDF1234";

    /**
     * AES 加密
     *
     * @param content 待解密内容
     * @return 解密的数据
     */
    public static String encryptAES(String content) {
        try {
            byte[] bytes = AES_KEY.getBytes("UTF-8");
            DESedeKeySpec spec = new DESedeKeySpec(bytes);
            // AES 是加密方式, CBC 是工作模式, PKCS5Padding 是填充模式
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            // IV 是初始向量，可以增强密码的强度
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance("DESede").generateSecret(spec), new IvParameterSpec(AES_IV.getBytes("UTF-8")));
            return new String(Base64.encode(cipher.doFinal(content.getBytes("UTF-8")), Base64.DEFAULT));
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * AES 加密
     *
     * @param content 待解密内容
     * @param key     密钥
     * @return 解密的数据
     */
    public static String encryptAES(String content, String key) {
        try {
            byte[] bytes = key.getBytes("UTF-8");
            DESedeKeySpec spec = new DESedeKeySpec(bytes);
            // AES 是加密方式, CBC 是工作模式, PKCS5Padding 是填充模式
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            // IV 是初始向量，可以增强密码的强度
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance("DESede").generateSecret(spec), new IvParameterSpec(AES_IV.getBytes("UTF-8")));
            return new String(Base64.encode(cipher.doFinal(content.getBytes("UTF-8")), Base64.DEFAULT)).replaceAll("\n","");
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * AES 解密
     *
     * @param content 待解密内容
     * @return 解密的数据
     */
    public static String decryptAES(String content) {
        try {
            byte[] bytes = AES_KEY.getBytes("UTF-8");
            String s = new String(Base64.decode(content, Base64.DEFAULT));
            DESedeKeySpec spec = new DESedeKeySpec(bytes);
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, SecretKeyFactory.getInstance("DESede").generateSecret(spec), new IvParameterSpec(AES_IV.getBytes("UTF-8")));
            return new String(cipher.doFinal(s.getBytes()), "UTF-8");
        } catch (Exception e) {
            BLog.StaticParams.d("");
        }
        return null;
    }

    /**
     * AES 解密
     *
     * @param content 待解密内容
     * @return 解密的数据
     */
    public static String decryptAES(String content, String key) {
        try {
            byte[] bytes = key.getBytes("UTF-8");
            String s = new String(Base64.decode(content, Base64.DEFAULT));
            DESedeKeySpec spec = new DESedeKeySpec(bytes);
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, SecretKeyFactory.getInstance("DESede").generateSecret(spec), new IvParameterSpec(AES_IV.getBytes("UTF-8")));
            return new String(cipher.doFinal(s.getBytes()), "UTF-8");
        } catch (Exception e) {

        }
        return null;
    }
}
