package com.gis.common.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gis.common.BaseApplication
import com.gis.common.R
import com.gis.common.dialog.LoadingDialog
import com.gis.common.extension.isNotNull
import com.gis.common.utils.AppManager
import com.gis.common.utils.KeyboardStatusDetector
import java.io.Serializable

/**
 * Created by chengzf on 2021/5/12.
 */
open class BaseActivity:AppCompatActivity() {

    private val mDialog: LoadingDialog by lazy { LoadingDialog(this) }

    fun showDialog(){
        if (!mDialog.isShowing){
            mDialog.show()
        }
    }

    fun dismissDialog(){
        if (mDialog.isShowing){
            mDialog.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.getAppManager().addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.getAppManager().finishActivity(this)
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
     * 隐藏键盘
     *
     * @param view
     */
    open fun hiddenKeyBoard(view: View) {
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (KeyboardStatusDetector.isKeyBoardShow(view)) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * 隐藏键盘
     */
    open fun hiddenKeyBoard() {
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager//得到InputMethodManager的实例
        if (imm.isActive && currentFocus.isNotNull()) {
            //拿到view的token 不为空
            if (currentFocus?.windowToken.isNotNull()) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    private val mApplicationProvider by lazy { ViewModelProvider(applicationContext as BaseApplication) }

    protected fun <T: ViewModel> getApplicationScopeViewModel(@NonNull modelClass:Class<T> ):T {
        return mApplicationProvider[modelClass]
    }

    /**
     * 获取指定ViewModel
     */
    fun <T: ViewModel>getAppointViewModel(viewModel: Class<T>): T {
        return ViewModelProvider(this)[viewModel]
    }

}