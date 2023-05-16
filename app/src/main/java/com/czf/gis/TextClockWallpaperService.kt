package com.czf.gis

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.gis.common.extension.log

/**
 * Created by chengzf on 2023/5/12.
 * 动态壁纸服务
 */
class TextClockWallpaperService:WallpaperService() {
    override fun onCreateEngine(): Engine {
        return MyEngine()
    }

    inner class MyEngine:Engine(){
        private val mClockView = TextClockView(this@TextClockWallpaperService.baseContext)

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            "TextClockWallpaperService:onCreate".log()
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            "TextClockWallpaperService:onSurfaceCreated".log()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            "TextClockWallpaperService:onSurfaceChanged".log()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            "TextClockWallpaperService:onVisibilityChanged".log()
            if (visible){
                mClockView.startTimer(surfaceHolder)
            }else {
                mClockView.stopTimer()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            "TextClockWallpaperService:onSurfaceDestroyed".log()
            mClockView.stopTimer()
        }
    }

}