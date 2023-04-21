package com.gis.common.manager

import android.content.Context
import android.os.Parcelable
import com.gis.common.BuildConfig
import com.gis.common.extension.LogEnum
import com.gis.common.extension.logWithTag
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVHandler
import com.tencent.mmkv.MMKVLogLevel

object MMKVUtil{

    private lateinit var mmkv:MMKV

    fun init(context: Context){
        // /data/user/0/com.module.plug/files
        MMKV.initialize(context, context.filesDir.absolutePath, null, MMKVLogLevel.LevelDebug,
            object : MMKVHandler{
                override fun onMMKVCRCCheckFail(mmapID: String?) = null

                override fun onMMKVFileLengthError(mmapID: String?) = null

                override fun wantLogRedirecting() = BuildConfig.LOG_ENABLE

                override fun mmkvLog(level: MMKVLogLevel?, file: String?, line: Int, function: String?, message: String?) {
                    val log = "<$file:$line::$function> $message"
                    when (level) {
                        MMKVLogLevel.LevelDebug -> "redirect logging MMKV $log".logWithTag("MMKV", LogEnum.VERBOSE)
                        MMKVLogLevel.LevelInfo -> "redirect logging MMKV $log".logWithTag("MMKV", LogEnum.INFO)
                        MMKVLogLevel.LevelWarning -> "redirect logging MMKV $log".logWithTag("MMKV", LogEnum.WARN)
                        MMKVLogLevel.LevelError,
                        MMKVLogLevel.LevelNone -> "redirect logging MMKV $log".logWithTag("MMKV", LogEnum.ERROR)
                        else -> "redirect logging MMKV $log".logWithTag("MMKV", LogEnum.VERBOSE)
                    }
                }
        })
        mmkv = MMKV.mmkvWithID("MMKV_${context.packageName}")
    }

    /**
     * 保存数据
     */
    fun <T>encode(key: String, value: T) {
        when (value) {
            is String -> {
                mmkv.encode(key, value.toString())
            }
            is Int -> {
                mmkv.encode(key, value.toInt())
            }
            is Boolean -> {
                mmkv.encode(key, value)
            }
            is Float -> {
                mmkv.encode(key, value.toFloat())
            }
            is Long -> {
                mmkv.encode(key, value.toLong())
            }
            is Double -> {
                mmkv.encode(key, value.toDouble())
            }
            is ByteArray -> {
                mmkv.encode(key, value)
            }
            else -> {
                mmkv.encode(key, value.toString())
            }
        }
    }

    fun encodeSet(key: String?, sets: Set<String>?) {
        mmkv.encode(key, sets)
    }

    fun encodeParcelable(key: String?, obj: Parcelable?) {
        mmkv.encode(key, obj)
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    fun decodeInt(key: String): Int {
        return mmkv.decodeInt(key, 0)
    }

    fun decodeDouble(key: String): Double {
        return mmkv.decodeDouble(key, 0.00)
    }

    fun decodeLong(key: String): Long {
        return mmkv.decodeLong(key, 0L)
    }

    fun decodeBoolean(key: String): Boolean {
        return mmkv.decodeBool(key, false)
    }

    fun decodeFloat(key: String): Float {
        return mmkv.decodeFloat(key, 0f)
    }

    fun decodeBytes(key: String): ByteArray? {
        return mmkv.decodeBytes(key)
    }

    fun decodeString(key: String): String {
        return mmkv.decodeString(key, "").toString()
    }

    fun decodeStringSet(key: String): MutableSet<String>? {
        return mmkv.decodeStringSet(key, HashSet<String>())
    }

    fun decodeParcelable(key: String): Parcelable? {
        return mmkv.decodeParcelable(key, null)
    }

    /**
     * 移除某个key对
     *
     * @param key
     */
    fun removeKey(key: String?) {
        mmkv.removeValueForKey(key)
    }

    /**
     * 清除所有key
     */
    fun clearAll() {
        mmkv.clearAll()
    }

    /**
     * 检查key对应的数据是否存在
     */
    fun containsKey(key: String):Boolean{
        return mmkv.containsKey(key)
    }
}