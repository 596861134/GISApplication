package com.gis.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author czf
 * @date 2020/6/4
 */

/**
 * 不关心data数据
 */
data class ObjectAnyBean(
        @SerializedName("data")
        var data: Any
):BaseBean(),Serializable

data class ObjectBooleanBean(
        @SerializedName("data")
        var data: Boolean
):BaseBean(),Serializable

data class ObjectStringBean(
        @SerializedName("data")
        var data: String?
):BaseBean(),Serializable

data class ObjectIntBean(
        @SerializedName("data")
        var data: Int
):BaseBean(),Serializable


/**
 * BaseBean<T>
 */
data class ObjectBaseBean<T>(
        val data: T
):BaseBean(),Serializable

data class ObjectBaseBeanWithNull<T>(
        val data: T?
):BaseBean(),Serializable

/**
 * BaseBean<T>
 */
data class ObjectBaseListBean<T>(
        val data: List<T>
):BaseBean(),Serializable

data class ObjectBaseListBeanWithNull<T>(
        val data: List<T>?
):BaseBean(),Serializable


/**
 * 分页相关
 */
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
        val records:List<T>,
        var isLoadMoreEnabled:Boolean =false,
):Serializable


open class BaseBean(
        var code: Int? = null,
        var message: String? = null,
        var msg: String? = null,
) : Serializable




