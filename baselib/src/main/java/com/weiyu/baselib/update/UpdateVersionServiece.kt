package com.weiyu.baselib.update

import android.widget.Toast
import com.google.gson.Gson
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.utils.AppUpdateUtils
import com.weiyu.baselib.R
import com.weiyu.baselib.base.BaseActivity
import com.weiyu.baselib.util.PrefUtils
import com.weiyu.baselib.util.SysUtils
import java.util.*


object UpdateVersionServiece {
    var version by PrefUtils("app_new_version", "")
    var path by PrefUtils("app_new_pate", "")
    private val UPDATE_VERSION = ""

    fun updateVersion(context: BaseActivity, appname: String, show: Boolean) {
        val params = HashMap<String, String>()
        //        params.put("version", SysUtils.getAppVersionName(context));
        params["name"] = appname//配置的参数
        UpdateAppManager.Builder()
                //当前Activity
                .setActivity(context)
                //更新地址
                .setUpdateUrl(UPDATE_VERSION)
                .setParams(params)
                .setOnlyWifi()
                .setHttpManager(OkGoUpdateHttpUtil())
                .setPost(true)
                .build()
                .checkNewApp(object : UpdateCallback() {
                    override fun parseJson(json: String): UpdateAppBean {
                        val versionUpdateBean = Gson().fromJson(json, VersionUpdateBean::class.java)
                        val appBean = UpdateAppBean()
                        if (versionUpdateBean.code == 0) {
                            val bean = versionUpdateBean.result
                            val curVersion = SysUtils.getAppVersionName(context)
//                            var cv=curVersion.split(".")
//                            var bv=bean.version.split(".")
                            if (curVersion != bean.version) {
                                appBean.update = "Yes"
                            } else {
                                appBean.update = "No"
                            }
                            appBean.newVersion = bean.version
                            appBean.apkFileUrl = bean.url
                            appBean.showIgnoreVersion(true)
                            appBean.updateLog = bean.remark
                            appBean.isConstraint = bean.type == 1
                        }
                        return appBean

                    }

                    override fun hasNewApp(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) {
                        version = updateApp.newVersion
                        if (updateApp.isConstraint) {
                            updateAppManager.showDialogFragment()
                        } else {
                            if (!show && AppUpdateUtils.isNeedIgnore(context, updateApp.newVersion)) {
                                updateAppManager.setmShowIgnoreVersion(false)
                            } else {
                                if (AppUpdateUtils.isNeedIgnore(context, updateApp.newVersion)) {
                                    updateAppManager.setmShowIgnoreVersion(false)
                                } else {
                                    updateAppManager.setmShowIgnoreVersion(true)
                                }
                                updateAppManager.showDialogFragment()
                            }

                        }
                    }

                    override fun onBefore() {

                    }

                    override fun onAfter() {

                    }


                    override fun noNewApp(msg: String) {
                        version = ""
                        if (show)
                            Toast.makeText(context, context.resources.getString(R.string.check_version), Toast.LENGTH_SHORT).show()
                    }
                })

    }
}
