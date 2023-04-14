package com.gis.common.mvvm.viewmodel

import android.app.Application
import com.gis.common.mvvm.interfaces.BaseRepository

/**
 * Created by chengzf on 2021/11/17.
 */
abstract class BaseRepositoryViewModel<T: BaseRepository>(app:Application, val mRepo:T): BaseLayoutViewModel(app) {

    fun dialogState(isShow: Boolean){
        isDialogShow.value = isShow
    }
}