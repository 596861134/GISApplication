package com.gis.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * @author czf
 * @date 2020/6/4
 */

/**
 * 不关心data数据
 */
@Parcelize
data class ObjectAnyBean(
        @SerializedName("data")
        var data: @RawValue Any
):BaseBean(),Parcelable

@Parcelize
data class ObjectBooleanBean(
        @SerializedName("data")
        var data: Boolean
):BaseBean(),Parcelable

@Parcelize
data class ObjectStringBean(
        @SerializedName("data")
        var data: String?
):BaseBean(),Parcelable

@Parcelize
data class ObjectIntBean(
        @SerializedName("data")
        var data: Int
):BaseBean(),Parcelable


/**
 * BaseBean<T>
 */
@Parcelize
data class ObjectBaseBean<T>(
        val data: @RawValue T
):BaseBean(),Parcelable

@Parcelize
data class ObjectBaseBeanWithNull<T>(
        val data: @RawValue T?
):BaseBean(),Parcelable

/**
 * BaseBean<T>
 */
@Parcelize
data class ObjectBaseListBean<T>(
        val data: @RawValue List<T>
):BaseBean(),Parcelable

@Parcelize
data class ObjectBaseListBeanWithNull<T>(
        val data: @RawValue List<T>?
):BaseBean(),Parcelable

/**
 * 分页相关
 */
@Parcelize
data class CommonBeanList<T>(
        // 当前页
        val current:Int,
        // 总页数
        val pages:Int,
        // 每页多少条
        val size:Int,
        // 总记录数
        val total:Int,
        // 是否能加载更多
        val records: @RawValue List<T>,
        var isLoadMoreEnabled:Boolean =false,
):Parcelable

@Parcelize
open class BaseBean(
        var code: Int? = null,
        var message: String? = null,
        var msg: String? = null,
) : Parcelable


@Parcelize
data class MyDataClass(val name: String, val age: Int): Parcelable

