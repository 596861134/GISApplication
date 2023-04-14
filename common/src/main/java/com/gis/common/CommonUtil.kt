package com.gis.common

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.core.content.pm.PackageInfoCompat
import com.gis.common.log.LogHelper
import com.gis.common.utils.CrashHandler
import com.gis.common.utils.MMKVUtil
import com.hjq.toast.Toaster
import me.jessyan.retrofiturlmanager.RetrofitUrlManager


/**
 * Created by chengzf on 2023/4/12.
 */
object CommonUtil {

    lateinit var mApplication: Application
    lateinit var mContext: Context
    var isDebug: Boolean = true

    private const val BASE_URL = "https://www.wanandroid.com/"

    fun init(application: Application, debug: Boolean) {
        mApplication = application
        mContext = application.applicationContext
        isDebug = debug
        LogHelper.init(debug, "GIS")
        Toaster.init(application)
        CrashHandler.getInstance().init(application.applicationContext)
        MMKVUtil.init(application.applicationContext)
        RetrofitUrlManager.getInstance().setGlobalDomain(BASE_URL)
    }

    /**
     * 判断当前的进程是否是APP的进程
     */
    fun isAppProcess(): Boolean {
        val processName = getProcessName(Process.myPid())
        return processName != null && processName.equals(mContext.packageName, ignoreCase = true)
    }

    /**
     * 获取指定进程名称
     * @param context
     * @param pid   进程号
     * @return
     */
    fun getProcessName(pid: Int): String? {
        val am = mContext.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
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
    fun getAppVersionName(): String {
        var versionName = ""
        try {
            val packageInfo = mContext.packageManager.getPackageInfo(mContext.packageName, 0)
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