package com.gis.common.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gis.common.R
import com.gis.common.dialog.BaseDialog
import com.gis.common.dialog.impl.WaitDialog
import com.gis.common.extension.delay
import com.gis.common.extension.falsely
import com.gis.common.extension.truely
import com.gyf.immersionbar.ImmersionBar
import java.io.Serializable

/**
 * Created by chengzf on 2021/5/12.
 */
open class BaseActivity: AppCompatActivity() {

    /** 状态栏沉浸 */
    private var immersionBar: ImmersionBar? = null

    /** 加载对话框 */
    private var dialog: BaseDialog? = null

    /** 对话框数量 */
    private var dialogCount: Int = 0

    /**
     * 当前加载对话框是否在显示中
     */
    open fun isShowDialog(): Boolean {
        return dialog != null && dialog?.isShowing.truely()
    }

    /**
     * 显示加载对话框
     */
    open fun showDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        dialogCount++
        300.delay {
            if ((dialogCount <= 0) || isFinishing || isDestroyed) {
                return@delay
            }
            if (dialog == null) {
                dialog = WaitDialog.Builder(this)
                    .setCancelable(false)
                    .create()
            }
            if (dialog?.isShowing.falsely()) {
                dialog?.show()
            }
        }
    }

    /**
     * 隐藏加载对话框
     */
    open fun dismissDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        if (dialogCount > 0) {
            dialogCount--
        }
        if ((dialogCount != 0) || (dialog == null) || dialog?.isShowing.falsely()) {
            return
        }
        dialog?.dismiss()
    }

    /**
     * 是否使用沉浸式状态栏
     */
    protected open fun isStatusBarEnabled(): Boolean {
        return true
    }

    /**
     * 状态栏字体深色模式
     */
    open fun isStatusBarDarkFont(): Boolean {
        return true
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    open fun getStatusBarConfig(): ImmersionBar {
        if (immersionBar == null) {
            immersionBar = createStatusBarConfig()
        }
        return immersionBar!!
    }

    /**
     * 初始化沉浸式状态栏
     */
    protected open fun createStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this) // 默认状态栏字体颜色为黑色
            .statusBarDarkFont(isStatusBarDarkFont()) // 指定导航栏背景颜色
            .navigationBarColor(R.color.white) // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
            .autoDarkModeEnable(true, 0.2f)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        if (isStatusBarEnabled()){
            getStatusBarConfig().init()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isShowDialog()){
            dismissDialog()
            dialog = null
        }
    }

    /**
     * 如果当前的 Activity（singleTop 启动模式） 被复用时会回调
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // 设置为当前的 Intent，避免 Activity 被杀死后重启 Intent 还是最原先的那个
        setIntent(intent)
    }

    fun startActivity(clazz: Class<*>, vararg data: Pair<String, Any?>) {
        val intent = Intent(this, clazz)

        data.forEach {
            when (it.second) {
                is Boolean -> {
                    intent.putExtra(it.first, it.second as Boolean)
                }
                is Byte -> {
                    intent.putExtra(it.first, it.second as Byte)
                }
                is Int -> {
                    intent.putExtra(it.first, it.second as Int)
                }
                is Short -> {
                    intent.putExtra(it.first, it.second as Short)
                }
                is Long -> {
                    intent.putExtra(it.first, it.second as Long)
                }
                is Float -> {
                    intent.putExtra(it.first, it.second as Float)
                }
                is Double -> {
                    intent.putExtra(it.first, it.second as Double)
                }
                is Char -> {
                    intent.putExtra(it.first, it.second as Char)
                }
                is String -> {
                    intent.putExtra(it.first, it.second as String)
                }
                is Serializable -> {
                    intent.putExtra(it.first, it.second as Serializable)
                }
                is Parcelable -> {
                    intent.putExtra(it.first, it.second as Parcelable)
                }
            }
        }

        startActivity(intent)
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.sdk_slide_right_in,R.anim.sdk_stay_orig)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.sdk_stay_orig,R.anim.sdk_slide_right_out)
    }

    /**
     * 获取指定ViewModel
     */
    fun <T: ViewModel>getAppointViewModel(viewModel: Class<T>): T {
        return ViewModelProvider(this)[viewModel]
    }

}