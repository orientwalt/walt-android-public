package com.weiyu.baselib.update;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class RxPermissionUtils {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void requestPermission(Activity activity, String[] permission, final PermissionsResultCallback callback) {
        if(activity==null||activity.isDestroyed())return;
        new RxPermissions(activity).requestEach(permission)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {

                            callback.onSuccessPermission();

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框

                            callback.onFailPermission();

                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            callback.onFailPermission();
                        }


                    }
                });






    }

    public interface PermissionsResultCallback{

        void onSuccessPermission();
        void onFailPermission();

    }

}
