package com.gis.common.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.gis.common.R
import com.gis.common.extension.getResString
import com.gis.common.extension.isNotNull
import com.gis.common.extension.log
import com.gis.common.extension.showToast
import com.gis.common.manager.MMKVKey
import com.gis.common.manager.MMKVUtil
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import java.util.UUID


/**
 * Created by chengzf on 2024/10/17.
 * 生成唯一设备ID 存储到外部存储的Download目录下，保证系统生命周期唯一
 */
class FileHelper {

    companion object{

        const val FILE_NAME = "WANT_UUID"

        const val FILES_NAME = "项目框架"

        private val mInstance: FileHelper by lazy { FileHelper() }

        @JvmStatic
        fun getInstance(): FileHelper {
            return mInstance
        }

    }


    /**
     * 在外部存储的Download目录下创建隐藏目录并写入数据
     */
    private fun createHiddenFileInDownload(content: String, fileName: String = FILE_NAME) {
        // 获取外部存储的Download目录
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (downloadDir == null) {
            "无法访问外部存储的Download目录".log()
        }

        // 创建隐藏文件
        val hiddenFile = File(downloadDir, ".$fileName") // 文件名前加.表示隐藏文件

        // 写入数据到文件
        try {
            FileOutputStream(hiddenFile).use { fos ->
                fos.write(content.toByteArray())
            }
            "文件创建成功：${hiddenFile.path}".log()
        } catch (e: IOException) {
            "创建或写入文件时出错: ${e.message}".log()
            e.printStackTrace()
        }
    }

