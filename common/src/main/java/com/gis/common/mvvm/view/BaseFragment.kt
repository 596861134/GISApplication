package com.gis.common.mvvm.view

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gis.common.action.BundleAction
import com.gis.common.action.HandlerAction
import com.gis.common.action.KeyboardAction
import com.gis.common.extension.isNotNull
import com.gis.common.extension.truely
import com.gis.common.utils.KeyboardStatusDetector
import java.io.Serializable

/**
 * Created by chengzf on 2021/5/13.
 */
open class BaseFragment:Fragment(),
    HandlerAction, BundleAction, KeyboardAction {

    lateinit var mActivity: BaseActivity
    /** 当前是否加载过 */
    private var loading: Boolean = false

    /** ActivityResult回调 */
    var onForResult: ((ActivityResult) -> Unit)? = null

    /**
     * 得到InputMethodManager的实例
     */
    val mInputMethodManager by lazy { mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }


    /**
     * 这个 Fragment 是否已经加载过了
     */
    open fun isLoading(): Boolean {
        return loading
    }

    /**
     * Fragment 可见回调
     *
     * @param first                 是否首次调用
     */
    open fun onFragmentResume(first: Boolean) {}

    /**
     * Activity 可见回调
     */
    open fun onActivityResume() {}

    /**
     * 获取绑定的 Activity，防止出现 getActivity 为空
     */
    open fun getAttachActivity(): BaseActivity {
        return mActivity
    }

    override fun getContext(): Context {
        return mActivity
    }

    /**
     * 获取 Application 对象
     */
    open fun getApplication(): Application? {
        activity?.let { return it.application }
        return null
    }

    /**
     * 当前加载对话框是否在显示中
     */
    open fun isShowDialog(): Boolean {
        val activity: BaseActivity = getAttachActivity() ?: return false
        return activity.isShowDialog()
    }

    /**
     * 显示加载对话框
     */
    open fun showDialog() {
        getAttachActivity().showDialog()
    }

    /**
     * 隐藏加载对话框
     */
    open fun hideDialog() {
        getAttachActivity().dismissDialog()
    }

    override fun getBundle(): Bundle? {
        return arguments
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as BaseActivity
    }

    override fun onResume() {
        super.onResume()
        if (!loading) {
            loading = true
            onFragmentResume(true)
            return
        }

        if (this.activity?.lifecycle?.currentState == Lifecycle.State.STARTED) {
            // activity 切换
            onActivityResume()
        } else {
            // fragment 切换
            onFragmentResume(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loading = false
        removeCallbacks()
    }

    override fun onDetach() {
        super.onDetach()
//        mActivity = null
    }

    /**
     * 隐藏键盘
     *
     * @param view
     */
    open fun hideKeyBoard(view: View? = null) {
        if (view.isNotNull()){
            if (KeyboardStatusDetector.isKeyBoardShow(view)) {
                view?.windowToken?.let {
                    mInputMethodManager.hideSoftInputFromWindow(it, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            }
        }else {
            if (mInputMethodManager.isActive.truely() && mActivity.currentFocus.isNotNull()) {
                //拿到view的token 不为空
                mActivity.currentFocus?.windowToken?.let {
                    mInputMethodManager.hideSoftInputFromWindow(it, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            }
        }
    }

    /**
     * 显示键盘
     */
    open fun showKeyBoard(view: View? = null){
        if (view.isNotNull()){
            view?.let {
                mInputMethodManager.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
            }
        }else {
            mActivity.currentFocus?.let {
                mInputMethodManager.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    /**
     * 获取指定ViewModel，仅当前页面生效
     */
    fun <T: ViewModel>getAppointViewModel(viewModel: Class<T>): T {
        return ViewModelProvider(this)[viewModel]
    }


    /**
     * 销毁当前 Fragment 所在的 Activity
     */
    open fun finish() {
        this.activity?.let {
            if (it.isFinishing || it.isDestroyed) {
                return
            }
            it.finish()
        }
    }

    fun startActivity(clazz: Class<*>, vararg data: Pair<String, Any?>){
        val intent = Intent(context, clazz)
        packageData(data, intent)
        startActivity(intent)
    }

    /**
     * when (it.resultCode) {
     *  Constants.CUSTOMER_SELECT_CODE -> { it.data }
     * }
     */
    private val startActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onForResult?.invoke(it)
        }

    /**
     * startActivityForResult
     *
     * data class CommonItemBean(var id: Int?, var title: String?, var link: String?, var collect: Boolean) : Serializable
     *
     * startActivity(X5WebActivity::class.java,
     * X5WebViewModel.FLAG_BEAN to CommonItemBean(mId, mTitle.get(), mLink, false),
     * X5WebViewModel.FLAG_SHOW_COLLECT_ICON to false)
     *
     * setResult(Constants.CUSTOMER_SELECT_CODE, Intent().apply {
     *  putExtra("customerId",it.customerId)
     *  putExtra("customerName",it.customerName)
     * })
     */
    fun startForResult(clazz: Class<*>, vararg data: Pair<String, Any?>) {
        val intent = Intent(context, clazz)
        packageData(data, intent)
        startActivityLauncher.launch(intent)
    }

    private fun packageData(data: Array<out Pair<String, Any?>>, intent: Intent) {
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
    }

}