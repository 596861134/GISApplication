package com.czf.gis

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.core.content.pm.PackageInfoCompat
import com.gis.common.BuildConfig
import com.gis.common.CommonUtil

/**
 * Created by chengzf on 2021/5/12.
 */
open class BaseApplication:Application() {


    companion object{
        private lateinit var app: BaseApplication
        private lateinit var context: Context

        @JvmStatic
        fun getContext():Context{
            return context
        }

        @JvmStatic
        fun getApplication(): BaseApplication {
            return app
        }

        @JvmStatic
        fun isDebug():Boolean{
            return BuildConfig.DEBUG
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        app = this@BaseApplication
        context = this.applicationContext
        CommonUtil.init(getApplication(), isDebug())
    }

}