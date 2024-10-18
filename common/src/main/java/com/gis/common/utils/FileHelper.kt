package com.gis.common.utils

import android.content.Context
import android.os.Environment
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

}