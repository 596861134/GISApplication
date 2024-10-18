package com.czf.gis

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.MultiDex
import com.czf.gis.crash.CrashHandler
import com.gis.common.BuildConfig
import com.gis.common.CommonUtil
import com.gis.common.extension.log
import com.gis.common.log.LogHelper
import com.gis.common.manager.AppActivityManager
import com.gis.common.manager.MMKVKey
import com.gis.common.manager.MMKVUtil
import com.gis.common.manager.SPUtils
import com.gis.common.permissions.PermissionInterceptor
import com.gis.common.widget.MaterialHeader
import com.gis.common.widget.SmartBallPulseFooter
import com.gis.common.widget.SplashADView
import com.hjq.permissions.XXPermissions
import com.hjq.toast.Toaster
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.tencent.vasdolly.helper.ChannelReaderUtil

/**
 * Created by chengzf on 2021/5/12.
 */
open class BaseApplication :Application(), ViewModelStoreOwner {


    companion object{

        private val mApplication: BaseApplication by lazy { BaseApplication() }

        @JvmStatic
        fun getInstance(): BaseApplication {
            return mApplication
        }

    }

    override val viewModelStore: ViewModelStore = ViewModelStore()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        if (isAppProcess()){
            CommonUtil.init(this)
            // 初始化日志打印
            LogHelper.init(BuildConfig.LOG_ENABLE, BuildConfig.LOG_TAG)
            // 初始Toast
            Toaster.init(this)
            // 初始化MMKV
            MMKVUtil.init(this.applicationContext)
            // 初始化页面管理
            AppActivityManager.getInstance().init(this)
            // 设置权限请求拦截器（全局设置）
            XXPermissions.setInterceptor(PermissionInterceptor())
            // 初始化crash
            CrashHandler.register(this)
            // 初始化广告view
            SplashADView.getInstance().register(R.mipmap.splash_preview)
            // 设置全局的 Header 构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator{ context: Context, _: RefreshLayout ->
                MaterialHeader(context).setColorSchemeColors(ContextCompat.getColor(context, R.color.teal_700))
            }
            // 设置全局的 Footer 构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator{ context: Context, _: RefreshLayout ->
                SmartBallPulseFooter(context)
            }
            // 设置全局初始化器
            SmartRefreshLayout.setDefaultRefreshInitializer { _: Context, layout: RefreshLayout ->
                // 刷新头部是否跟随内容偏移
                layout.setEnableHeaderTranslationContent(true)
                    // 刷新尾部是否跟随内容偏移
                    .setEnableFooterTranslationContent(true)
                    // 加载更多是否跟随内容偏移
                    .setEnableFooterFollowWhenNoMoreData(true)
                    // 内容不满一页时是否可以上拉加载更多
                    .setEnableLoadMoreWhenContentNotFull(false)
                    // 仿苹果越界效果开关
                    .setEnableOverScrollDrag(false)
            }
            //用户已同意隐私协议才能运行第三方SDK
            if (SPUtils.getData(this.applicationContext, MMKVKey.IS_AGREE_PRIVACY, false)) {
                initContinue()
            }
            "渠道号：${getChannel()}".log()
        }
    }

    /**
     * 初始化需要同意隐私协议的sdk
     */
    fun initContinue() {
        CommonUtil.initContinue()
    }

    fun getChannel():String{
        return ChannelReaderUtil.getChannel(this@BaseApplication)
    }

    /**
     * 获取应用程序级ViewModel，通常用来做消息通信
     */
    fun <T: ViewModel>getApplicationScopeViewModel(viewModel: Class<T>): T {
        return ViewModelProvider(this)[viewModel]
    }

    /**
     *
     * 判断当前的进程是否是APP的进程
     */
    private fun isAppProcess(): Boolean {
        val processName = getProcessName(this, Process.myPid())
        return processName != null && processName.equals(this.packageName, ignoreCase = true)
    }

    /**
     * 获取指定进程名称
     * @param context
     * @return
     */
    private fun getProcessName(context: Context, pid: Int): String? {
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

}