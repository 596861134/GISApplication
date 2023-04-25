package com.gis.common.extension

import android.graphics.Color
import androidx.annotation.ColorInt
import java.text.SimpleDateFormat

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
