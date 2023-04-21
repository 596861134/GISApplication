package com.czf.gis

import com.gis.network.impl.NetRepository
import okhttp3.RequestBody
import retrofit2.http.Body

/**
 * Created by chengzf on 2023/4/20.
 */
class MainRepository:NetRepository() {

    suspend fun userLogout(@Body body: RequestBody) = api.userLogout(body)
}