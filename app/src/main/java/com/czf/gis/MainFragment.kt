package com.czf.gis

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.czf.gis.databinding.FragmentMainBinding
import com.gis.common.manager.LifecycleManager
import com.gis.common.mvvm.view.BaseViewModelFragment
import com.gis.common.mvvm.viewmodel.BaseLayoutViewModel

/**
 * Created by chengzf on 2023/4/27.
 */
class MainFragment:BaseViewModelFragment<BaseLayoutViewModel, FragmentMainBinding>(BaseLayoutViewModel::class.java) {

    private val mLabel:String by lazy { arguments?.getString("viewSource") ?: "" }


    private val mLifecycleManager: LifecycleManager by lazy { LifecycleManager() }

    companion object{

        @JvmStatic
        fun newInstance(viewSource: String) = MainFragment().apply {
            val bundle = Bundle().apply {
                putString("viewSource", viewSource)
            }
            arguments = bundle
        }
    }

    override fun getLayoutId(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onViewInit() {
        super.onViewInit()
        lifecycle.addObserver(mLifecycleManager)
        mActivity.getStatusBarConfig().statusBarDarkFont(false).init()
    }

    override fun onEvent() {
        super.onEvent()
//        mBinding.tvView.text = mLabel
        lifecycle.addObserver(mBinding.clockView)

        /**
         * 设置桌面壁纸
         */
        mBinding.btnClick.setOnClickListener {
            val intent = Intent().apply {
                action = WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
                // 在Android 11后，Google限制了访问锁屏壁纸的权限，因此无法像更早的版本中那样直接使用
                // WallpaperManager.ACTION_SET_LOCK_SCREEN 来设置锁屏壁纸。
                // 目前在Android 11及更高版本中，只能支持设置桌面壁纸，而无法直接设置锁屏壁纸
//                action = WallpaperManager.ACTION_SET_LOCK_SCREEN
                putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(mActivity, TextClockWallpaperService::class.java))
            }
            startActivity(intent)
        }

        /**
         * 还原桌面壁纸
         */
        mBinding.btnReset.setOnClickListener {
            WallpaperManager.getInstance(mActivity).clear()
        }
    }
}