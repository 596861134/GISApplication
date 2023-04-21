package com.gis.common.aop

import com.gis.common.BuildConfig

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/06
 *    desc   : Debug 日志注解
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR)
annotation class Log constructor(val value: String = BuildConfig.LOG_TAG)