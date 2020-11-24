package com.weiyu.baselib.util

import android.text.TextUtils
import java.util.regex.Pattern
import java.util.regex.Pattern.matches


class StringUtils {
    companion object {
        val REGEX_EMAIL = "^[a-zA-Z0-9][-\\w.]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$"
        /**
         * 校验邮箱
         *
         * @param email
         * @return 校验通过返回true，否则返回false
         */
        fun isEmail(email: String?): Boolean {
            if (TextUtils.isEmpty(email)) {
                return false
            }
            return matches(REGEX_EMAIL, email)
        }

        /**
         * 更严格的判断
         *
         * @param
         * @return
         */
        fun isMobileNum(telNum: String?): Boolean {
            if (TextUtils.isEmpty(telNum)) {
                return false
            }
            val p = Pattern.compile("^[1][3456789][0-9]{9}$")
            val m = p.matcher(telNum)
            return m.matches()
        }

        fun setIdCard(s: String): String {
            val stringBuffer = StringBuffer()
            if (!TextUtils.isEmpty(s) && s.length > 10) {
                stringBuffer.append(s, 0, 5)
                stringBuffer.append("****")
                stringBuffer.append(s, s.length - 2, s.length)
            }
            return stringBuffer.toString()
        }

        fun isPwd(str: String): Boolean {
            val regex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![-*&amp;#@!?~%,/;\$_.]+$)[-*&amp;#@!?~%,/;\$_.0-9A-Za-z]{8,16}$"
//            val regex = "^(?![0-9]+$)(?![a-zA-Z]+$){8,16}$"
            return str.matches(regex.toRegex())
        }
        fun isPwd2(str: String): Boolean {
            val regex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![-*&amp;#@!?~%,/;\$_.]+$)[-*&amp;#@!?~%,/;\$_.0-9A-Za-z]{8,20}$"
//            val regex = "^(?![0-9]+$)(?![a-zA-Z]+$){8,16}$"
            return str.matches(regex.toRegex())
        }
        /**
         * 判断密码强度
         *
         * @return Z = 字母 S = 数字 T = 特殊字符
         */
        fun passwordStrong(passwordStr: String): Int {
            var GRADE_SCORE = 0
            val regexZ = "\\d*"
            val regexS = "[a-zA-Z]+"
            val regexT = "\\W+$"
            val regexZT = "\\D*"
            val regexST = "[\\d\\W]*"
            val regexZS = "\\w*"
            val regexZST = "[\\w\\W]*"

            if (passwordStr.matches(regexZ.toRegex())) {
                return 20
            }
            if (passwordStr.matches(regexS.toRegex())) {
                return 20
            }
            if (passwordStr.matches(regexT.toRegex())) {
                return 20
            }
            if (passwordStr.matches(regexZT.toRegex())) {
                return 60
            }
            if (passwordStr.matches(regexST.toRegex())) {
                return 60
            }
            if (passwordStr.matches(regexZS.toRegex())) {
                return 60
            }
            if (passwordStr.matches(regexZST.toRegex())) {
                return 90
            }
            return GRADE_SCORE
        }

        fun printHexString(b: ByteArray): String {
            var s = StringBuffer()
            for (i in b.indices) {
                var hex = Integer.toHexString((b[i].toInt() and 0xFF))
                if (hex.length == 1) {
                    hex = "0$hex"
                }
                s.append(hex)
            }
            return s.toString()
        }

        // 判断一个字符是否是中文
        fun isChinese(c: Char): Boolean {
            return c.toInt() in 0x4E00..0x9FA5// 根据字节码判断
        }

        // 判断一个字符串是否含有中文
        fun isChinese(str: String?): Boolean {
            if (str == null)
                return false
            for (c in str.toCharArray()) {
                if (isChinese(c))
                    return true// 有一个中文字符就返回
            }
            return false
        }

        fun countStr(str: String, str2: String): Int {
            var str1 = str
            var counter = 0
            if (str1.indexOf(str2) == -1) {
                return 0
            }
            while (str1.indexOf(str2) != -1) {
                counter++
                str1 = str1.substring(str1.indexOf(str2) + str2.length)
            }
            return counter
        }
    }

}