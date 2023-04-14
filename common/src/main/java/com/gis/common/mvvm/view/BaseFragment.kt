package com.gis.common.mvvm.view

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gis.common.dialog.LoadingDialog

/**
 * Created by chengzf on 2021/5/13.
 */
open class BaseFragment:Fragment() {

    lateinit var mActivity: BaseActivity
    private val mDialog by lazy { LoadingDialog(mActivity) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as BaseActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

    open fun hideKeyBoard() {
        val imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager //得到InputMethodManager的实例
        if (imm.isActive && mActivity.currentFocus != null) {
            //拿到view的token 不为空
            if (mActivity.currentFocus?.windowToken != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(
                    mActivity.currentFocus?.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }

    /**
     * 获取指定ViewModel
     */
    fun <T: ViewModel>getAppointViewModel(viewModel: Class<T>): T {
        return ViewModelProvider(this)[viewModel]
    }


}