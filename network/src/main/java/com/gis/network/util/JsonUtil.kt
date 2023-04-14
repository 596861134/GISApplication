package com.gis.network.util

import android.text.TextUtils
import com.gis.common.log.LogHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by chengzf on 2021/5/12.
 */
object JsonUtil {

    /**
     * 格式化字符串
     */
    fun json(content: String?) :String{
        var message = "Invalid Json"
        var json:String? = content
        if (TextUtils.isEmpty(json)) {
            LogHelper.d("Empty/Null json content")
            return "Empty/Null json content"
        }
        try {
            json = json!!.trim { it <= ' ' }
            if (json.startsWith("{")) {
                val jsonObject = JSONObject(json)
                message = jsonObject.toString(2)
            }
            if (json.startsWith("[")) {
                val jsonArray = JSONArray(json)
                message = jsonArray.toString(2)
            }
        } catch (e: JSONException) {
            message = "Invalid Json :${e.message}"
        }
        return message
    }

}