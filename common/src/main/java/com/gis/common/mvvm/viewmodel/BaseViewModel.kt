package com.gis.common.mvvm.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gis.common.mvvm.livedata.UnPeekLiveData
import java.io.Serializable

/**
 * Created by chengzf on 2021/6/28.
 */
open class BaseViewModel(app:Application) :AndroidViewModel(app) {

    // Dialog监听使用MutableLiveData，Base中已自动记录调用次数
    var isDialogShow = MutableLiveData<Boolean>()
    var isFinish = UnPeekLiveData<Boolean>()

    lateinit var mBundle: Bundle

    fun setBundle(bundle: Bundle){
        mBundle = bundle
    }

    open fun onModelBind(){

    }

    fun finish(){
        isFinish.value = true
    }

    fun startActivity(clazz:Class<*>, vararg data:Pair<String, Any?>){
        val application = getApplication<Application>()
        val intent = Intent(application, clazz)
        data.forEach {
            when(it.second){
                is Boolean -> intent.putExtra(it.first, it.second as Boolean)
                is Byte -> intent.putExtra(it.first, it.second as Byte)
                is Short -> intent.putExtra(it.first, it.second as Short)
                is Int -> intent.putExtra(it.first, it.second as Int)
                is Float -> intent.putExtra(it.first, it.second as Float)
                is Double -> intent.putExtra(it.first, it.second as Double)
                is Long -> intent.putExtra(it.first, it.second as Long)
                is Char -> intent.putExtra(it.first, it.second as Char)
                is String -> intent.putExtra(it.first, it.second as String)
                is Serializable -> intent.putExtra(it.first, it.second as Serializable)
                is Parcelable -> intent.putExtra(it.first, it.second as Parcelable)
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }
}