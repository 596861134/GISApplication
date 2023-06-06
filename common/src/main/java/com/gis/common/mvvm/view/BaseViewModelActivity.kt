package com.gis.common.mvvm.view

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.gis.common.mvvm.interfaces.ViewState
import com.gis.common.mvvm.viewmodel.BaseLayoutViewModel

/**
 * Created by chengzf on 2021/5/13.
 */
abstract class BaseViewModelActivity<VM: BaseLayoutViewModel, T: ViewBinding>(private val clazz:Class<VM>):
    BaseActivity(), ViewState {

    lateinit var mRealVM: VM
    lateinit var mBinding: T

    /**
     * ActivityMainBinding.inflate(layoutInflater)
     */
    abstract fun getLayoutId(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beforeSetView()
        mRealVM = ViewModelProvider(this)[clazz]
        mBinding = getLayoutId()
        setContentView(mBinding.root)
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