package com.gis.common.extension

import androidx.annotation.StringRes
import com.hjq.toast.Toaster

fun toast(string: String){
    Toaster.showShort(string)
}

fun toast(@StringRes strId:Int){
    Toaster.showShort(strId)
}
