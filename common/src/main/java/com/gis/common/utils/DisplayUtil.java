package com.gis.common.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * @author chengzf
 *
 * <p>1.在编写Android代码时，建议使用Context对象而不是Resources.getSystem（），
 * 因为后者在某些情况下会导致问题。例如，如果您正在运行后台服务，Resources.getSystem（）将无法工作，
 * 因为它需要一个活动的“活动”。使用Context对象可以确保您的代码在所有情况下都能工作。</p>
 *
 * <p>2.将dp转换为px时，添加0.5是将结果四舍五入到最近整数的常用技术。
 * 这是因为从dp到px的转换因子并不总是整数，并且可能会出现舍入错误。
 * 通过在强制转换为整数之前加0.5，将结果四舍五入到最接近的整数。
 * 配合Math.round()使用，用于将一个数字四舍五入为最接近的整数。</p>
 */
public class DisplayUtil {

    /**
     * 将dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dipValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(spValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(pxValue / scale + 0.5f);
    }

    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(pxValue / scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return Math.round(spValue * scale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return Math.round(pxValue / scale + 0.5f);
    }

}