package com.gis.common.mvvm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.gis.common.mvvm.interfaces.ViewState
import com.gis.common.mvvm.viewmodel.BaseLayoutViewModel

/**
 * Created by chengzf on 2021/5/13.
 */
abstract class BaseViewModelFragment<VM: BaseLayoutViewModel, T: ViewBinding>(private val clazz:Class<VM>):
    BaseFragment(), ViewState {
    lateinit var mRealVM: VM
    lateinit var mBinding: T

    /**
     * return FragmentMainBinding.inflate(inflater, container, false)
     */
    abstract fun getLayoutId(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        beforeSetView()
        mRealVM = ViewModelProvider(this)[clazz]
        mBinding = getLayoutId(inflater, container)
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