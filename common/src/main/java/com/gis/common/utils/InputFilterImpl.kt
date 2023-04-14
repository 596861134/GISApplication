package com.gis.common.utils

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

/**
 * 用法
 * edittext.filters = arrayOf(SignedDecimalFilter(0,2), MaximumFilter(type.costUpperLimit ?: 0.0))
 */

/**
 * 限制电话号码
 */
class TelephoneNumberRegexInputFilter: InputFilter {
    override fun filter(
        source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int
    ): CharSequence {
        val builder =  StringBuilder(dest)
        builder.insert(dstart, source)
        val length = builder.length
        if (length == 1) {
            return if (builder[0] == '1') {
                source
            } else {
                ""
            }
        }
        if (length in 1..11) {
            val suffixSize = length - 2
            val pattern = Pattern.compile("^1[3-9]\\d{$suffixSize}$")
            return if (pattern.matcher(builder.toString()).matches()) {
                source
            } else {
                ""
            }
        }
        return ""
    }
}

/**
 * 禁止表情、符号
 */
class EmojiRegexInputFilter:InputFilter {
    override fun filter(
        source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int
    ): CharSequence {
        //表情
        val emoji =
            Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]")
                .apply { flags().and(Pattern.UNICODE_CASE).and(Pattern.CASE_INSENSITIVE) }
        //符号
        val symbol = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]")

        val emojiMatcher = emoji.matcher(source)
        val symbolMatcher = symbol.matcher(source)
        //禁止输入空格、表情、符号
        return if (source == " " || source.toString().contentEquals("\n")
            ||emojiMatcher.find()||symbolMatcher.find()) {
            ""
        }else {
            source
        }
    }
}

/**
 * 禁止表情
 */
class EmojiInputFilter:InputFilter {
    override fun filter(
        source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int
    ): CharSequence {
        //表情
        val emoji =
            Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]")
                .apply { flags().and(Pattern.UNICODE_CASE).and(Pattern.CASE_INSENSITIVE) }

        val emojiMatcher = emoji.matcher(source)
        //禁止输入空格、表情
        return if (source == " " || source.toString().contentEquals("\n") ||emojiMatcher.find()) {
            ""
        }else {
            source
        }
    }
}

// 仅支持数字符号汉字
class InputFilterTxt:InputFilter{
    val pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_]");
    val symbol = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]")

    override fun filter(
        source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int
    ): CharSequence {
        val matcher = pattern.matcher(source)
        val symbolMatcher = symbol.matcher(source)
        return if(!matcher.find()||symbolMatcher.find()) source else ""
    }
}

/**
 * 小数限制2位
 */
class SignedDecimalFilter(min:Int,  numOfDecimals:Int) : InputFilter {

    private var pattern:Pattern = Pattern.compile("^" + (if(min < 0 ) "-?" else "")
    + "[0-9]*\\.?[0-9]" + (if(numOfDecimals > 0 ) ("{0,$numOfDecimals}$") else "*"))

    override fun filter(
        source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int
    ): CharSequence {
        if (source == ".") {
            if (dstart == 0 || dest[dstart - 1] !in '0'..'9') {
                return ""
            }
        }
        //if (source == "0" && (dest.toString()).contains(".") && dstart == 0) { //防止在369.369的最前面输入0变成0369.369这种不合法的形式
        //    return ""
        //}
        val builder = StringBuilder(dest)
        builder.delete(dstart, dend)
        builder.insert(dstart, source)
        if (!pattern.matcher(builder.toString()).matches()) {
            return ""
        }
        return source
    }
}

/**
 * 限制最大输入值
 */
class MaximumFilter(val max:Double):InputFilter{

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        // 获取EditText中已经输入的文本
        val inputText = dest.toString() + source.toString()
        // 判断输入的文本是否超过最大值
        return if ((inputText.toDoubleOrNull() ?: 0.0) > max) {
            // 超过最大值则返回空字符串，即不做任何处理
            ""
        } else {
            // 没有超过最大值则返回null，即不做任何限制
            null
        }
    }
}

/**
 * 转大写
 */
class UpperCaseFilter : InputFilter {

    override fun filter(
        source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int
    ): CharSequence {
        if (dend >= 18) return ""
        return source.toString().uppercase()
    }
}