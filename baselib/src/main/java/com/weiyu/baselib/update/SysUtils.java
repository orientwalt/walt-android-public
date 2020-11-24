package com.weiyu.baselib.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class SysUtils {

    /**
     * 获取App版本名.
     *
     * @param context Context
     * @return App版本名
     */
    public static String getAppVersionName(final Context context) {
        if (context == null) {
            throw new NullPointerException("context不能为空");
        }
        String appVersionName = "Unknown Version Name";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            appVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return appVersionName;
    }

    /**
     * 没有连接网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * wifi网络
     */
    private static final int NETWORK_WIFI = 1;

    public static int getNetWorkState(Context context) {
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            // 得到连接管理器对象
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

                if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                    return NETWORK_WIFI;
                } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                    return NETWORK_MOBILE;
                }
            }
            return NETWORK_NONE;
        } else {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (networkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                        return NETWORK_WIFI;
                    } else if (networkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                        return NETWORK_MOBILE;
                    }
                }
            }
            return NETWORK_NONE;
        }
    }

}
