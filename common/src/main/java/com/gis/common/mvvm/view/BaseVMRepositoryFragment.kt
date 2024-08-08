package com.gis.common.mvvm.view

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.gis.common.mvvm.interfaces.ViewState
import com.gis.common.mvvm.viewmodel.BaseRepositoryViewModel
import com.gis.common.mvvm.viewmodel.BaseViewModelFactory

/**
 * Created by chengzf on 2021/5/13.
 */
abstract class BaseVMRepositoryFragment<VM : BaseRepositoryViewModel<*>, T : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T,
    private val ownerActivity: Boolean = false) :
    BaseFragment(), ViewState {

    lateinit var mRealVM: VM
    lateinit var mBinding: T
    abstract fun getViewModel(app: Application): VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        beforeSetView()
        val vm = getViewModel(mActivity.application)
        mRealVM = ViewModelProvider(if(ownerActivity) mActivity else this, BaseViewModelFactory(mActivity.application, vm))[vm::class.java]
        mBinding = bindingInflater(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewInit()
        mRealVM.onModelBind()
        onEvent()
    }

    override fun beforeSetView() {
    }

    override fun onViewInit() {
    }

    override fun onEvent() {
        mRealVM.dialogState(mActivity)
        mRealVM.finish(mActivity)
    }


}