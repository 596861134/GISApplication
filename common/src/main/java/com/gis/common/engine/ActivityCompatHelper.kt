package com.gis.common.engine

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity
import com.gis.common.extension.isNull

/**
 * Created by chengzf on 2023/5/12.
 */
object ActivityCompatHelper {

    private const val MIN_FRAGMENT_COUNT = 1

    fun isDestroy(activity: Activity?):Boolean{
        return activity?.let {
            it.isFinishing || it.isDestroyed
        }?: kotlin.run {
            return true
        }
    }

    /**
     * 验证Fragment是否已存在
     */
    fun checkFragmentNonExits(activity: FragmentActivity?, fragmentTag:String):Boolean{
        if (isDestroy(activity)){
            return false
        }
        val fragment = activity?.supportFragmentManager?.findFragmentByTag(fragmentTag)
        return fragment.isNull()
    }

    fun assertValidRequest(context: Context):Boolean{
        if (context is Activity){
            val activity = context as Activity
            return !isDestroy(activity)
        }else if (context is ContextWrapper){
            val contextWrapper = context as ContextWrapper
            if (contextWrapper.baseContext is Activity){
                val activity = contextWrapper.baseContext as Activity
                return !isDestroy(activity)
            }
        }
        return true
    }

    /**
     * 验证当前是否是根Fragment
     */
    fun checkRootFragment(activity: FragmentActivity?):Boolean{
        if (isDestroy(activity)) return false
        return activity?.supportFragmentManager?.backStackEntryCount == MIN_FRAGMENT_COUNT
    }
}