package com.gis.common

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Created by chengzf on 2021/5/12.
 */
open class BaseApplication:Application(), ViewModelStoreOwner {

    private val mAppViewModelStore: ViewModelStore by lazy { ViewModelStore() }

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

        CommonUtil.init()
    }

    override val viewModelStore: ViewModelStore = mAppViewModelStore

    /**
     *
     * 判断当前的进程是否是APP的进程
     */
    open fun isAppProcess(): Boolean {
        val processName = getProcessName(this, Process.myPid())
        return processName != null && processName.equals(this.packageName, ignoreCase = true)
    }

    /**
     * 获取指定进程名称
     * @param context
     * @param pid   进程号
     * @return
     */
    open fun getProcessName(context: Context, pid: Int): String? {
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                if (procInfo.processName != null) {
                    return procInfo.processName
                }
            }
        }
        return null
    }

    /**
     * 应用版本号
     * @return String
     */
    open fun getAppVersionName(): String {
        var versionName = ""
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
            versionName = packageInfo.versionName
            if (versionName.isEmpty()) {
                return ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionName
    }

}