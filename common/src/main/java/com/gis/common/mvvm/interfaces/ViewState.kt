package com.gis.common.mvvm.interfaces

import com.gis.common.mvvm.view.BaseActivity
import com.gis.common.mvvm.viewmodel.BaseViewModel

/**
 * @author czf
 * @date 2020/10/22
 */
interface ViewState {

    fun beforeSetView()

    fun onViewInit()

    fun onEvent()

    fun BaseViewModel.dialogState(baseActivity: BaseActivity) {
        isDialogShow.observe(baseActivity) {
            if (it) baseActivity.showDialog() else baseActivity.dismissDialog()
        }
    }

    fun BaseViewModel.finish(baseActivity: BaseActivity) {
        isFinish.observe(baseActivity) {
            if (it) {
                baseActivity.finish()
                isFinish.value = false
            }
        }
    }

}