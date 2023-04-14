package com.gis.common.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.gis.common.extension.showToast
import java.io.File


/**
 *
 * @ClassName:      ImageUtils
 * @Description:    图片操作工具类
 * @Author:         jiang
 * @CreateDate:     2022/3/24 11:02 下午
 */
object ImageUtils {

    /**
     * 保存图片
     * Android 9（API 级别 28）或更低版本的设备需要先请求存储相关权限
     * @param displayName 带后缀文件名
     */
    fun saveImage(resolver: ContentResolver, bitmap: Bitmap, displayName: String) {
        // 创建图片索引
        val value = ContentValues()
        value.put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
        value.put(MediaStore.Images.Media.MIME_TYPE, "image/*")

        // 拿到 MediaStore.Images 表的uri
        val tableUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            value.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            val img =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .absolutePath + File.separator + displayName
            value.put(MediaStore.MediaColumns.DATA, img)
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        try {
            val imageUri = resolver.insert(tableUri, value)
            if (imageUri != null) {
                val os = resolver.openOutputStream(imageUri)
                // 图片压缩保存
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)) {
                    "图片保存成功".showToast()
                } else {
                    "图片保存失败".showToast()
                }
                os?.close()
            } else {
                "图片保存失败".showToast()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "图片保存失败".showToast()
        }
    }


    /**
     * 为 Bitmap 设置切割圆角
     *
     * @param source 原图片
     * @param lt     左上圆角 px
     * @param rt     右上圆角 px
     * @param rb     右下圆角 px
     * @param lb     左下圆角 px
     * @return 按照参数切割圆角后的 Bitmap
     */
    fun getRoundBitmap(source: Bitmap, lt: Float, rt: Float, rb: Float, lb: Float): Bitmap? {
        var lt = lt
        var rt = rt
        var rb = rb
        var lb = lb
        val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        paint.isAntiAlias = true
        // 设置画笔为原图
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val width = source.width.toFloat()
        val height = source.height.toFloat()
        // 绘制四个圆角，只四个圆角的话，结果如图1
        canvas.drawArc(RectF(0.0f, 0.0f, lt, lt), 180f, 90f, true, paint)
        canvas.drawArc(RectF(width - rt, 0.0f, width, rt), 270f, 90f, true, paint)
        canvas.drawArc(RectF(width - rb, height - rb, width, height), 0f, 90f, true, paint)
        canvas.drawArc(RectF(0.0f, height - lb, lb, height), 90f, 90f, true, paint)
        // 绘制剩余区域
        // 因为上边是圆角，我们只绘制了一个半径，所以除2就是剩余要绘制的区域了
        // 不除2的话，结果如图2
        lt /= 2f
        rt /= 2f
        rb /= 2f
        lb /= 2f
        val path = Path()
        path.moveTo(0f, lt)
        path.moveTo(lt, lt)
        path.moveTo(lt, 0f)
        path.lineTo(width - rt, 0f)
        path.lineTo(width - rt, rt)
        path.lineTo(width, rt)
        path.lineTo(width, height - rb)
        path.lineTo(width - rb, height - rb)
        path.lineTo(width - rb, height)
        path.lineTo(lb, height)
        path.lineTo(lb, height - lb)
        path.lineTo(0f, height - lb)
        path.lineTo(0f, lt)
        path.close()
        canvas.drawPath(path, paint)
        return result
    }

    /**
     * 判断是否图片地址
     */
    fun isImageFile(filePath: String): Boolean {
        val imgArr = filePath.split('.')
        val suffix = setOf("png", "jpg", "jpeg")
        return suffix.contains(imgArr.last())
    }
}