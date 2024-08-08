package com.czf.gis

import com.czf.gis.databinding.ActivityScendBinding
import com.gis.common.manager.FragmentLifecycle
import com.gis.common.mvvm.view.BaseViewModelActivity
import com.gis.common.mvvm.viewmodel.BaseLayoutViewModel

/**
 * Created by chengzf on 2023/4/27.
 */
class ScendActivity:BaseViewModelActivity<BaseLayoutViewModel, ActivityScendBinding>(ActivityScendBinding::inflate, BaseLayoutViewModel::class.java) {

    private val mSmallSaleTaskFragment by lazy { MainFragment.newInstance("TASK_LABEL") }

    private val mFragmentManager:FragmentLifecycle by lazy { FragmentLifecycle() }

    override fun onViewInit() {
        super.onViewInit()
        supportFragmentManager.apply {
            beginTransaction().add(mBinding.frameLayout.id, mSmallSaleTaskFragment).commitNowAllowingStateLoss()
            registerFragmentLifecycleCallbacks(mFragmentManager, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(mFragmentManager)
    }
}