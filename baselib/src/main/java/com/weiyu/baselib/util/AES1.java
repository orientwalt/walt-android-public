package com.weiyu.baselib.util;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES1 {
    /* 加密使用的 key */
    public static final String AES_KEY = "YJYVPOLDKZLDUFOK";
    /* 加密使用的 IV */
    private static final String AES_IV = "ASDZDSZ45AWQAZSD";

    /**
     * AES 加密
     *
     * @param content 待解密内容
     * @return 解密的数据
     */
    public static String encryptAES(String content) {
        try {
            byte[] bytes = AES_KEY.getBytes("UTF-8");
            SecretKeySpec secretKeySpec = new SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(bytes, 32), "AES");
            // AES 是加密方式, CBC 是工作模式, PKCS5Padding 是填充模式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // IV 是初始向量，可以增强密码的强度
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(AES_IV.getBytes("UTF-8")));
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
            SecretKeySpec secretKeySpec = new SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(bytes, 32), "AES");
            // AES 是加密方式, CBC 是工作模式, PKCS5Padding 是填充模式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // IV 是初始向量，可以增强密码的强度
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(AES_IV.getBytes("UTF-8")));
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
            SecretKeySpec secretKeySpec = new SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(bytes, 32), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(AES_IV.getBytes("UTF-8")));
            return new String(cipher.doFinal(s.getBytes()), "UTF-8");
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
    public static String decryptAES(String content, String key) {
        try {
            byte[] bytes = key.getBytes("UTF-8");
            String s = new String(Base64.decode(content, Base64.DEFAULT));
            SecretKeySpec secretKeySpec = new SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(bytes, 32), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(AES_IV.getBytes("UTF-8")));
            return new String(cipher.doFinal(s.getBytes()), "UTF-8");
        } catch (Exception e) {

        }
        return null;
    }
}
