package com.gis.network.util

import android.accounts.NetworkErrorException
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import com.gis.common.extension.isNotNull
import com.gis.common.extension.isNull
import com.gis.common.extension.showToast
import com.gis.common.mvvm.viewmodel.BaseViewModel
import com.gis.network.BaseBean
import com.gis.network.NetConstant
import com.google.gson.stream.MalformedJsonException
import kotlinx.coroutines.launch
import java.io.EOFException
import java.io.FileNotFoundException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.net.ssl.SSLException

/**
 * @author czf
 * @date 2020/10/27
 */
fun BaseViewModel.launch(showDialog: Boolean = true, finish: (suspend () -> Unit)? = null, error: (suspend () -> Unit)? = null, success: suspend () -> Unit) {
    viewModelScope.launch {
        try {
            success.invoke()
            dialogState(showDialog, true)
        } catch (e: Exception) {
            when(e){
                is FileNotFoundException ->{
                    fileNotFoundError()
                }
                is MalformedJsonException,
                is NetworkErrorException,
                is UnknownHostException,
                is ConnectException,
                is SocketException,
                is EOFException,
                is SSLException,
                is IOException -> {
                    netError()
                }
                is SocketTimeoutException,
                is TimeoutException -> {
                    "连接超时".showToast()
                }
                else -> {
                    e.netError()
                }
            }
            if (error.isNotNull()) {
                error?.invoke()
            }
            e.printStackTrace()
        } finally {
            dialogState(showDialog, false)
            finish?.invoke()
        }
    }
}

private fun BaseViewModel.dialogState(showDialog: Boolean, state: Boolean) {
    if (showDialog) {
        isDialogShow.value = state
    }

}

fun <T : BaseBean> response(bean: T, error: (T.() -> Unit)? = null, result: T.() -> Unit) {
    when (bean.code) {
        NetConstant.SUCCESS -> result.invoke(bean)
        else -> {
            if (error.isNull()){
                // 适配不自己处理error的情况
                bean.code?.showToast()
            }
            error?.invoke(bean)
        }
    }
}

fun loadSuccess() {
    WanExecutors.mDiskIO.execute {
        if (NetworkUtils.isAvailable()) {
            "加载成功...".showToast()
        }
    }
}

fun noMoreData() {
    "没有更多数据了...".showToast()
}

fun loginFirst() {
    "请先登录...".showToast()
}

fun Throwable.netError() {
    // 网络请求被动取消不弹
    if (!message.equals("Job was cancelled", true)){
        "$message".showToast()
    }
}

fun netError() {
    "网络错误...".showToast()
}

fun fileNotFoundError() {
    "文件错误".showToast()
}







