package com.gis.common.mvvm.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Created by chengzf on 2021/5/13.
 */
open class BaseFragment:Fragment() {

    lateinit var mActivity: BaseActivity
    // 标识fragment视图已经初始化完毕
    private var isViewPrepared:Boolean = false
    // 标识已经触发过懒加载数据
    private var hasFetchData:Boolean = false

    /**
     * 获取绑定的 Activity，防止出现 getActivity 为空
     */
    open fun getAttachActivity(): BaseActivity? {
        return mActivity
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
        getAttachActivity()?.showDialog()
    }

    /**
     * 隐藏加载对话框
     */
    open fun hideDialog() {
        getAttachActivity()?.dismissDialog()
    }

    /**
     * 执行需要懒加载的方法
     */
    open fun lazyInit(){
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as BaseActivity
    }

    fun lazyFetchDataIfPrepared(){
        if (userVisibleHint && !hasFetchData && isViewPrepared){
            hasFetchData = true
            lazyInit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hasFetchData = false
        isViewPrepared = false
    }

    /**
     * 获取指定ViewModel
     */
    fun <T: ViewModel>getAppointViewModel(viewModel: Class<T>): T {
        return ViewModelProvider(this)[viewModel]
    }


}