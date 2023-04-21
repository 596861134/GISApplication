package com.czf.gis

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import com.gis.common.CommonUtil
import com.gis.common.widget.MaterialHeader
import com.gis.common.widget.SmartBallPulseFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * Created by chengzf on 2021/5/12.
 */
open class BaseApplication:Application() {


    companion object{

        private val mApplication: BaseApplication by lazy { BaseApplication() }

        @JvmStatic
        fun getInstance(): BaseApplication {
            return mApplication
        }

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        CommonUtil.init(this@BaseApplication)

        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator{ context: Context, layout: RefreshLayout ->
            MaterialHeader(context).setColorSchemeColors(ContextCompat.getColor(context, R.color.teal_700))
        }
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator{ context: Context, layout: RefreshLayout ->
            SmartBallPulseFooter(context)
        }
        // 设置全局初始化器
        SmartRefreshLayout.setDefaultRefreshInitializer { context: Context, layout: RefreshLayout ->
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

    }

}