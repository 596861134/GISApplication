package com.gis.common.view.banner.util;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.gis.common.log.LogHelper;

public class BannerLifecycleObserverAdapter implements LifecycleObserver {
    private final BannerLifecycleObserver mObserver;
    private final LifecycleOwner mLifecycleOwner;

    public BannerLifecycleObserverAdapter(LifecycleOwner lifecycleOwner, BannerLifecycleObserver observer) {
        mLifecycleOwner = lifecycleOwner;
        mObserver = observer;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        LogHelper.i("onStart");
        mObserver.onStart(mLifecycleOwner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        LogHelper.i("onStop");
        mObserver.onStop(mLifecycleOwner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        LogHelper.i("onDestroy");
        mObserver.onDestroy(mLifecycleOwner);
    }

}