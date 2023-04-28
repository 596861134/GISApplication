package com.gis.common.manager

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.gis.common.extension.LogEnum
import com.gis.common.extension.log

/**
 * Created by chengzf on 2023/4/27.
 *
 * DefaultLifecycleObserver, LifecycleEventObserver用法示例
 * DefaultLifecycleObserver和LifecycleEventObserver二选一实现即可
 *
 * 调用：lifecycle.addObserver(LifecycleManager())
 */
class LifecycleManager : DefaultLifecycleObserver, LifecycleEventObserver {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        String.format("LifecycleManager %s - onCreate", owner.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        String.format("LifecycleManager %s - onResume", owner.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        String.format("LifecycleManager %s - onStart", owner.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        String.format("LifecycleManager %s - onPause", owner.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        String.format("LifecycleManager %s - onStop", owner.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        String.format("LifecycleManager %s - onDestroy", owner.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        String.format("LifecycleManager-onStateChanged %s - %s", source.javaClass.simpleName, event.toString()).log(LogEnum.INFO)
    }
}