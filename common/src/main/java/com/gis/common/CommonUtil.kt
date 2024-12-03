package com.gis.common

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Process
import androidx.core.content.ContextCompat
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.gis.common.extension.showToast
import com.gis.common.manager.AppActivityManager
import me.jessyan.retrofiturlmanager.RetrofitUrlManager


/**
 * Created by chengzf on 2023/4/12.
 */
object CommonUtil {

    lateinit var mContext: Context

    fun init(application: Application) {
        mContext = application.applicationContext
    }

    fun initContinue() {
        RetrofitUrlManager.getInstance().setGlobalDomain(BuildConfig.HOST)
        registerNet(mContext)
    }

    private fun registerNet(context: Context){
        // 注册网络状态变化监听
        val connectivityManager: ConnectivityManager? = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    val topActivity: Activity? = AppActivityManager.getInstance().getTopActivity()
                    topActivity?.let {
                        if (topActivity !is LifecycleOwner) {
                            return
                        }
                        val lifecycleOwner: LifecycleOwner = topActivity
                        if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.RESUMED) {
                            return
                        }
                        "当前网络不可用，请检查网络设置".showToast()
                    }
                }
            })
        }
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
            versionName = packageInfo.versionName ?:""
            if (versionName.isEmpty()) {
                return ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionName
    }

}