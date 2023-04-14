package com.gis.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * 
 * @author czf
 *
 */
public class SPUtils {

	/**
     * 保存在手机里的SP文件名
     */
    public static final String FILE_NAME = "want_want_sp";

    /**
     * 保存数据
     */
    public static void put(Context context, String key, Object obj) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (obj instanceof Boolean) {
            editor.putBoolean(key, (Boolean) obj);
        } else if (obj instanceof Float) {
            editor.putFloat(key, (Float) obj);
        } else if (obj instanceof Integer) {
            editor.putInt(key, (Integer) obj);
        } else if (obj instanceof Long) {
            editor.putLong(key, (Long) obj);
        } else {
            editor.putString(key, (String) obj);
        }
        editor.commit();
    }

    /**
     * 根据泛型存消息
     * @param context
     * @param key
     * @param t
     * @param <T>
     */
    public static <T> void saveData(Context context, String key, T t) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (t instanceof String) {
            editor.putString(key, (String) t).commit();
        } else if (t instanceof Integer) {
            editor.putInt(key, (Integer) t).commit();
        } else if (t instanceof Boolean) {
            editor.putBoolean(key, (Boolean) t).commit();
        } else if (t instanceof Long) {
            editor.putLong(key, (Long) t).commit();
        } else if (t instanceof Float) {
            editor.putFloat(key, (Float) t).commit();
        }
    }

    /**
     * 获取指定数据
     */
    public static Object get(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            return sp.getString(key, "");
    }

    /**
     * 删除指定数据
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 返回所有键值对
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Map<String, ?> map = sp.getAll();
        return map;
    }

    /**
     * 删除所有数据
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 检查key对应的数据是否存在
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 根据泛型获取指定数据
     * @param context
     * @param key
     * @param t
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getData(Context context, String key, T t) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if (t instanceof String) {
            String str = sp.getString(key, (String) t);
            t = (T) str;
        } else if (t instanceof Integer) {
            Integer in = sp.getInt(key, (Integer) t);
            t = (T) in;
        } else if (t instanceof Long) {
            Long lon = sp.getLong(key, (Long) t);
            t = (T) lon;
        } else if (t instanceof Float) {
            Float fl = sp.getFloat(key, (Float) t);
            t = (T) fl;
        } else if (t instanceof Boolean) {
            Boolean bl = sp.getBoolean(key, (Boolean) t);
            t = (T) bl;
        }
        return t;
    }
}
