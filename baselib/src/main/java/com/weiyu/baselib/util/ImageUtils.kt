package com.weiyu.baselib.util

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


/**
 *Created by weiweiyu
 *on 2019/6/4
 */
class ImageUtils {
    companion object {
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun getPicturePathFromUri(context: Context, uri: Uri): String? {
            val sdkVersion = Build.VERSION.SDK_INT
            return if (sdkVersion >= 19) {
                getPicturePathFromUriAboveApi19(context, uri)
            } else {
                getPicturePathFromUriBelowAPI19(context, uri)
            }
        }

        private fun getPicturePathFromUriBelowAPI19(context: Context, uri: Uri): String? {
            return getDataColumn(context, uri, null, null)
        }

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        private fun getPicturePathFromUriAboveApi19(context: Context, uri: Uri): String? {
            var filePath: String? = null
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // 如果是document类型的 uri, 则通过document id来进行处理
                val documentId = DocumentsContract.getDocumentId(uri)
                if (isMediaDocument(uri)) { // MediaProvider
                    // 使用':'分割
                    val id = documentId.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]

                    val selection = MediaStore.Images.Media._ID + "=?"
                    val selectionArgs = arrayOf<String>(id)
                    filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs)
                } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(documentId))
                    filePath = getDataColumn(context, contentUri, null, null)
                }
            } else if ("content".equals(uri.getScheme(), ignoreCase = true)) {
                // 如果是 content 类型的 Uri
                filePath = getDataColumn(context, uri, null, null)
            } else if ("file" == uri.getScheme()) {
                // 如果是 file 类型的 Uri,直接获取图片对应的路径
                filePath = uri.getPath()
            }
            return filePath
        }

        private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
            var path: String? = null

            val projection = arrayOf<String>(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor!!.moveToFirst()) {
                    val columnIndex = cursor!!.getColumnIndexOrThrow(projection[0])
                    path = cursor!!.getString(columnIndex)
                }
            } catch (e: Exception) {
                if (cursor != null) {
                    cursor!!.close()
                }
            }

            return path
        }

        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.getAuthority()
        }

        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.getAuthority()
        }

        fun compressPicture(imgPath: String): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imgPath, options)
            Log.e("Image", "onActivityResult: 未压缩之前图片的宽：" + options.outWidth + "--未压缩之前图片的高："
                    + options.outHeight + "--未压缩之前图片大小:" + options.outWidth * options.outHeight * 4 / 1024 / 1024 + "M")

            options.inSampleSize = calculateInSampleSize(options, 100, 100)
            Log.e("Image", "onActivityResult: inSampleSize:" + options.inSampleSize)
            options.inJustDecodeBounds = false
            val afterCompressBm = BitmapFactory.decodeFile(imgPath, options)
            //      //默认的图片格式是Bitmap.Config.ARGB_8888
            Log.e("Image", "onActivityResult: 图片的宽：" + afterCompressBm.getWidth() + "--图片的高："
                    + afterCompressBm.getHeight() + "--图片大小:" + afterCompressBm.getWidth() * afterCompressBm.getHeight() * 4 / 1024 / 1024 + "M")
            return afterCompressBm
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight = height / 2
                val halfWidth = width / 2

                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }


        fun saveImageToGallery(context: Context, bmp: Bitmap) {
            // 首先保存图片
            val appDir = File(Environment.getExternalStorageDirectory(), "walt")
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val file = File(appDir, fileName)
            try {
                val fos = FileOutputStream(file)
                bmp.compress(CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // 其次把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(context.contentResolver,
                        file.absolutePath, fileName, null)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            // 最后通知图库更新
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://${Environment.getExternalStorageDirectory()}")))
        }
    }
}