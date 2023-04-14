package com.gis.common.utils;

import android.view.View;

/**
 * <b>Project:</b> project_hotclub<br>
 * <b>Create Date:</b> 2017/1/18<br>
 * <b>Author:</b> zou<br>
 * <b>Description:</b> <br>
 */
public class DoubleCLickUtils {

    private static final long DIFF = 500;

    /**
     * 最近一次点击的时间
     */
    private static long mLastClickTime;
    /**
     * 最近一次点击的控件ID
     */
    private static int mLastClickViewId;

    /**
     * 是否是快速点击
     *
     * @param v  点击的控件
     * @return  true:是，false:不是
     */
    public static boolean isFastDoubleClick(View v,long diffTime) {
        int viewId = v.getId();
        long time = System.currentTimeMillis();
        long timeInterval = Math.abs(time - mLastClickTime);
        if (timeInterval < diffTime && viewId == mLastClickViewId) {
            return true;
        } else {
            mLastClickTime = time;
            mLastClickViewId = viewId;
            return false;
        }
    }

    /**
     * 一个参数的方法设置默认的时间
     * @param v
     * @return
     */
    public static boolean isFastDoubleClick(View v) {
       return isFastDoubleClick(v, DIFF);
    }

}
