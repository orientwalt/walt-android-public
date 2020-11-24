/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yjy.wallet;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.entry.ApplicationLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.weiyu.baselib.tinker.BaseLike;

import org.bitcoinj.utils.BriefLogFormatter;
import org.litepal.LitePal;

/**
 * because you can not use any other class in your application, we need to
 * move your implement of Application to {@link ApplicationLifeCycle}
 * As Application, all its direct reference class should be in the main dex.
 * <p>
 * We use tinker-android-anno to make sure all your classes can be patched.
 * <p>
 * application: if it is start with '.', we will add SampleApplicationLifeCycle's package name
 * <p>
 * flags:
 * TINKER_ENABLE_ALL: support dex, lib and resource
 * TINKER_DEX_MASK: just support dex
 * TINKER_NATIVE_LIBRARY_MASK: just support lib
 * TINKER_RESOURCE_MASK: just support resource
 * <p>
 * loaderClass: define the tinker loader class, we can just use the default TinkerLoader
 * <p>
 * loadVerifyFlag: whether check files' md5 on the load time, defualt it is false.
 * <p>
 * Created by zhangshaowen on 16/3/17.
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = "com.yjy.wallet.WApplication",//指定路径和和命名
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)
public class WApplicationLike extends BaseLike {
    public WApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                            long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(getApplication());
        ZXingLibrary.initDisplayOpinion(getApplication());
        CrashReport.initCrashReport(getApplication(), "ae50158086", false);
        BriefLogFormatter.init();
//        CustomPushNotificationBuilder builder = new
//                CustomPushNotificationBuilder(getApplication(),
//                R.layout.customer_notitfication_layout_one,
//                R.id.icon,
//                R.id.title,
//                R.id.text);
//        // 指定定制的 Notification Layout
//        builder.statusBarDrawable = R.mipmap.ic_launcher;
//        // 指定最顶层状态栏小图标
//        builder.layoutIconDrawable = R.mipmap.ic_launcher;
//        // 指定下拉状态栏时显示的通知图标
//        JPushInterface.setPushNotificationBuilder(2, builder);
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
//                return new BezierRadarHeader(context).setEnableHorizontalDrag(false);
//                layout.setPrimaryColorsId(R.color.white, R.color.title_bg_5f8def);
                return new MaterialHeader(context).setShowBezierWave(false);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new BallPulseFooter(context).setSpinnerStyle(SpinnerStyle.Scale);
            }
        });
    }

}
