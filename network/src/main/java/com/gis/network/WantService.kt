package com.gis.network

import com.gis.common.extension.log
import com.gis.network.config.CookieInterceptor
import com.gis.network.config.LocalCookieJar
import com.gis.network.config.LogInterceptor
import io.reactivex.rxjava3.schedulers.Schedulers
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by czf on 2020-01-13.
 */
object WantService {

    private const val READ_TIMEOUT = 60L
    private const val WRITE_TIMEOUT = 60L
    private const val CONNECT_TIMEOUT = 30L

    private var mRetrofit: Retrofit? = null

    private fun getClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .callTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .cookieJar(LocalCookieJar())
            .addNetworkInterceptor(CookieInterceptor())
            .addNetworkInterceptor(LogInterceptor {
                logLevel(LogInterceptor.LogLevel.BODY)
            })
//            .build()
    }

    fun getRetrofit(): Retrofit {
        // 使用RetrofitUrlManager动态配置BaseUrl
        val client = RetrofitUrlManager.getInstance().with(getClient()).build()
        val baseUrl = RetrofitUrlManager.getInstance().globalDomain
        baseUrl.toString().log()
        return mRetrofit ?: Retrofit.Builder()
            .baseUrl(baseUrl)
            .validateEagerly(true)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(client)
            .build()
            .also { mRetrofit = it }
    }

    inline fun <reified T> create(): T = getRetrofit().create(T::class.java)

}