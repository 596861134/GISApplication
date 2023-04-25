package com.czf.gis.crash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.czf.gis.MainActivity
import com.czf.gis.SplashActivity
import com.gis.common.extension.showToast
import com.gis.common.mvvm.view.BaseActivity

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2020/11/29
 *    desc   : 重启应用
 */
class RestartActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RestartActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

        fun restart(context: Context) {
            val intent: Intent = if (true) {
                // 如果是未登录的情况下跳转到闪屏页
                Intent(context, SplashActivity::class.java)
            } else {
                // 如果是已登录的情况下跳转到首页
                Intent(context, MainActivity::class.java)
            }
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restart(this)
        finish()
    }

}