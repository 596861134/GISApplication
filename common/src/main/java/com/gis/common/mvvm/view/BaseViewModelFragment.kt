package com.gis.common.mvvm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.gis.common.mvvm.interfaces.ViewState
import com.gis.common.mvvm.viewmodel.BaseLayoutViewModel

/**
 * Created by chengzf on 2021/5/13.
 */
open class BaseViewModelFragment<VM: BaseLayoutViewModel, T:ViewDataBinding>(@LayoutRes private val layoutId:Int, private val clazz:Class<VM>):
    BaseFragment(), ViewState {
    lateinit var mRealVM: VM
    lateinit var mBinding: T
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        beforeSetView()
        mRealVM = ViewModelProvider(this)[clazz]
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