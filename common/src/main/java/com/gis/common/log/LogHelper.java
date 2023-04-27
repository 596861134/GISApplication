package com.gis.common.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author czf
 * <a href="https://github.com/orhanobut/logger"/>Log封装库</a>
 */
public class LogHelper {

    public static void init(boolean isShowLog) {
        init(isShowLog, "CLog");
    }

    public static void init(boolean isShowLog, @Nullable String tag) {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                // (Optional) 是否显示线程信息。默认为true
                .showThreadInfo(false)
                // (Optional) 要显示多少方法行。默认值2，数值越大调用链越清晰
                .methodCount(0)
                // (Optional) 跳过堆栈跟踪中的一些方法调用。默认值5
                .methodOffset(0)
                // (Optional) 将日志策略更改为打印输出。默认LogCat
                // .logStrategy(customLog)
                // (Optional) 每个日志的自定义标记。默认PRETTY_LOGGER
                .tag(tag)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return isShowLog;
            }
        });
    }

    public static void d(@Nullable Object object) {
        Logger.d(object);
    }

    public static void d(String tag, @Nullable Object object) {
        Logger.t(tag).d(object);
    }

    public static void d(@NonNull String message, @Nullable Object... args) {
        Logger.d(message, args);
    }


    public static void e(String object) {
        Logger.e(object);
    }

    public static void e(String tag, String object) {
        Logger.t(tag).e(object);
    }

    public static void e(@NonNull String message, @Nullable Object... args) {
        Logger.e(null, message, args);
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        Logger.e(throwable, message, args);
    }

    public static void i(String object) {
        Logger.i(object);
    }

    public static void i(String tag, String object) {
        Logger.t(tag).i(object);
    }

    public static void i(@NonNull String message, @Nullable Object... args) {
        Logger.i(message, args);
    }

    public static void v(String object) {
        Logger.v(object);
    }

    public static void v(String tag, String object) {
        Logger.t(tag).v(object);
    }

    public static void v(@NonNull String message, @Nullable Object... args) {
        Logger.v(message, args);
    }

    public static void w(String object) {
        Logger.w(object);
    }

    public static void w(String tag, String object) {
        Logger.t(tag).w(object);
    }

    public static void w(@NonNull String message, @Nullable Object... args) {
        Logger.w(message, args);
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public static void wtf(@NonNull String message, @Nullable Object... args) {
        Logger.wtf(message, args);
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(@Nullable String json) {
        Logger.json(json);
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(@Nullable String xml) {
        Logger.xml(xml);
    }

}
