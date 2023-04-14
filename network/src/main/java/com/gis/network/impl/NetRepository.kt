package com.gis.network.impl

import com.gis.common.mvvm.interfaces.BaseRepository
import com.gis.network.WantService

/**
 * Created by chengzf on 2021/5/13.
 */
open class NetRepository: BaseRepository {

    val api by lazy { WantService.create<ApiService>() }

}