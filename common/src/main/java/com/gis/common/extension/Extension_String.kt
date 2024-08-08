package com.gis.common.extension

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.net.URL
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 如果转换出错 那么返回 null
 */
fun String.safeParseInt():Int?{
    try {
        return toInt()
    }catch (e:Exception){
        return  null
    }
}

/**
 * 如果转换出错 那么返回 null
 */
fun String.safeParseFloat():Float?{
    try {
        return toFloat()
    }catch (e:Exception){
        return  null
    }
}

/**
 * 如果转换出错 那么返回 null
 */
fun String.safeParseDouble():Double?{
    try {
        return toDouble()
    }catch (e:Exception){
        return  null
    }
}

//计算两个时间字符串的差值
//两个时间格式必须一样
//2020-10-21 20:17:58 SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
fun String.caculatTimeDiffer(
        simpleDateFormat: SimpleDateFormat,
        end: String): Long {
    try {
        return toTime(simpleDateFormat) - end.toTime(simpleDateFormat)
    } catch (e: Exception) {
        return 0
    }
}

fun String.toTime(simpleDateFormat: SimpleDateFormat): Long {
    return try {
        simpleDateFormat.parse(this)?.time ?: 0
    } catch (e: Exception) {
        0
    }
}

fun String.toColorOrNull(): Int? {
    //Color.parseColor(it.backgroundColor)
    try {
        return Color.parseColor(this)
    } catch (e: Exception) {
    }
    return null
}

fun String.toColorOrDefault(@ColorInt default: Int): Int? {
    return toColorOrNull() ?: default
}

/**
 * 指定日期格式转换
 * @param time      时间
 * @param format    时间格式
 * @param byFormat  要转换的格式
 * @return
 */
fun timeByFormat(time: String, format: String, byFormat: String): String {
    if (time.isNullOrEmpty()) {
        return ""
    }
    val dateTime = DateTime.parse(time, DateTimeFormat.forPattern(format))
    val date = dateTime.toDate()
    val simpleDateFormat = SimpleDateFormat(byFormat)
    return simpleDateFormat.format(date)
}

/**
 * 判断url中的文件名是否被转义
 * https://hotkidceo-1251330842.file.myqcloud.com/2.6M%E5%95%86%E5%AE%B6%E7%89%88.pdf
 */
fun String.isUrlEncoded(): Boolean {
    try {
        val parsedUrl = URL(this)
        val path = parsedUrl.path
        val decodedPath = URLDecoder.decode(path, "UTF-8")
        return if (path == decodedPath) {
            "文件名未被转义".log()
            false
        } else {
            "文件名已被转义".log()
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // 解析异常则使用正则表达式来检查
        val pattern: Pattern = Pattern.compile("%[0-9a-fA-F]{2}")
        val matcher: Matcher = pattern.matcher(this)
        return matcher.find()
    }
}

/**
 * 获取真实的url
 */
fun String.getRealFromUrl(): String {
    return if (this.isUrlEncoded()) URLDecoder.decode(this, "UTF-8") else this
}

/**
 * 通过url获取文件名称
 */
fun String.getFileNameFromUrl(): String {
    val uri = Uri.parse(this.getRealFromUrl())
    return uri.pathSegments.safeGet(uri.pathSegments.size - 1) ?: ""
}
