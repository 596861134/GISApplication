package com.czf.gis

import android.annotation.SuppressLint
import android.content.Intent
import com.czf.gis.databinding.ActivitySplashBinding
import com.gis.common.dialog.impl.UserAgreementDialog
import com.gis.common.extension.delay
import com.gis.common.extension.gone
import com.gis.common.extension.log
import com.gis.common.extension.visible
import com.gis.common.manager.MMKVKey
import com.gis.common.manager.SPUtils
import com.gis.common.mvvm.view.BaseViewModelActivity
import com.gis.common.mvvm.viewmodel.BaseLayoutViewModel
import com.gis.common.utils.FileHelper
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import java.util.Locale

/**
 * Created by chengzf on 2023/4/23.
 */
class SplashActivity:BaseViewModelActivity<BaseLayoutViewModel, ActivitySplashBinding>(ActivitySplashBinding::inflate, BaseLayoutViewModel::class.java) {

    private var hasInitialized = false

    private val mUserAgreementDialog by lazy { UserAgreementDialog.Builder(getContext()) }

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
        mBinding.ivSplashDebug.apply {
            setText(AppConfig.getServerType().uppercase(Locale.getDefault()))
            if (AppConfig.isDebug()){
                visible()
            }else {
                gone()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 检查是否已初始化
        if (!hasInitialized) {
            // 在启动页提前进行预登录以提高效率
            if (!SPUtils.getData(getContext(), MMKVKey.IS_FIRST_USER, false)) {
                // 跳转隐私协议
                mUserAgreementDialog.setOnClick {
                    // 同意
                    FileHelper.getInstance().readHiddenFileFromDownload(getContext()) { result ->
                        mUserAgreementDialog.dismiss()
                        // 处理成功返回的字符串
                        "UUID: $result".log()
                        // 首次打开，保存状态
                        SPUtils.saveData(getContext(), MMKVKey.IS_FIRST_USER, true)
                        // 同意隐私协议
                        SPUtils.saveData(getContext(), MMKVKey.IS_AGREE_PRIVACY, true)
                        // 标记为已初始化
                        hasInitialized = true
                        // 继续初始化
                        BaseApplication.getInstance().initContinue()
                        // 继续运行
                        initContinue()
                    }
                }.show()
            } else {
                FileHelper.getInstance().readHiddenFileFromDownload(getContext()) { result ->
                    // 处理成功返回的字符串
                    "UUID: $result".log()
                    // 标记为已初始化
                    hasInitialized = true
                    // 继续运行
                    initContinue()
                }
            }
        }
    }


    private fun initContinue() {
        1500.delay {
            startActivity(MainActivity::class.java)
            finish()
        }
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig().hideBar(BarHide.FLAG_HIDE_BAR)
    }

    // 禁用返回键
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }

}