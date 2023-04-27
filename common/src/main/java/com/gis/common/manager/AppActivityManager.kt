package com.gis.common.manager

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import androidx.collection.ArrayMap
import com.gis.common.extension.LogEnum
import com.gis.common.extension.log
import kotlin.system.exitProcess

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/11/18
 *    desc   : Activity 管理类
 */
class AppActivityManager private constructor() : ActivityLifecycleCallbacks {

    companion object {

        private val activityManager: AppActivityManager by lazy { AppActivityManager() }

        @JvmStatic
        fun getInstance(): AppActivityManager {
            return activityManager
        }

        /**
         * 获取一个对象的独立无二的标记
         */
        private fun getObjectTag(`object`: Any): String {
            // 对象所在的包名 + 对象的内存地址
            return `object`.javaClass.name + Integer.toHexString(`object`.hashCode())
        }
    }

    /** Activity 存放集合 */
    private val activitySet: ArrayMap<String?, Activity?> = ArrayMap()

    /** 应用生命周期回调 */
    private val lifecycleCallbacks: ArrayList<ApplicationLifecycleCallback> = ArrayList()

    /** 当前应用上下文对象 */
    private lateinit var application: Application

    /** 栈顶的 Activity 对象 */
    private var topActivity: Activity? = null

    /** 前台并且可见的 Activity 对象 */
    private var resumedActivity: Activity? = null

    fun init(application: Application) {
        this.application = application
        this.application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 获取 Application 对象
     */
    fun getApplication(): Application {
        return application
    }

    /**
     * 获取Context
     */
    fun getContext(): Context {
        return application.applicationContext
    }

    /**
     * 获取栈顶的 Activity
     */
    fun getTopActivity(): Activity? {
        return topActivity
    }

    /**
     * 获取前台并且可见的 Activity
     */
    fun getResumedActivity(): Activity? {
        return resumedActivity
    }

    /**
     * 判断当前应用是否处于前台状态
     */
    fun isForeground(): Boolean {
        return getResumedActivity() != null
    }

    /**
     * 注册应用生命周期回调
     */
    fun registerApplicationLifecycleCallback(callback: ApplicationLifecycleCallback) {
        lifecycleCallbacks.add(callback)
    }

    /**
     * 取消注册应用生命周期回调
     */
    fun unregisterApplicationLifecycleCallback(callback: ApplicationLifecycleCallback) {
        lifecycleCallbacks.remove(callback)
    }

    /**
     * 销毁指定的 Activity
     */
    fun finishActivity(clazz: Class<out Activity?>?) {
        if (clazz == null) {
            return
        }
        val keys: Array<String?> = activitySet.keys.toTypedArray()
        for (key: String? in keys) {
            val activity: Activity? = activitySet[key]
            if (activity == null || activity.isFinishing) {
                continue
            }
            if ((activity.javaClass == clazz)) {
                activity.finish()
                activitySet.remove(key)
                break
            }
        }
    }

    /**
     * 销毁所有的 Activity
     */
    fun finishAllActivities() {
        finishAllActivities(null as Class<out Activity?>?)
    }

    /**
     * 销毁所有的 Activity
     *
     * @param classArray            白名单 Activity
     */
    @SafeVarargs
    fun finishAllActivities(vararg classArray: Class<out Activity>?) {
        val keys: Array<String?> = activitySet.keys.toTypedArray()
        for (key: String? in keys) {
            val activity: Activity? = activitySet[key]
            if (activity == null || activity.isFinishing) {
                continue
            }
            var whiteClazz = false
            for (clazz: Class<out Activity?>? in classArray) {
                if ((activity.javaClass == clazz)) {
                    whiteClazz = true
                }
            }
            if (whiteClazz) {
                continue
            }

            // 如果不是白名单上面的 Activity 就销毁掉
            activity.finish()
            activitySet.remove(key)
        }
    }

    /**
     * 退出应用程序
     */
    fun exitApp(context: Context) {
        try {
            finishAllActivities()
            val activityMgr =
                context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            activityMgr.restartPackage(context.packageName)
            exitProcess(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        String.format("%s - onCreate", activity.javaClass.simpleName).log(LogEnum.INFO)
        if (activitySet.size == 0) {
            for (callback: ApplicationLifecycleCallback? in lifecycleCallbacks) {
                callback?.onApplicationCreate(activity)
            }
            String.format("%s - onApplicationCreate", activity.javaClass.simpleName).log(LogEnum.INFO)
        }
        activitySet[getObjectTag(activity)] = activity
        topActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        String.format("%s - onStart", activity.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onActivityResumed(activity: Activity) {
        String.format("%s - onResume", activity.javaClass.simpleName).log(LogEnum.INFO)
        if (topActivity === activity && resumedActivity == null) {
            for (callback: ApplicationLifecycleCallback in lifecycleCallbacks) {
                callback.onApplicationForeground(activity)
            }
            String.format("%s - onApplicationForeground", activity.javaClass.simpleName).log(LogEnum.INFO)
        }
        topActivity = activity
        resumedActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        String.format("%s - onPause", activity.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onActivityStopped(activity: Activity) {
        String.format("%s - onStop", activity.javaClass.simpleName).log(LogEnum.INFO)
        if (resumedActivity === activity) {
            resumedActivity = null
        }
        if (resumedActivity == null) {
            for (callback: ApplicationLifecycleCallback in lifecycleCallbacks) {
                callback.onApplicationBackground(activity)
            }
            String.format("%s - onApplicationBackground", activity.javaClass.simpleName).log(LogEnum.INFO)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        String.format("%s - onSaveInstanceState", activity.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onActivityDestroyed(activity: Activity) {
        String.format("%s - onDestroy", activity.javaClass.simpleName).log(LogEnum.INFO)
        activitySet.remove(getObjectTag(activity))
        if (topActivity === activity) {
            topActivity = null
        }
        if (activitySet.size == 0) {
            for (callback: ApplicationLifecycleCallback in lifecycleCallbacks) {
                callback.onApplicationDestroy(activity)
            }
            String.format("%s - onApplicationDestroy", activity.javaClass.simpleName).log(LogEnum.INFO)
        }
    }

    /**
     * 应用生命周期回调
     */
    interface ApplicationLifecycleCallback {

        /**
         * 第一个 Activity 创建了
         */
        fun onApplicationCreate(activity: Activity)

        /**
         * 最后一个 Activity 销毁了
         */
        fun onApplicationDestroy(activity: Activity)

        /**
         * 应用从前台进入到后台
         */
        fun onApplicationBackground(activity: Activity)

        /**
         * 应用从后台进入到前台
         */
        fun onApplicationForeground(activity: Activity)
    }
}