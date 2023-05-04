package com.gis.network.impl

import com.gis.common.CommonUtil
import com.gis.common.manager.MMKVUtil
import com.gis.network.config.persistentcookiejar.ClearableCookieJar
import com.gis.network.config.persistentcookiejar.PersistentCookieJar
import com.gis.network.config.persistentcookiejar.cache.SetCookieCache
import com.gis.network.config.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.gis.network.util.Constant
import okhttp3.CookieJar

/**
 * Created by chengzf on 2023/5/4.
 */
object CookieImpl {

    private var cookieJar: ClearableCookieJar? = null

    fun getCookieJar(): CookieJar {
       cookieJar = PersistentCookieJar(SetCookieCache(),
            SharedPrefsCookiePersistor(CommonUtil.mContext))
        return cookieJar as CookieJar
    }

    /**
     * 退出登录操作
     * 清除cookie、token
     */
    fun cleanCookie(){
        cookieJar?.clear()
        MMKVUtil.removeKey(Constant.ACCESS_TOKEN)
    }


}