package com.czf.gis

import android.app.Application
import com.gis.common.extension.emptyBody
import com.gis.common.mvvm.viewmodel.BaseRepositoryViewModel
import com.gis.network.util.launch
import com.gis.network.util.response

/**
 * Created by chengzf on 2023/4/20.
 */
class MainViewModel(app:Application):BaseRepositoryViewModel<MainRepository>(app, MainRepository()) {

    fun userLogout(){
        launch(true){
            response(mRepo.userLogout(emptyBody)){

            }
        }

    }
}