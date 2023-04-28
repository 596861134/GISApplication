package com.gis.common.manager

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * Created by chengzf on 2023/4/27.
 *
 * LifecycleRegistry用法示例，自定义LifecycleOwner管理生命周期
 * 通过监听父类的生命周期来管理自己子类的生命周期
 *
 * 调用：lifecycle.addObserver(LifecycleManager())
 */
class LifecycleOwnerManager :LifecycleOwner, LifecycleEventObserver {

    private val mLifeDemo:LifecycleManager by lazy { LifecycleManager() }

    private val mLifecycleRegistry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle = mLifecycleRegistry

    init {
        lifecycle.addObserver(mLifeDemo)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        mLifecycleRegistry.handleLifecycleEvent(event)
    }

}