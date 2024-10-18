package com.gis.network.config

import com.gis.common.manager.MMKVUtil
import com.gis.common.utils.FileHelper
import com.gis.network.BuildConfig
import com.gis.network.util.Constant
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by chengzf on 2023/5/4.
 * TokenInterceptor 添加请求头
 */
class TokenInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = MMKVUtil.decodeString(Constant.ACCESS_TOKEN)
        val builder = chain.request().newBuilder()
        if (token.isNotEmpty()){
            builder.addHeader("TOKEN", token)
        }
        builder.addHeader("OS","Android")
        builder.addHeader("APP_VERSION", BuildConfig.VERSIONNAME)
        builder.addHeader("DEVICE_ID", FileHelper.getInstance().getUdtId())
        return chain.proceed(builder.build())
    }
}