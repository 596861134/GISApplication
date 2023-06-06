package com.gis.common.widget

import android.app.Activity
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.gis.common.databinding.WidgetSplashViewBinding
import com.gis.common.extension.doubleClick
import com.gis.common.extension.isNull
import com.gis.common.manager.AppActivityManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by chengzf on 2023/6/6.
 * 广告页
 */
class SplashADView : AppActivityManager.ApplicationLifecycleCallback {

    companion object {

        private val mSplashADView: SplashADView by lazy { SplashADView() }

        @JvmStatic
        fun getInstance(): SplashADView {
            return mSplashADView
        }

        // 广告显示时间
        const val mADDuration: Long = (1000 * 4).toLong()

        // 广告显示间隔时间, 间隔内不出现广告
        const val mADInterval: Long = (1000 * 10).toLong()

    }

    // 进入过后台
    private var mBackToForeground: Boolean = false

    // 上次显示广告的时间
    private var mLastADTime: Long = 0

    // 倒计时
    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    // 广告页的view
    private var mAdView: View? = null
    private var mBinding: WidgetSplashViewBinding? = null
    // 资源ID
    private var mResourceID: Int = 0

    /**
     * 注册广告
     */
    fun register(resource: Int) {
        mResourceID = resource
        AppActivityManager.getInstance().registerApplicationLifecycleCallback(this)
    }

    /**
     * 初始化广告UI
     */
    private fun initAdView(activity: Activity) {
        mBinding = WidgetSplashViewBinding.inflate(
            LayoutInflater.from(activity),
            FrameLayout(activity),
            false
        )
        mAdView = mBinding?.root
        mBinding?.splashImg?.setBackgroundResource(mResourceID)
        mBinding?.splashText?.setOnClickListener {
            if (it.doubleClick()) return@setOnClickListener
            if (AppActivityManager.getInstance().isForeground()){
                activity.windowManager.removeViewImmediate(mAdView)
            }
            stopTimer()
        }
    }

    /**
     * 创建广告
     */
    private fun createADView(activity: Activity) {
        val params = WindowManager.LayoutParams()
        params.x = 0
        params.y = 0
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        // 使 Window 全屏
        params.flags = (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        params.format = PixelFormat.TRANSLUCENT
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // 在 Android 11 以及之后的版本中，需要使用 TYPE_APPLICATION_OVERLAY 类型的窗口来展示悬浮视图
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }*/
        if (mAdView.isNull()) {
            initAdView(activity)
        }
        activity.windowManager.addView(mAdView, params)
    }

    /**
     * 显示广告
     */
    private fun showAD(activity: Activity) {
        createADView(activity)
        stopTimer()
        startTimer(activity)
    }

    /**
     * 启动倒计时
     */
    private fun startTimer(activity: Activity) {
        mCompositeDisposable.add(
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver(activity))
        )
    }

    /**
     * 处理倒计时逻辑
     */
    private fun getObserver(activity: Activity): DisposableObserver<Long> {
        return object : DisposableObserver<Long>() {
            override fun onNext(@NonNull o: Long) {
                val ss = mADDuration - (o.toInt() * 1000)
                mBinding?.splashText?.text = "跳过 ${ss / 1000}s"
                if (ss == 0L && AppActivityManager.getInstance().isForeground()) {
                    activity.windowManager.removeViewImmediate(mAdView)
                }
            }

            override fun onError(@NonNull e: Throwable) {}
            override fun onComplete() {
            }
        }
    }

    /**
     * 停止倒计时
     */
    private fun stopTimer() {
        mCompositeDisposable.clear()
    }

    /**
     * 判断两次时间是否大于规定的间隔
     */
    private fun canShowAD(): Boolean {
        // 当前时间 - 进入后台的时间 = 在后台停留的时间
        return System.currentTimeMillis() - mLastADTime > mADInterval
    }

    /**
     * 第一个 Activity 创建了
     */
    override fun onApplicationCreate(activity: Activity) {
        initAdView(activity)
    }

    /**
     * 最后一个 Activity 销毁了
     */
    override fun onApplicationDestroy(activity: Activity) {
        AppActivityManager.getInstance().unregisterApplicationLifecycleCallback(this)
        mBackToForeground = false
        stopTimer()
    }

    /**
     * 应用从前台进入到后台
     */
    override fun onApplicationBackground(activity: Activity) {
        mBackToForeground = true
        mLastADTime = System.currentTimeMillis()
    }

    /**
     * 应用从后台进入到前台
     */
    override fun onApplicationForeground(activity: Activity) {
        if (mBackToForeground && canShowAD()) {
            mBackToForeground = !mBackToForeground
            showAD(activity)
        }
    }
}