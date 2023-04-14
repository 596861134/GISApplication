package com.gis.common.mvvm.view

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.gis.common.mvvm.interfaces.ViewState
import com.gis.common.mvvm.viewmodel.BaseLayoutViewModel

/**
 * Created by chengzf on 2021/5/13.
 */
open class BaseViewModelActivity<VM: BaseLayoutViewModel, T:ViewDataBinding>(@LayoutRes private val layoutId:Int, private val clazz:Class<VM>):
    BaseActivity(), ViewState {

    lateinit var mRealVM: VM
    lateinit var mBinding: T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beforeSetView()
        mRealVM = ViewModelProvider(this)[clazz]
        mBinding = DataBindingUtil.setContentView(this, layoutId)
//        mBinding.setVariable(mRealVM.id(), mRealVM)
        mBinding.lifecycleOwner = this
        mBinding.executePendingBindings()
        onViewInit()
        mRealVM.setBundle(intent.extras ?: Bundle())
        mRealVM.onModelBind()
        onEvent()
    }

    override fun beforeSetView() {
    }

    override fun onViewInit() {
    }

    override fun onEvent() {
        mRealVM.dialogState(this)
        mRealVM.finish(this)
    }
}