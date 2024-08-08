package com.gis.common.extension

import android.text.SpannableString
import android.text.SpannableStringBuilder
import com.gis.common.utils.DoubleMathUtils


/**
 * 保留2位小数
 */
fun Double?.formatDouble2(): String = DoubleMathUtils.formatDouble2(this ?: 0.0)

/**
 * 保留2位小数，并设置小数点后数字大小
 * @param decSize 小数点后数字大小
 */
fun Double?.formatDouble2(decSize: Int): SpannableString = DoubleMathUtils.formatPrice(this ?: 0.0, decSize)

/**
 * 保留2位小数，添加千位符，并设置小数点后数字大小
 * @param decSize 小数点后数字大小
 */
fun Double?.formatSpitDouble2(decSize: Int): SpannableString = formatMoney(decSize)

/**
 * 保留2位小数，设置小数点后数字大小，并添加单位
 * @param decSize 小数点后数字大小
 * @param unit  单位
 */
fun Double?.formatSpitDouble2Unit(decSize: Int, unit:String): SpannableStringBuilder = DoubleMathUtils.formatPrice(this.formatDouble2(), decSize, unit)

/**
 * 添加千位符
 */
fun String?.spitByComma(): String = DoubleMathUtils.spitByComma(this)

/**
 * 保留2位小数，添加千位符
 */
fun Double?.formatMoney() = (this ?: 0.0).formatDouble2().spitByComma()

/**
 * 金额处理
 *      给double数值添加千分位分隔符，
 *      支持正负数，
 *      支持保留2位小数四舍五入，
 *      支持设置小数点后面数字字体的大小
 *      支持设置自动转换为万
 * @param decimalFontSize   小数字体大小
 * @param isConverter       是否自动转换为万
 */
fun Double?.formatMoney(decimalFontSize:Int = 0, isConverter:Boolean = false): SpannableString = DoubleMathUtils.formatDoubleWithSeparator(this ?: 0.0, decimalFontSize, isConverter)