    /**
     * 读取隐藏文件内容
     */
    fun readHiddenFileFromDownload(context: Context, success:(String) -> Unit) {
        XXPermissions.with(context).permission(
            Permission.MANAGE_EXTERNAL_STORAGE
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (all) {
                    // 获取外部存储的Download目录
                    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (downloadDir.isNotNull()) {
                        // 获取隐藏文件
                        val hiddenFile = File(downloadDir, ".$FILE_NAME") // 文件名前加.表示隐藏文件

                        if (hiddenFile.exists()){
                            try {
                                // 读取文件内容
                                val content = StringBuilder()
                                content.append(readFileContent(hiddenFile))
                                if (content.isNotEmpty()){
                                    "文件内容存在:$content".log()
                                    MMKVUtil.encode(MMKVKey.ANDROID_DEVICES_ID, content.toString())
                                }
                            } catch (e: IOException) {
                                "读取文件时出错: ${e.message}".log()
                                e.printStackTrace()
                            }
                            success.invoke(getUdtId())
                        }else {
                            "文件不存在".log()
                            createHiddenFileInDownload(getUdtId())
                            success.invoke(getUdtId())
                        }
                    }else {
                        "无法访问外部存储的Download目录".log()
                        success.invoke(getUdtId())
                    }
                }
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                R.string.granted_faild.getResString().showToast()
                success.invoke(getUdtId())
            }
        })
    }

    private fun readFileContent(file: File): String {
        val content = StringBuilder()
        FileInputStream(file).buffered().use { fis ->
            val buffer = ByteArray(1024)
            var byteRead: Int
            while (fis.read(buffer).also { byteRead = it } != -1) {
                content.append(String(buffer, 0, byteRead, Charsets.UTF_8))
            }
        }
        return content.toString()
    }

    /**
     * 生成设备唯一标识，只能保证本次安装唯一
     * @return
     */
    fun getUdtId(): String {
        var udtId: String = MMKVUtil.decodeString(MMKVKey.ANDROID_DEVICES_ID)
        // 避免每次都重新计算
        if (udtId == null || "" == udtId) {
            udtId = "ANDROID-" + UUID.randomUUID().toString().uppercase(Locale.getDefault())
            MMKVUtil.encode(MMKVKey.ANDROID_DEVICES_ID, udtId)
            return udtId
        }
        return udtId
    }


    /**
     * 保存bitmap图片到系统相册
     * Android 9（API 级别 28）或更低版本的设备需要先请求存储相关权限
     * @param displayName 带后缀文件名
     */
    fun saveImage(context: Context, resolver: ContentResolver, bitmap: Bitmap, displayName: String, realName:((String) -> Unit)? = null) {
        // 创建图片索引
        XXPermissions.with(context).permission(
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (all) {
                    val path =  saveImage(resolver, bitmap, displayName)
                    realName?.invoke(path)
                } else {
                    showPermissionDeniedToast()
                }
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                showPermissionDeniedToast()
            }
        })
    }

    /**
     * 保存图片到系统相册
     */
    fun saveImage(context: Context, resolver: ContentResolver, imageFile: File, displayName: String, realName: ((String) -> Unit)? = null) {
        // 创建图片索引
        XXPermissions.with(context).permission(
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (all) {
                    val path =  saveImage(resolver, imageFile, displayName)
                    realName?.invoke(path)
                } else {
                    showPermissionDeniedToast()
                }
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                showPermissionDeniedToast()
            }
        })
    }

    /**
     * 视频保存到系统相册
     */
    fun saveVideo(context: Context, resolver: ContentResolver, file: File, displayName: String, realName:((String) -> Unit)? = null) {
        // 创建视频索引
        XXPermissions.with(context).permission(
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (all) {
                    val path = saveVideo(resolver, file, displayName)
                    realName?.invoke(path)
                } else {
                    showPermissionDeniedToast()
                }
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                showPermissionDeniedToast()
            }
        })
    }

    /**
     * 存储文件到 /Download
     */
    fun saveFileToDownload(context: Context, resolver: ContentResolver, file: File, displayName: String, realName:((String) -> Unit)? = null) {
        XXPermissions.with(context).permission(
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (all) {
                    val path = saveFileToDownload(resolver, file, displayName)
                    realName?.invoke(path)
                } else {
                    showPermissionDeniedToast()
                }
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                showPermissionDeniedToast()
            }
        })

    }

    private fun saveImage(resolver: ContentResolver, bitmap: Bitmap, displayName: String):String {
        var realPath = getGlobalFilePath(resolver, displayName)
        if(isFileExists(resolver, displayName) && !realPath.isNullOrEmpty()){
            realPath.log()
            updateFileTimestamp(realPath)
            "保存成功，图片已保存至系统相册".showToast()
            return realPath
        }

        val value = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis()) // 设置时间戳
        }

        // 拿到 MediaStore.Images 表的uri
        // 指定自定义目录
        val tableUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上版本使用 RELATIVE_PATH
            value.put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/${FILES_NAME}")
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            // Android 9 及以下版本使用 DATA
            val imageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), FILES_NAME)
            if (!imageDir.exists()) {
                imageDir.mkdirs() // 创建文件夹
            }
            value.put(MediaStore.MediaColumns.DATA, File(imageDir, displayName).absolutePath) // 使用绝对路径
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        try {
            val imageUri = resolver.insert(tableUri, value)
            if (imageUri != null) {
                resolver.openOutputStream(imageUri)?.use { os ->
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)) {
                        realPath = getRealPathFromURI(resolver, imageUri)
                        realPath?.log()
                        "保存成功，图片已保存至系统相册".showToast()
                    } else {
                        "图片保存失败".showToast()
                    }
                }?: run {
                    "图片保存失败".showToast()
                }
            } else {
                "图片保存失败".showToast()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Failed to save image: ${e.message}".log()
            "图片保存失败".showToast()
        }
        return realPath ?:""
    }

    private fun saveImage(resolver: ContentResolver, imageFile: File, displayName: String):String {
        var realPath = getGlobalFilePath(resolver, displayName)
        if (isFileExists(resolver, displayName) && !realPath.isNullOrEmpty()) {
            realPath.log()
            updateFileTimestamp(realPath)
            "保存成功，图片已保存至系统相册".showToast()
            return realPath
        }

        val value = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/*")  // 或者根据实际文件类型来设定
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis()) // 设置时间戳
        }

        // 拿到 MediaStore.Images 表的uri
        // 指定自定义目录
        val tableUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上版本使用 RELATIVE_PATH
            value.put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/${FILES_NAME}")
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            // Android 9 及以下版本使用 DATA
            val imageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), FILES_NAME)
            if (!imageDir.exists()) {
                imageDir.mkdirs() // 创建文件夹
            }
            value.put(MediaStore.MediaColumns.DATA, File(imageDir, displayName).absolutePath) // 使用绝对路径
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        try {
            val imageUri = resolver.insert(tableUri, value)
            if (imageUri != null) {
                // 使用流复制文件内容
                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    FileInputStream(imageFile).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    imageFile.delete()
                    realPath = getRealPathFromURI(resolver, imageUri)
                    realPath?.log()
                    "保存成功，图片已保存至系统相册".showToast()
                } ?: run {
                    "图片保存失败".showToast()
                }
            } else {
                "图片保存失败".showToast()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Failed to save image: ${e.message}".log()
            "图片保存失败".showToast()
        }
        return realPath ?: imageFile.path
    }

    private fun saveVideo(resolver: ContentResolver, file: File, displayName: String):String {
        var realPath = getGlobalFilePath(resolver, displayName)
        if(isFileExists(resolver, displayName) && !realPath.isNullOrEmpty()){
            realPath.log()
            updateFileTimestamp(realPath)
            "保存成功，视频已保存至系统相册".showToast()
            return realPath
        }

        val value = ContentValues()
        value.put(MediaStore.Video.Media.DISPLAY_NAME, displayName)
        value.put(MediaStore.Video.Media.MIME_TYPE, "video/*")
        value.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis()) // 设置时间戳

        // 拿到 MediaStore.Video 表的uri
        val tableUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上版本使用 RELATIVE_PATH
            value.put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/${FILES_NAME}")
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            // Android 9 及以下版本使用 DATA
            val imageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), FILES_NAME)
            if (!imageDir.exists()) {
                imageDir.mkdirs() // 创建文件夹
            }
            value.put(MediaStore.MediaColumns.DATA, File(imageDir, displayName).absolutePath) // 使用绝对路径
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        // 输出输入流
        try {
            val bufferSize = 8192
            val videoUri = resolver.insert(tableUri, value)
            if (videoUri != null) {
                resolver.openOutputStream(videoUri)?.use { outputStream ->
                    FileInputStream(file).use { inputStream ->
                        inputStream.copyTo(outputStream, bufferSize = bufferSize) // 可以调整缓冲区大小
                    }
                    file.delete()
                    realPath = getRealPathFromURI(resolver, videoUri)
                    realPath?.log()
                    "保存成功，视频已保存至系统相册".showToast()
                } ?: run {
                    "视频保存失败".showToast()
                }
            } else {
                "视频保存失败".showToast()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "视频保存失败".showToast()
            "Unexpected error: ${e.message}".log()
        }
        return realPath ?: file.path
    }

    private fun saveFileToDownload(resolver: ContentResolver, file: File, displayName: String):String {
        var realPath = getGlobalFilePath(resolver, displayName)
        if(isFileExists(resolver, displayName) && !realPath.isNullOrEmpty()){
            realPath.log()
            updateFileTimestamp(realPath)
            "保存成功，文件已保存至/Download/${FILES_NAME}".showToast()
            return realPath
        }

        val value = ContentValues()
        value.put(MediaStore.Downloads.DISPLAY_NAME, displayName)
        value.put(MediaStore.Downloads.MIME_TYPE, getMimeType(file))
        value.put(MediaStore.Downloads.DATE_TAKEN, System.currentTimeMillis()) // 设置时间戳

        // 拿到 MediaStore.Downloads 表的uri
        val tableUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上版本使用 RELATIVE_PATH
            value.put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/${FILES_NAME}")
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            // Android 9 及以下版本使用 DATA
            val imageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILES_NAME)
            if (!imageDir.exists()) {
                imageDir.mkdirs() // 创建文件夹
            }
            value.put(MediaStore.MediaColumns.DATA, File(imageDir, displayName).absolutePath) // 使用绝对路径
            MediaStore.Files.getContentUri("external")
        }

        val bufferSize = 8192 // 1024 * 1024 // 1MB
        try {
            val fileUri = resolver.insert(tableUri, value)
            if (fileUri != null) {
                resolver.openOutputStream(fileUri)?.use { outputStream ->
                    FileInputStream(file).use { inputStream ->
                        inputStream.copyTo(outputStream, bufferSize = bufferSize) // 可以调整缓冲区大小
                    }
                    file.delete()
                    realPath = getRealPathFromURI(resolver, fileUri)
                    realPath?.log()
                    "保存成功，文件已保存至/Download/${FILES_NAME}".showToast()
                } ?: run {
                    "文件保存失败".showToast()
                }
            }else {
                "文件保存失败".showToast()
                "Failed to insert file into MediaStore".log()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "文件保存失败".showToast()
            "Unexpected error: ${e.message}".log()
        }
        return realPath ?: file.path
    }

    private fun showPermissionDeniedToast() {
        R.string.granted_faild.getResString().showToast()
    }

    private fun getMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "mp4", "avi", "mkv" -> "video/*"
            "jpg", "jpeg", "png", "gif" -> "image/*"
            "pdf" -> "application/pdf"
            "txt" -> "text/plain"
            else -> "*/*"
        }
    }

    /**
     * 通过查询 MediaStore 得到插入的文件的真实路径
     */
    private fun getRealPathFromURI(contentResolver: ContentResolver, uri: Uri): String {
        var path: String? = null
        val projection = arrayOf(MediaStore.MediaColumns.DATA)

        // 查询对应的 MediaStore 表
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                path = cursor.getString(columnIndex)
            }
        }

        return path ?: ""
    }

    /**
     * 通过查询 MediaStore 得到插入的文件的真实路径
     */
    fun getGlobalFilePath(resolver: ContentResolver, displayName: String): String? {
        // 定义要查询的列
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(displayName)

        // 查询 MediaStore 所有类型的文件
        val uri = MediaStore.Files.getContentUri("external")

        // 执行查询
        resolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            }
        }

        return null // 文件不存在或未找到路径
    }

    /**
     * 更新文件时间戳
     */
    fun updateFileTimestamp(path: String) {
        val file = File(path)
        val modified = file.setLastModified(System.currentTimeMillis())
        if (modified) "成功更新已存在文件时间戳：${path}".log() else "更新失败".log()
    }

    /**
     * 判断文件是否已存在
     */
    fun isFileExists(contentResolver: ContentResolver, displayName: String): Boolean {
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(displayName)

        // 根据 API 版本选择适当的 URI
        val uris = listOf(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Files.getContentUri("external")
            }
        )

        // 查询所有类型的文件
        for (uri in uris) {
            contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                if (cursor.count > 0) {
                    return true // 文件存在
                }
            }
        }

        return false // 所有查询都未找到文件
    }

    /**
     * 删除现有的文件
     */
    private fun deleteExistingFile(resolver: ContentResolver, displayName: String): Boolean {
        // 定义要查询的列
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(displayName)

        // 查询 MediaStore 下载目录
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }
        resolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                // 获取文件 ID
                val fileId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val fileUri = ContentUris.withAppendedId(uri, fileId)

                // 删除文件
                return resolver.delete(fileUri, null, null) > 0
            }
        }

        return false // 文件不存在或删除失败
    }

}