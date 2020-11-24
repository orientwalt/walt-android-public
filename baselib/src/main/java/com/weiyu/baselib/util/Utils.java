package com.weiyu.baselib.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 *
 */

public class Utils {

    private static int DEF_DIV_SCALE = 2;
    private static String sResult;

    /**
     * 获取手机号码的星号形式
     *
     * @param pNumber
     * @return
     */
    public static String formatPhoneNO(String pNumber) {
        if (!TextUtils.isEmpty(pNumber) && pNumber.length() > 6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pNumber.length(); i++) {
                char c = pNumber.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return null;
        }

    }

    public static String formatCardNO(String pNumber) {
        if (!TextUtils.isEmpty(pNumber) && pNumber.length() > 6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pNumber.length(); i++) {
                char c = pNumber.charAt(i);
                if (i >= 8 && i <= 12) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return null;
        }

    }

    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    // TODO: 2016/11/26 总感觉怪怪的，有问题，需要好好看看，尤其是一些异常需要及时捕获
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return true;
        }
        return false;
    }

    /**
     * 获取 yyyy-MM-dd 格式的日期
     *
     * @param date
     * @return
     */
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 将double精确到小数点后一位
     *
     * @param value
     * @return
     */
    public static String formatDouble1(double value) {
        BigDecimal decimal = new BigDecimal(value);
        decimal = decimal.setScale(1, BigDecimal.ROUND_HALF_UP);
        return "" + decimal;
    }

    /**
     * 将double精确到小数点后两位
     *
     * @param value
     * @return
     */
    public static String formatDouble2(double value) {
        BigDecimal decimal = new BigDecimal(value);
        decimal = decimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return "" + decimal;
    }

    /**
     * 乘机
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static double mul(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    public  static String div(String v1 ,String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2).toPlainString();
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double roundBigDecimal(BigDecimal v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal one = new BigDecimal("1");
        return v.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    //加密规则 太过于复杂 两层加密
//    public static String pwdTwoLayerEncryption(String pwd){
//        if(TextUtils.isEmpty(pwd))return "";
//        String newpwd= pwd;
//        String three=newpwd.substring(0,3);
//        String afterthree=newpwd.substring(3);
//        return SignDataUtil.md5("2"+"294fd721c603dafd748f1"+SignDataUtil.md5("8352"+three+"6972"+afterthree+"7332")+"294fd721c603dafd748f1"+"2");
//    }

    //一层加密
//    public static String pwdOneLayerEncryption(String pwd){
//        if(TextUtils.isEmpty(pwd))return "";
//        String newpwd= pwd;
//        String three=newpwd.substring(0,3);
//        String afterthree=newpwd.substring(3);
//        return SignDataUtil.md5("8352"+three+"6972"+afterthree+"7332");
//    }

    /**
     * 应用程序是否已安装
     *
     * @param context
     * @param packageName 应用程序的包名
     * @return
     */
    public static boolean isInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> mPacks = pm.getInstalledPackages(0);
        for (PackageInfo info : mPacks) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                if (packageName.equals(info.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * String 类型的 加法运算
     *
     * @param v1
     * @param v2
     * @return
     */
    public static String add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Utils.toNormal(v1));
        BigDecimal b2 = new BigDecimal(Utils.toNormal(v2));


        String result = String.valueOf(b1.add(b2).doubleValue());
        return result;
    }

    /**
     * String 类型的 减法运算
     *
     * @param v1
     * @param v2
     * @return
     */
    public static String sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Utils.toNormal(v1));
        BigDecimal b2 = new BigDecimal(Utils.toNormal(v2));

        String v = String.valueOf(b1.subtract(b2).doubleValue());
        return v;
    }

    public static String subStr(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);

        String v = String.valueOf(b1.subtract(b2).doubleValue());
        return v;
    }

    /**
     * String 类型的 减法运算
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double sub2Double(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Utils.toNormal(v1));
        BigDecimal b2 = new BigDecimal(Utils.toNormal(v2));

        String v = String.valueOf(b1.subtract(b2).doubleValue());

        return Double.valueOf(v);
    }

    /**
     * String 类型的 减法运算
     *
     * @param v1
     * @param v2
     * @return
     */
    public static String subBigDecimal(BigDecimal v1, double v2) {
        BigDecimal b2 = new BigDecimal(Utils.toNormal(v2));
        return v1.subtract(b2).toPlainString();
    }

    public static String addBigDecimal(BigDecimal v1, double v2) {
        BigDecimal b2 = new BigDecimal(Utils.toNormal(v2));
        return v1.add(b2).toPlainString();
    }

    public static String subBigDecimal(double v1, double v2, double v3) {
        BigDecimal b1 = new BigDecimal(Utils.toNormal(v1));
        BigDecimal b2 = new BigDecimal(Utils.toNormal(v2));
        BigDecimal b3 = new BigDecimal(Utils.toNormal(v3));
        return b1.subtract(b2).subtract(b3).toString();
    }
    public static String subBigDecimal(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Utils.toNormal(v1));
        BigDecimal b2 = new BigDecimal(Utils.toNormal(v2));
        return b1.subtract(b2).toString();
    }
    /**
     * String类型 的乘法
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }

    public static double divide(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, 10, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * String类型 的乘法
     *
     * @param v1
     * @param v2
     * @param scale 保留位数
     * @return 返回不为科学计位数
     */
    public static String multiplyWithoutE(double v1, double v2, int scale) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);

        NumberFormat df = NumberFormat.getInstance();
        df.setGroupingUsed(false);
        // 设置数的小数部分所允许的最小位数
        df.setMinimumFractionDigits(2);
        // 设置数的小数部分所允许的最大位数
        df.setMaximumFractionDigits(5);
        return df.format(b1.multiply(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    public static String setDoubleWithoutE(double v1, int scale) {
        NumberFormat df = NumberFormat.getInstance();
        df.setGroupingUsed(false);
        // 设置数的小数部分所允许的最小位数
        df.setMinimumFractionDigits(0);
        // 设置数的小数部分所允许的最大位数
        df.setMaximumFractionDigits(scale);
        return df.format(v1);
    }

    public static String floatToPecent(float number) {
        DecimalFormat numberFormat = new DecimalFormat("0.00%");
        String format = numberFormat.format(number);
        return format;
    }

    public static String doubleToPecent(double number) {
        DecimalFormat numberFormat = new DecimalFormat("0.00%");
        String format = numberFormat.format(number);
        return format;
    }

    public static String StringToPecent(String number) {
        DecimalFormat numberFormat = new DecimalFormat("0.00%");
        String format = numberFormat.format(number);
        return format;
    }
    public static String StringToPecent(Double number) {
        DecimalFormat numberFormat = new DecimalFormat("0.00%");
        String format = numberFormat.format(number);
        return format;
    }
    public static String getPercentFormat(double d,int IntegerDigits,int FractionDigits){
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumIntegerDigits(IntegerDigits);//小数点前保留几位
        nf.setMinimumFractionDigits(FractionDigits);// 小数点后保留几位
        String str = nf.format(d);
        return str;
    }

    public static String toNormal(double amount) {
        BigDecimal decimalFormat = new BigDecimal(String.valueOf(amount));
        return decimalFormat.toPlainString();
    }

    public static String toNormal(String amount) {
        BigDecimal decimalFormat = new BigDecimal(amount);
        return decimalFormat.toPlainString();
    }

    public static String to4Decimal(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(4);
        return nf.format(amount);
    }

    public static String to4SubString(double amount) {
        String amountResult = Utils.toNormal(amount);
        if (amountResult.contains(".")) {
            String[] split = amountResult.split("\\.");
            StringBuilder stringBuilder = new StringBuilder(split[0]);
            if (split.length > 1 && split[1].length() > 3) {
                stringBuilder.append(".").append(split[1].substring(0, 4));
                return Utils.toDoubleNormal(Double.parseDouble(stringBuilder.toString()), 4);
            } else {
                return Utils.toDoubleNormal(Double.parseDouble(stringBuilder.toString()), 4);
            }
        } else {
            return Utils.toDoubleNormal(Double.parseDouble(amountResult), 4);
        }
    }

    /**
     * 截取不要保留
     *
     * @param amount
     * @return
     */
    public static String toSubStringNo(double amount, int digister) {
        String amountResult = Utils.toNormal(amount);
        if (amountResult.contains(".")) {
            String[] split = amountResult.split("\\.");
            StringBuilder stringBuilder = new StringBuilder(split[0]);
            if (split.length > 1 && split[1].length() > digister - 1) {
                stringBuilder.append(".").append(split[1].substring(0, digister));
                return Utils.toDoubleNormal(Double.parseDouble(stringBuilder.toString()), digister);
            } else {
                return Utils.toDoubleNormal(amount, digister);
            }
        } else {
            return Utils.toDoubleNormal(Double.parseDouble(amountResult), digister);
        }
    }


    /**
     * 截取，不足补零(每三位一个逗号)
     *
     * @return
     */
    public static String toSubStringDegist(String amountResult, int degist) {
        //2353453---->2353453.0
        if (!amountResult.contains(".")) amountResult = amountResult + ".0";
        if (amountResult.contains(".")) {
            String[] split = amountResult.split("\\.");
            StringBuilder stringBuilder = new StringBuilder();
//            StringBuilder stringBuilder=new StringBuilder(split[0]);
            String strFront = Utils.toDoubleNormal(Double.parseDouble(split[0]), 0);
            if (split.length > 1 && split[1].length() > degist - 1) {
                stringBuilder.append(".").append(split[1].substring(0, degist));
                String strBack = stringBuilder.toString();
                if ("00".equals(strFront)) {
                    return strFront.replaceAll("00", "0") + strBack;
                } else {
                    return strFront + strBack;
                }
            } else if (split.length > 1) {
                stringBuilder.append(".").append(split[1]);
                for (int i = 0; i < (degist) - split[1].length(); i++) {
                    stringBuilder.append("0");
                }
                if ("00".equals(strFront)) {
                    return strFront.replaceAll("00", "0") + stringBuilder.toString();
                }
                return strFront + stringBuilder.toString();
            } else {
                return Utils.toDoubleNormal(Double.parseDouble(amountResult), degist);
            }
        } else {
            return Utils.to2Double(Double.parseDouble(amountResult));
        }
    }


    /**
     * 截取，不足补零(每三位一个逗号)
     *
     * @param amount
     * @return
     */
    public static String toSubStringDegist(double amount, int degist) {
        String amountResult = Utils.toNormal(amount);
        //2353453---->2353453.0
        if (!amountResult.contains(".")) amountResult = amountResult + ".0";
        if (amountResult.contains(".")) {
            String[] split = amountResult.split("\\.");
            StringBuilder stringBuilder = new StringBuilder();
//            StringBuilder stringBuilder=new StringBuilder(split[0]);
            String strFront = Utils.toDoubleNormal(Double.parseDouble(split[0]), 0);
            if (split.length > 1 && split[1].length() > degist - 1) {
                stringBuilder.append(".").append(split[1].substring(0, degist));
                String strBack = stringBuilder.toString();
                if ("00".equals(strFront)) {
                    return strFront.replaceAll("00", "0") + strBack;
                } else {
                    return strFront + strBack;
                }
            } else if (split.length > 1) {
                stringBuilder.append(".").append(split[1]);
                for (int i = 0; i < (degist) - split[1].length(); i++) {
                    stringBuilder.append("0");
                }
                if ("00".equals(strFront)) {
                    return strFront.replaceAll("00", "0") + stringBuilder.toString();
                }
                return strFront + stringBuilder.toString();
            } else {
                return Utils.toDoubleNormal(Double.parseDouble(amountResult), degist);
            }
        } else {
            return Utils.to2Double(Double.parseDouble(amountResult));
        }
    }

    /**
     * 截取，不足补零(每三位一个逗号),当degist为0时则不要小数点
     *
     * @param amount
     * @return
     */
    public static String toNoPointSubStringDegistIfDegistIsZero(double amount, int degist) {
        String amountResult = Utils.toNormal(amount);
        if (degist == 0) {
            return new BigDecimal(amount).setScale(0, BigDecimal.ROUND_DOWN).toString();
        }
        //2353453---->2353453.0
        if (amountResult.contains(".")) {
            String[] split = amountResult.split("\\.");
            StringBuilder stringBuilder = new StringBuilder();
//            StringBuilder stringBuilder=new StringBuilder(split[0]);
            String strFront = Utils.toDoubleNormal(Double.parseDouble(split[0]), 0);
            if (split.length > 1 && split[1].length() > degist - 1) {
                stringBuilder.append(".").append(split[1].substring(0, degist));
                String strBack = stringBuilder.toString();
                if ("00".equals(strFront)) {
                    return strFront.replaceAll("00", "0") + strBack;
                } else {
                    return strFront + strBack;
                }
            } else if (split.length > 1) {
                stringBuilder.append(".").append(split[1]);
                for (int i = 0; i < (degist) - split[1].length(); i++) {
                    stringBuilder.append("0");
                }
                if ("00".equals(strFront)) {
                    return strFront.replaceAll("00", "0") + stringBuilder.toString();
                }
                return strFront + stringBuilder.toString();
            } else {
                return Utils.toDoubleNormal(Double.parseDouble(amountResult), degist);
            }
        } else {
            return Utils.to2Double(Double.parseDouble(amountResult));
        }
    }


    /**
     * 截取，不足补零(每三位一个逗号)，小數點后保留6位，不足6位的 保留原有位數，整數的在後面加.0
     *
     * @param amount
     * @param degist：保留小数位数
     * @param supplement:小数点不足是否补0
     * @return
     */
    public static String toSubStringDegistForChart(double amount, int degist, boolean supplement) {
        String amountResult = Utils.toNormal(amount);
        //2353453---->2353453.0
        if (!amountResult.contains(".")) amountResult = amountResult + ".0";
        if (amountResult.contains(".")) {
            String[] split = amountResult.split("\\.");
            StringBuilder stringBuilder = new StringBuilder();
//            StringBuilder stringBuilder=new StringBuilder(split[0]);
            String strFront = Utils.toDoubleNormal(Double.parseDouble(split[0]), 0);
            if (split.length > 1 && split[1].length() > degist - 1) {
                stringBuilder.append(".").append(split[1].substring(0, degist));
                String strBack = stringBuilder.toString();
                if ("00".equals(strFront)) {
                    return strFront.replaceAll("00", "0") + strBack;
                } else {
                    if (degist == 0) {//修改degist=0的情况
                        return strFront;
                    }
                    return strFront + strBack;
                }
            } else if (split.length > 1) {
                stringBuilder.append(".").append(split[1]);

                //判斷是否存在小數點 如果存在判斷小數點后位數>6只保留6位不足6保留原有位數
                if (supplement) {
                    for (int i = 0; i < (degist) - split[1].length(); i++) {
                        stringBuilder.append("0");
                    }
                }
                if ("00".equals(strFront)) {
                    return strFront.replaceAll("00", "0") + stringBuilder.toString();
                }
                return strFront + stringBuilder.toString();
            } else {
                return Utils.toDoubleNormal(Double.parseDouble(amountResult), degist);
            }
        } else {
            return Utils.to2Double(Double.parseDouble(amountResult));
        }
    }


    /**
     * 截取，不足补零(每三位一个逗号)，小數點后保留6位，不足6位的 保留原有位數，整數的在後面加.0
     *
     * @param amount
     * @param degist：保留小数位数
     * @param supplement:小数点不足是否补0
     * @return
     */
    public static String toSubStringDegistForChartStr(String amount, int degist, boolean supplement) {
        //    String amountResult = Utils.toNormal(amount);

        String amountResult = amount;
        //2353453---->2353453.0
        if (!amountResult.contains(".")) amountResult = amountResult + ".0";
        if (amountResult.contains(".")) {
            String[] split = amountResult.split("\\.");
            StringBuilder stringBuilder = new StringBuilder();
//            StringBuilder stringBuilder=new StringBuilder(split[0]);
            String strFront = Utils.toDoubleNormal(Double.parseDouble(split[0]), 0);
            if (split.length > 1 && split[1].length() > degist - 1) {
                stringBuilder.append(".").append(split[1].substring(0, degist));
                String strBack = stringBuilder.toString();
                if ("00".equals(strFront)) {
                    return strFront.replaceAll("00", "0") + strBack;
                } else {
                    return strFront + strBack;
                }
            } else if (split.length > 1) {
                stringBuilder.append(".").append(split[1]);

                //判斷是否存在小數點 如果存在判斷小數點后位數>6只保留6位不足6保留原有位數
                if (supplement) {
                    for (int i = 0; i < (degist) - split[1].length(); i++) {
                        stringBuilder.append("0");
                    }
                }
                if ("00".equals(strFront)) {
                    return strFront.replaceAll("00", "0") + stringBuilder.toString();
                }
                return strFront + stringBuilder.toString();
            } else {
                return Utils.toDoubleNormal(Double.parseDouble(amountResult), degist);
            }
        } else {
            return BigDecimal.valueOf(Double.valueOf(amountResult)).toPlainString();
        }
    }


    /**
     * 截取，不足补零（去掉逗号）
     *
     * @param amount
     * @return
     */
    public static String toSubStringDegistNo(String amount, int degist) {
        // String amountResult =Utils.toNormal(amount);
        String amountResult = amount;
        if(!amountResult.contains(".")) {
            amountResult = amountResult + ".0";
        }
//        if (amountResult.contains(".")) {
            String[] split = amountResult.split("\\.");
            StringBuilder stringBuilder = new StringBuilder();
//            StringBuilder stringBuilder=new StringBuilder(split[0]);
            String strFront = Utils.toDoubleNormal(Double.parseDouble(split[0]), 0);
            if (split.length > 1 && split[1].length() > degist - 1) {
                stringBuilder.append(".").append(split[1].substring(0, degist));
                String strBack = stringBuilder.toString();
                if ("00".equals(strFront)) {
                    return (strFront.replaceAll("00", "0") + strBack).replaceAll(",", "");
                } else {
                    return (strFront + strBack).replaceAll(",", "");
                }
            } else if (split.length > 1) {
                stringBuilder.append(".").append(split[1]);
                for (int i = 0; i < (degist) - split[1].length(); i++) {
                    stringBuilder.append("0");
                }
                if ("00".equals(strFront)) {
                    return (strFront.replaceAll("00", "0") + stringBuilder.toString()).replaceAll(",", "");
                }
                return (strFront + stringBuilder.toString()).replaceAll(",", "");
            } else {
                return (Utils.toDoubleNormal(Double.parseDouble(amountResult), degist)).replaceAll(",", "");
            }
//        } else {
//            return (Utils.to2Double(Double.parseDouble(amountResult))).replaceAll(",", "");
//        }
    }


    public static String to2Decimal(double amount) {
        BigDecimal decimalFormat = new BigDecimal(amount);
        decimalFormat.setScale(2, BigDecimal.ROUND_DOWN);
        return decimalFormat.toPlainString();
    }

    public static String to2Double(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(2);
        return nf.format(amount);
    }

    public static String toDoubleNormal(double amount, int digits) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(digits);
        return nf.format(amount);
    }


    public static String to8Double(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(8);
        return nf.format(amount);
    }
    public static String to8Double2(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(8);
        nf.setGroupingUsed(false);
        return nf.format(amount);
    }
    public static String getVersionName(Context context) throws Exception {
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    public static int getPrecision(String amount) {
        int index = amount.indexOf("1") - 1;

        return index;
    }

    public static boolean isUsername(final CharSequence input) {
        return isMatch(REGEX_USERNAME, input);
    }


    public static String getVolOrderNum(String num, int degistV) {

//        int e = (int) Math.floor(Math.log10(num));
//
//        if (e >= 8) {
//            return 8;
//        } else if (e >= 4) {
//            return 4;
//        } else {
//            return 1;
//        }
        try {
            if(Double.valueOf(num) < 1000.0) {
                if(num.length() > 5) {
                    return num.substring(0,5);
                }else {
                    return num;
                }
            }else {
                //大于1000只考虑前面的部分
                String value = num;
                if(value.contains(".")) {
                    value = value.split("\\.")[0];
                }
                //截取  前面k的部分
                if(value.substring(0,value.length() -3).length() < 3) {
                    //还需要多截取的部分
                   return value.substring(0,value.length() -3) + "."+ value.substring(value.length() -3,value.length() -3 + 3 - value.substring(0,value.length() -3).length())+"k";
                }else {
                    return value.substring(0,value.length() -3).length() +"k";
                }

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return   Utils.toSubStringDegistNo(num,degistV);
        }
    }

    /**
     * 判断是否匹配正则
     *
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    /**
     * 正则：用户名，取值范围为 字母和数组（4-16）
     */
    public static final String REGEX_USERNAME = "^[A-Za-z0-9]{4,16}$";


}
