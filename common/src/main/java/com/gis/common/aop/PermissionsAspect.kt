package com.gis.common.aop

import android.app.Activity
import com.gis.common.extension.log
import com.gis.common.extension.showToast
import com.gis.common.manager.AppActivityManager
import com.hjq.demo.aop.Permissions
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/06
 *    desc   : 权限申请切面
 */
@Suppress("unused")
@Aspect
class PermissionsAspect {

    /**
     * 方法切入点
     */
    @Pointcut("execution(@com.hjq.demo.aop.Permissions * *(..))")
    fun method() {}

    /**
     * 在连接点进行方法替换
     */
    @Around("method() && @annotation(permissions)")
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint, permissions: Permissions) {
        var activity: Activity? = null

        // 方法参数值集合
        val parameterValues: Array<Any?> = joinPoint.args
        for (arg: Any? in parameterValues) {
            if (arg !is Activity) {
                continue
            }
            activity = arg
            break
        }
        if ((activity == null) || activity.isFinishing || activity.isDestroyed) {
            activity = AppActivityManager.getInstance().getTopActivity()
        }
        if ((activity == null) || activity.isFinishing || activity.isDestroyed) {
            "The activity has been destroyed and permission requests cannot be made".log()
            return
        }
        requestPermissions(joinPoint, activity, permissions.value)
    }

    private fun requestPermissions(joinPoint: ProceedingJoinPoint, activity: Activity, permissions: Array<out String>) {
        XXPermissions.with(activity)
            .permission(*permissions)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String>, all: Boolean) {
                    if (!all) {
                        "获取权限失败，请手动授予权限".showToast()
                        return
                    }
                    // 获得权限，执行原方法
                    joinPoint.proceed()
                }

                override fun onDenied(permissions: List<String>, never: Boolean) {
                    permissions.let {
                        "权限请求失败".showToast()
                    }
                }
            })
    }
}