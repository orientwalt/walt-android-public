package com.weiyu.baselib.util

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import android.util.Base64

import java.io.UnsupportedEncodingException
import java.security.Key
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.Provider
import java.security.SecureRandom

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

import android.os.Build.VERSION_CODES.M
import com.weiyu.baselib.R
import com.weiyu.baselib.net.exception.BackErrorException
import com.weiyu.baselib.tinker.util.BaseApplicationContext

/**
 * Created by weiweiyu
 * on 2019/5/15
 */
object AesUtils {
    /**
     * 算法Key
     */
    private val KEY_ALGORITHM = "AES"
    /**
     * 加密算法
     */
    private val CIPHER_ALGORITHM = "AES"


    /**
     * 加密数据
     *
     * @param data 待加密内容
     * @param key  加密的密钥
     * @return 加密后的数据
     */
    @Throws(BackErrorException::class)
    fun encrypt(data: String, key: String): String {
        return try {
            // 获得密钥
            val desKey = keyGenerator(key)
            // 实例化一个密码对象
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            // 密码初始化
            cipher.init(Cipher.ENCRYPT_MODE, desKey)
            // 执行加密
            val bytes = cipher.doFinal(data.toByteArray(charset("UTF-8")))
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            ""
        }

    }

    /**
     * 解密数据
     *
     * @param data 待解密的内容
     * @param key  解密的密钥
     * @return 解密后的字符串
     */
    @Throws(BackErrorException::class)
    fun decrypt(data: String?, key: String): String {
        return try {
            // 生成密钥
            val kGen = keyGenerator(key)
            // 实例化密码对象
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            // 初始化密码对象
            cipher.init(Cipher.DECRYPT_MODE, kGen)
            // 执行解密
            val bytes = cipher.doFinal(Base64.decode(data, Base64.DEFAULT))
            String(bytes)
        } catch (e: Exception) {
            throw BackErrorException(BaseApplicationContext.application.resources.getString(R.string.send_err_pwd2))
        }

    }

    /**
     * 获取密钥
     *
     * @param key 密钥字符串
     * @return 返回一个密钥
     */
    @SuppressLint("DeletedProvider")
    private fun keyGenerator(key: String): Key {
        return SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(key.toByteArray(), 32), KEY_ALGORITHM)
    }
}

