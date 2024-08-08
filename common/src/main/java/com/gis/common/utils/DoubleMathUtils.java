package com.gis.common.utils;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;

import com.gis.common.CommonUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DoubleMathUtils {
    /**
     * 保留两位小数，四舍五入的一个老土的方法
     *
     * @param d
     * @return
     */
    public static double formatDouble1(double d) {
        return (double) Math.round(d * 100) / 100;
    }


    /**
     * The BigDecimal class provides operations for arithmetic, scale manipulation, rounding, comparison, hashing, and format conversion.
     *
     * @param d
     * @return
     */
    public static String formatDouble2(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)){
            //防止后端返回数据错误报错
            return "0.0";
        }
        BigDecimal bg = BigDecimal.valueOf(d);
        // 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
        String price =  bg.setScale(2, BigDecimal.ROUND_HALF_UP).toString();


//        String price = String.valueOf(d);
        if (price.indexOf(".") > 0) {
            price = price.replaceAll("0+?$", "");//去掉多余的0
            price = price.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return price;

       /* BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        double num = bg.doubleValue();
        if (Math.round(num) - num == 0) {
            return String.valueOf((long) num);
        }
        return String.valueOf(num);*/
    }

    /**
     * NumberFormat is the abstract base class for all number formats.
     * This class provides the interface for formatting and parsing numbers.
     *
     * @param d
     * @return
     */
    public static String formatDouble3(double d) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(2);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.DOWN);


        return nf.format(d);
    }


    /**
     * 这个方法挺简单的。
     * DecimalFormat is a concrete subclass of NumberFormat that formats decimal numbers.
     *
     * @param d
     * @return
     */
    public static String formatDouble4(double d) {
        DecimalFormat df = new DecimalFormat("#.00");


        return df.format(d);
    }


    /**
     * 如果只是用于程序中的格式化数值然后输出，那么这个方法还是挺方便的。
     * 应该是这样使用：System.out.println(String.format("%.2f", d));
     *
     * @param d
     * @return
     */
    public static String formatDouble5(double d) {
        return String.format("%.2f", d);
    }

    //默认除法运算精度
    private static final int DEF_DIV_SCALE = 10;

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */

    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2);
    }

    /**
     * 提供精确的加法运算
     *
     * @param v1    被加数
     * @param v2    加数
     * @param scale 保留scale 位小数
     * @return 两个参数的和
     */
    public static String add(String v1, String v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal sub(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2);
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1    被减数
     * @param v2    减数
     * @param scale 保留scale 位小数
     * @return 两个参数的差
     */
    public static String sub(String v1, String v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static BigDecimal subDouble(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(0);
        return b1.subtract(b2).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2);
    }


    /**
     * 提供精确的乘法运算
     *
     * @param v1    被乘数
     * @param v2    乘数
     * @param scale 保留scale 位小数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2, int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return round(b1.multiply(b2).doubleValue(), scale);
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1    被乘数
     * @param v2    乘数
     * @param scale 保留scale 位小数
     * @return 两个参数的积
     */
    public static String mul(String v1, String v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }


    /**
     * 提供精确的小数位四舍五入处理
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static String round(String v, int scale) {
        if (TextUtils.isEmpty(v)) {
            if (scale < 0) {
                throw new IllegalArgumentException(
                        "The scale must be a positive integer or zero");
            }
            BigDecimal b = new BigDecimal(v);
            return b.setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        } else {

            return "";
        }

    }


    /**
     * 取余数
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 小数点后保留几位
     * @return 余数
     */
    public static String remainder(String v1, String v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.remainder(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 取余数  BigDecimal
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 小数点后保留几位
     * @return 余数
     */
    public static BigDecimal remainder(BigDecimal v1, BigDecimal v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        return v1.remainder(v2).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 比较大小
     *
     * @param v1 被比较数
     * @param v2 比较数
     * @return 如果v1 大于v2 则 返回true 否则false
     */
    public static boolean compare(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        int bj = b1.compareTo(b2);
        boolean res;
        if (bj > 0) {
            res = true;
        } else {
            res = false;
        }
        return res;
    }


    /**
     * 给double数值添加千分位分隔符，
     * 支持正负数，
     * 支持保留2位小数四舍五入，
     * 支持设置小数点后面数字字体的大小
     * 支持设置自动装换为万
     *
     * @param num               源数据
     * @param decimalFontSize   小数字体大小
     * @param isConverter       是否进制转换
     * @return  格式化字符
     */
    public static SpannableString formatDoubleWithSeparator(double num, int decimalFontSize, boolean isConverter) {
        // 转换为万
        double formattedNum = num;
        String unit = "";

        if (isConverter){
            int base = 10000;
            if (Math.abs(formattedNum) >= base) {
                formattedNum = formattedNum / base;
                unit = "万";
            }
        }

        // 保留2位小数
        String formattedNumber = DoubleMathUtils.formatDouble2(formattedNum);

        // 检查负号
        String sign = "";
        if (num < 0) {
            sign = "-";
            // 去掉负号进行处理
            formattedNumber = formattedNumber.substring(1);
        }

        // 分割整数和小数部分
        String[] parts = formattedNumber.split("\\.");
        String integerPart = parts[0];
        String decimalPart = parts.length > 1 ? "." + parts[1] : "";

        // 给整数部分添加千分位分隔符
        StringBuilder result = new StringBuilder();
        for (int i = integerPart.length() - 1, count = 0; i >= 0; i--) {
            result.insert(0, integerPart.charAt(i));
            if (++count % 3 == 0 && i > 0) {
                result.insert(0, ",");
            }
        }

        // 创建具有不同字体大小的 SpannableStringBuilder
        SpannableString spannable = new SpannableString(sign + result + decimalPart + unit);
        // 有小数点 或有单位， 且有设置字体大小
        boolean condition = (formattedNumber.contains(".") || !unit.isEmpty()) && decimalFontSize > 0;
        if (condition) {
            // 设置小数点后面数字字体大小，若不需要处理单位，结束位 end = spannable.length() - unit.length()
            int start = sign.length() + result.length();
            int end = spannable.length();
            spannable.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(CommonUtil.mContext, decimalFontSize)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 用逗号(,)把字符串隔开 默认隔开首个小数点前面的
     * eg: 123456.00  -> 123,456.000
     * 重写使其支持正负数
     *
     * @return
     */
    public static String spitByComma(String target) {
        if (null == target || TextUtils.isEmpty(target)) {
            return "";
        }

        // 检查负号
        boolean isNegative = false;
        if (target.startsWith("-")) {
            isNegative = true;
            // 移除负号
            target = target.substring(1);
        }

        // 分割整数和小数部分
        String[] parts = target.split("\\.");
        String integerPart = parts[0];
        String decimalPart = parts.length > 1 ? "." + parts[1] : "";

        // 给整数部分添加千分位分隔符
        StringBuilder result = new StringBuilder();
        for (int i = integerPart.length() - 1, count = 0; i >= 0; i--) {
            result.insert(0, integerPart.charAt(i));
            if (++count % 3 == 0 && i > 0) {
                result.insert(0, ",");
            }
        }

        // 添加负号（如果有）
        if (isNegative) {
            result.insert(0, "-");
        }

        // 返回结果
        return result.toString() + decimalPart;
    }


    /**
     * 商品价格、单位格式化 兼容formatPrice(double price, int decSize)
     *
     * @param price      商品价格
     * @param unitString 商品单位
     * @param decSize    小数字体大小
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder formatPrice(String price, int decSize, String unitString) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        // 小数处理
        SpannableString spannableString = formatPrice(Double.parseDouble(price), decSize);
        // 拼接
        builder.append(spannableString);
        if (!TextUtils.isEmpty(unitString)) {
            // 单位处理
            SpannableString unitSpannable = new SpannableString("/" + unitString);
            unitSpannable.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(CommonUtil.mContext, decSize)), 0, unitSpannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            // 拼接
            builder.append(unitSpannable);
        }
        return builder;
    }

    /**
     * 商品价格格式化
     *
     * @param price   商品价格
     * @param decSize 小数字体大小
     * @return
     */
    public static SpannableString formatPrice(double price, int decSize) {
        String target = DoubleMathUtils.formatDouble2(price);
        SpannableString spannableString = new SpannableString(target);
        if (target.contains(".")) {
            xTextSize(spannableString, decSize, target.indexOf("."), target.length());
        }
        return spannableString;
    }

    public static void xTextSize(SpannableString spannableString, int textSpSize, int start, int end) {
        spannableString.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(CommonUtil.mContext, textSpSize)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
