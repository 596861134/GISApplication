package com.gis.common.extension

import android.view.inputmethod.InputMethodManager
import android.widget.EditText


/**
 * 获取输入的double值
 * 针对获取输入框输入值时，最后一位为.的情况
 */
fun EditText.getEditNumber():Double{
    var outPut = 0.0
    var edPrice = this.text.toString().trim()
    if (edPrice.isNotEmpty()){
        val substring = edPrice.substring(edPrice.length - 1)
        if (substring=="."){
            // 最后一位是. 自动删除
            edPrice = edPrice.substring(0, edPrice.indexOf("."))
        }

        outPut = try {
            if (edPrice.toDouble() <= 0){
                0.0
            }else {
                edPrice.toDouble()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }
    return outPut
}

/**
 * 输入的double值转String
 * 针对输入框失去焦点时，最后一位为.的情况
 */
fun String.strToDouble():Double{
    var outPut = 0.0
    var edPrice = this.trim()
    if (edPrice.isNotEmpty()){
        val substring = edPrice.substring(edPrice.length - 1)
        if (substring=="."){
            // 最后一位是. 自动删除
            edPrice = edPrice.substring(0, edPrice.indexOf("."))
        }

        outPut = try {
            if (edPrice.toDouble() <= 0){
                0.0
            }else {
                edPrice.toDouble()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }
    return outPut
}

/**
 * 使EditText失去焦点
 */
fun EditText.clearEditFocus(){
    isFocusable = false
    isFocusableInTouchMode = false
    clearFocus()
}

/**
 * 使EditText获得焦点并弹出键盘
 */
fun EditText.requestEditFocus(imm: InputMethodManager){
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
    findFocus()
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    if (text.isNotEmpty()) {
        setSelection(text.length)
    }
}