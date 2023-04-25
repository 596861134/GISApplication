package com.czf.gis

import android.content.Intent
import com.czf.gis.databinding.ActivitySplashBinding
import com.gis.common.extension.delay
import com.gis.common.extension.gone
import com.gis.common.extension.visible
import com.gis.common.mvvm.view.BaseViewModelActivity
import com.gis.common.mvvm.viewmodel.BaseLayoutViewModel
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import java.util.*

/**
 * Created by chengzf on 2023/4/23.
 */
class SplashActivity:BaseViewModelActivity<BaseLayoutViewModel, ActivitySplashBinding>(
    R.layout.activity_splash, BaseLayoutViewModel::class.java) {

    override fun beforeSetView() {
        super.beforeSetView()
        // 问题及方案：https://www.cnblogs.com/net168/p/5722752.html
        // 如果当前 Activity 不是任务栈中的第一个 Activity
        if (!isTaskRoot) {
            val intent: Intent? = intent
            // 如果当前 Activity 是通过桌面图标启动进入的
            if (((intent != null) && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                        && (Intent.ACTION_MAIN == intent.action))) {
                // 对当前 Activity 执行销毁操作，避免重复实例化入口
                finish()
                return
            }
        }
    }

    override fun onViewInit() {
        super.onViewInit()

        1500.delay {
            startActivity(MainActivity::class.java)
            finish()
        }

        mBinding.ivSplashDebug.apply {
            setText(AppConfig.getServerType().uppercase(Locale.getDefault()))
            if (AppConfig.isDebug()){
                visible()
            }else {
                gone()
            }
        }
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig().hideBar(BarHide.FLAG_HIDE_BAR)
    }

    // 禁用返回键
    override fun onBackPressed() {
    }

}