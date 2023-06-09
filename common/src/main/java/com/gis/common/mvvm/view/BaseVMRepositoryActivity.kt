package com.gis.common.mvvm.view

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.gis.common.mvvm.interfaces.ViewState
import com.gis.common.mvvm.viewmodel.BaseRepositoryViewModel
import com.gis.common.mvvm.viewmodel.BaseViewModelFactory

/**
 * Created by chengzf on 2021/5/13.
 */
abstract class BaseVMRepositoryActivity<VM: BaseRepositoryViewModel<*>, T: ViewBinding>():
    BaseActivity(), ViewState {

    lateinit var mRealVM:VM
    lateinit var mBinding: T
    abstract fun getViewModel(app: Application): VM

    /**
     * ActivityMainBinding.inflate(layoutInflater)
     */
    abstract fun getLayoutId():T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beforeSetView()
        val vm = getViewModel(application)
        mRealVM = ViewModelProvider(this, BaseViewModelFactory(application,vm))[vm::class.java]
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