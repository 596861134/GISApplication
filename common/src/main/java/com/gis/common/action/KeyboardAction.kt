package com.gis.common.action

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2020/03/08
 *    desc   : 软键盘相关意图
 */
interface KeyboardAction {

    private fun getInputMethodManager(view: View?): InputMethodManager? {
        view?.apply {
            return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }
        return null
    }
    /**
     * 显示软键盘，需要先 requestFocus 获取焦点，如果是在 Activity Create，那么需要延迟一段时间
     */
    fun showKeyboard(view: View?) {
        view?.apply {
            getInputMethodManager(this)?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(view: View?) {
        view?.apply {
            getInputMethodManager(this)?.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * 切换软键盘
     */
    fun toggleSoftInput(view: View?) {
        view?.apply {
            getInputMethodManager(this)?.toggleSoftInput(0, 0)
        }
    }

    /**
     * 去除EditText焦点
     */
    fun clearEditFocus(view: View?){
        hideKeyboard(view)
        view?.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            clearFocus()
        }
    }

    /**
     * 重新获取焦点，通过onClick点击事件调用
     */
    fun  requestEditFocus(view: View?){
        view?.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
            findFocus()
            val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

}