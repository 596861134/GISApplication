package com.gis.network.config

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Created by czf on 2020-01-16.
 */
class LocalCookieJar : CookieJar {

    //cookie的本地化存储
    private val cache = mutableListOf<Cookie>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        //过期的Cookie
        val invalidCookies: MutableList<Cookie> = ArrayList()
        //有效的Cookie
        val validCookies: MutableList<Cookie> = ArrayList()

        for (cookie in cache) {
            if (cookie.expiresAt < System.currentTimeMillis()) {
                //判断是否过期
                invalidCookies.add(cookie)
            } else if (cookie.matches(url)) {
                //匹配Cookie对应url
                validCookies.add(cookie)
            }
        }
        //缓存中移除过期的Cookie
        cache.removeAll(invalidCookies)

        //返回List<Cookie>让Request进行设置
        return validCookies
//        return emptyList()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cache.addAll(cookies)
    }
}