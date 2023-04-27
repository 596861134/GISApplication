package com.gis.common.manager

import android.content.Context
import android.os.Environment
import com.gis.common.extension.log
import com.gis.common.log.LogHelper
import java.io.File
import java.io.FileInputStream
import java.text.DecimalFormat


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/03/01
 *    desc   : 应用缓存管理
 */
object CacheDataManager {

    //获取文件大小单位为B的double值
    const val SIZETYPE_B = 1
    //获取文件大小单位为KB的double值
    const val SIZETYPE_KB = 2
    //获取文件大小单位为MB的double值
    const val SIZETYPE_MB = 3
    //获取文件大小单位为GB的double值
    const val SIZETYPE_GB = 4

    /**
     * 获取缓存大小
     */
    fun getTotalCacheSize(context: Context): String {
        var cacheSize: Double = getFileSizes(context.cacheDir)
        if ((Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)) {
            cacheSize += getFileSizes(context.externalCacheDir!!)
        }
        return formetFileSize(cacheSize)
    }

    /**
     * 清除缓存
     */
    fun clearAllCache(context: Context) {
        deleteDir(context.cacheDir)
        if ((Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)) {
            deleteDir(context.externalCacheDir)
        }
    }

    /**
     * 删除文件夹
     */
    fun deleteDir(dir: File?): Boolean {
        if (dir == null) {
            return false
        }
        if (!dir.isDirectory) {
            return dir.delete()
        }
        val children: Array<out String> = dir.list() ?: return false
        for (child: String in children) {
            deleteDir(File(dir, child))
        }
        return false
    }

    /**
     * 获取指定文件的指定单位的大小
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    fun getFileOrFilesSize(filePath: String, sizeType: Int): Double {
        val file = File(filePath)
        var blockSize = 0.0
        try {
            blockSize = if (file.isDirectory) {
                getFileSizes(file)
            } else {
                getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogHelper.e("获取文件大小,获取失败!")
        }
        return formetFileSize(blockSize, sizeType)
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    fun getAutoFileOrFilesSize(filePath: String): String{
        val file = File(filePath)
        var blockSize = 0.0
        try {
            blockSize = if (file.isDirectory) {
                getFileSizes(file)
            } else {
                getFileSize(file)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LogHelper.e("获取失败!")
        }
        return formetFileSize(blockSize)
    }

    /**
     * 获取指定文件大小
     * @param file
     * @return
     * @throws Exception
     */
    @Throws(java.lang.Exception::class)
    fun getFileSize(file: File): Double {
        var size = 0.0
        if (file.exists()) {
            val fis = FileInputStream(file)
            size = fis.available().toDouble()
        } else {
            file.createNewFile()
            "文件不存在!".log()
        }
        return size
    }

    /**
     * 获取指定文件夹
     * @param f
     * @return
     * @throws Exception
     */
    @Throws(java.lang.Exception::class)
    fun getFileSizes(f: File): Double {
        var size = 0.0
        val flist = f.listFiles()
        for (i in flist.indices) {
            size = if (flist[i].isDirectory) {
                size + getFileSizes(flist[i])
            } else {
                size + getFileSize(flist[i])
            }
        }
        return size
    }

    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    fun formetFileSize(fileS: Double): String {
        val df = DecimalFormat("#.00")
        var fileSizeString = ""
        val wrongSize = "0B"
        if (fileS == 0.0) {
            return wrongSize
        }
        fileSizeString = if (fileS < 1024) {
            df.format(fileS) + "B"
        } else if (fileS < 1048576) {
            df.format(fileS / 1024) + "KB"
        } else if (fileS < 1073741824) {
            df.format(fileS / 1048576) + "MB"
        } else {
            df.format(fileS / 1073741824) + "GB"
        }
        return fileSizeString
    }

    /**
     * 转换文件大小,指定转换的类型
     * @param fileS
     * @param sizeType
     * @return
     */
    fun formetFileSize(fileS: Double, sizeType: Int): Double {
        val df = DecimalFormat("#.00")
        var fileSizeLong = 0.0
        when (sizeType) {
            SIZETYPE_B -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS))
            SIZETYPE_KB -> fileSizeLong =
                java.lang.Double.valueOf(df.format(fileS / 1024))

            SIZETYPE_MB -> fileSizeLong =
                java.lang.Double.valueOf(df.format(fileS / 1048576))

            SIZETYPE_GB -> fileSizeLong =
                java.lang.Double.valueOf(df.format(fileS / 1073741824))

            else -> {}
        }
        return fileSizeLong
    }

}