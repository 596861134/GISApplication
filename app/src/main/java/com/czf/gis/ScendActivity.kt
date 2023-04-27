package com.czf.gis

import com.czf.gis.databinding.ActivityScendBinding
import com.gis.common.mvvm.view.BaseViewModelActivity
import com.gis.common.mvvm.viewmodel.BaseLayoutViewModel

/**
 * Created by chengzf on 2023/4/27.
 */
class ScendActivity:BaseViewModelActivity<BaseLayoutViewModel, ActivityScendBinding>(BaseLayoutViewModel::class.java) {

    private val mSmallSaleTaskFragment by lazy { MainFragment.newInstance("TASK_LABEL") }

    override fun getViewBinding(): ActivityScendBinding {
        return ActivityScendBinding.inflate(layoutInflater)
    }

    override fun onViewInit() {
        super.onViewInit()
        supportFragmentManager.beginTransaction()
            .add(mBinding.frameLayout.id, mSmallSaleTaskFragment)
            .commitNowAllowingStateLoss()
    }
}