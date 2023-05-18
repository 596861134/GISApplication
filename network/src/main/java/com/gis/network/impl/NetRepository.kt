package com.gis.network.impl

import com.gis.common.mvvm.interfaces.BaseRepository
import com.gis.network.WantService

/**
 * Created by chengzf on 2021/5/13.
 */
open class NetRepository: BaseRepository {

    private var mApi:ApiService? = null

    fun getBaeApi():ApiService{
        return mApi?: WantService.create<ApiService>().also { mApi = it }
    }

    fun cleanCache(){
        mApi = null
    }

    companion object{

        private val mInstance: NetRepository by lazy { NetRepository() }
        fun getInstance():NetRepository{
            return mInstance
        }

    }

//    val api by lazy { WantService.create<ApiService>() }

}