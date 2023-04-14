package com.gis.network.config

import com.gis.network.util.Constant
import com.gis.common.utils.MMKVUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by czf on 2020-01-16.
 */
class CookieInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val finalResponse: Response

        val cookies = MMKVUtil.decodeStringSet(Constant.KEY_COOKIE)

        if (cookies.isNullOrEmpty()) {
            val originResponse = chain.proceed(chain.request())

//            if (!originResponse.headers("Set-Cookie").isNullOrEmpty()) {
//                val tempCookies = hashSetOf<String>()
//                originResponse.headers("Set-Cookie").forEach {
//                    tempCookies.add(it)
//                }
//                MmkvUtil.saveCookie(tempCookies)
//            }

            finalResponse = originResponse

        } else {
            val builder = chain.request().newBuilder()
            cookies.forEach {
                it.let { builder.addHeader("Cookie", it) }
            }
            //解决okhttp java.io.IOException: unexpected end of stream on https://www.wanandroid.com/...
            builder.addHeader("Connection", "close")
            finalResponse = chain.proceed(builder.build())
        }

        return finalResponse
    }

}