package com.czf.gis

import android.app.Application
import com.chad.library.adapter.base.loadState.LoadState
import com.gis.common.extension.emptyBody
import com.gis.common.mvvm.livedata.UnPeekLiveData
import com.gis.common.mvvm.viewmodel.BaseRepositoryViewModel
import com.gis.network.ObjectAnyBean
import com.gis.network.util.launch
import com.gis.network.util.response

/**
 * Created by chengzf on 2023/4/20.
 */
class MainViewModel(app:Application):BaseRepositoryViewModel<MainRepository>(app, MainRepository()) {


    private val mUpdateBusinessLiveData: UnPeekLiveData<ObjectAnyBean> by lazy { UnPeekLiveData() }
    fun getUpdateBusinessLiveData(): UnPeekLiveData<ObjectAnyBean> {
        return mUpdateBusinessLiveData
    }

    fun userLogout(){
        launch(true, finish = {
            mUpdateBusinessLiveData.value = ObjectAnyBean(LoadState.None)
        }){
            response(mRepo.userLogout(emptyBody)){
                mUpdateBusinessLiveData.value = this
            }
        }
    }
}