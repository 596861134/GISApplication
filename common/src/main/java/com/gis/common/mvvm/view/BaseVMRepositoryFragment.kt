package com.gis.common.mvvm.view

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.gis.common.mvvm.interfaces.ViewState
import com.gis.common.mvvm.viewmodel.BaseRepositoryViewModel
import com.gis.common.mvvm.viewmodel.BaseViewModelFactory

/**
 * Created by chengzf on 2021/5/13.
 */
abstract class BaseVMRepositoryFragment<VM: BaseRepositoryViewModel<*>, T:ViewDataBinding>(@LayoutRes private val layoutId: Int):
    BaseFragment(), ViewState {

    lateinit var mRealVM: VM
    lateinit var mBinding: T
    abstract fun getViewModel(app: Application): VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        beforeSetView()
        val vm = getViewModel(mActivity.application)
        mRealVM = ViewModelProvider(this, BaseViewModelFactory(mActivity.application,vm))[vm::class.java]
        mBinding = DataBindingUtil.inflate(inflater,layoutId,container,false)
        mBinding.lifecycleOwner = this
//        mBinding.setVariable(mRealVM.id(),mRealVM)
        mBinding.executePendingBindings()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewInit()
        mRealVM.onModelBind()
        onEvent()
        lazyFetchDataIfPrepared()
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