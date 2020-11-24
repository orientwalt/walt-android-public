package com.weiyu.baselib.util

import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import com.lzy.okserver.download.DownloadListener
import com.lzy.okserver.download.DownloadTask
import java.io.File

/**
 *Created by weiweiyu
 *on 2019/5/30
 */

class DownloadUtils private constructor() {
    companion object {
        val instance: DownloadUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DownloadUtils()
        }
    }

    fun add(url: String, listener: DownloadListener) {
        val selectTack = OkDownload.getInstance().getTask(url)
        if (selectTack != null) {
            selectTack.register(listener).start()
        } else {
            val request = OkGo.get<File>(url)
            OkDownload.request(url, request).save().register(listener).fileName(url.substring(url.lastIndexOf("/"))).start()
        }
    }

    fun add(url: String, fileName: String, listener: DownloadListener) {
        val selectTack = OkDownload.getInstance().getTask(url)
        if (selectTack != null) {
            selectTack.save().register(listener).start()
        } else {
            val request = OkGo.get<File>(url)
            OkDownload.request(url, request).save().register(listener).fileName(fileName).start()
        }
    }
}