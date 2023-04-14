package com.gis.common

import com.gis.common.log.LogHelper
import com.gis.common.utils.CrashHandler
import com.gis.common.utils.MMKVUtil
import com.hjq.toast.Toaster
import me.jessyan.retrofiturlmanager.RetrofitUrlManager


/**
 * Created by chengzf on 2023/4/12.
 */
object CommonUtil {


    private const val BASE_URL = "https://www.wanandroid.com/"

    fun init(){
        LogHelper.init(BaseApplication.isDebug(), "GIS")
        Toaster.init(BaseApplication.getApplication())
        CrashHandler.getInstance().init(BaseApplication.getContext())
        MMKVUtil.init(BaseApplication.getApplication())
        RetrofitUrlManager.getInstance().setGlobalDomain(BASE_URL)

    }

}