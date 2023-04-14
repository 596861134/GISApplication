package com.gis.network.impl

import com.gis.network.ObjectAnyBean
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by chengzf on 2021/5/13.
 */
interface ApiService {

    //退出
    @POST(PathConstant.LOGOUT)
    suspend fun userLogout(@Body body: RequestBody): ObjectAnyBean

}