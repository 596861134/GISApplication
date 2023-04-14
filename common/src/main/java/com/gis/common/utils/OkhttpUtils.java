package com.gis.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @ProjectName: CEO_APP
 * @Package: com.want.hotkidclub.ceo.mvp.utils
 * @ClassName: OkhttpUtils
 * @Description: java类作用描述
 * @Author: bagelly
 * QQ:774169396
 * @CreateDate: 2019-08-14 16:31
 */
public class OkhttpUtils {
    /**
     * 把一个对象转换成 json格式的 RequestBody
     *
     * @param obj
     * @return
     */
    public static RequestBody objToRequestBody(Object obj) {
        return objToRequestBody(obj, false);
    }

    /**
     * 把一个对象转换成 json格式的 RequestBody
     *
     * @param obj
     * @param ignoreNullField 是否忽略值为null的属性
     * @return
     */
    public static RequestBody objToRequestBody(Object obj, boolean ignoreNullField) {

        if (obj == null) {
            return null;
        }

        Gson gson = null;
        if (ignoreNullField) {
            gson = new GsonBuilder().serializeNulls().create();
        } else {
            gson = new Gson();
        }
        return RequestBody.Companion.create(gson.toJson(obj), MediaType.parse("application/json; charset=utf-8"));
    }

}
