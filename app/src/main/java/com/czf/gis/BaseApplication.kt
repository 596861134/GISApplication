package com.czf.gis

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.MultiDex
import com.czf.gis.crash.CrashHandler
import com.gis.common.CommonUtil
import com.gis.common.extension.log
import com.gis.common.widget.MaterialHeader
import com.gis.common.widget.SmartBallPulseFooter
import com.gis.common.widget.SplashADView
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
        CommonUtil.init(this@BaseApplication)
        CrashHandler.register(this@BaseApplication)
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
        "渠道号：${getChannel()}".log()
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

}