package com.gis.common.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.gis.common.databinding.WidgetStatusLayoutBinding

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/04/18
 *    desc   : 状态布局（网络错误，异常错误，空数据）
 */
class StatusLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val mContext = context

    /** 主布局 */
    private var mainLayout: ViewGroup

    /** 提示图标 */
    private var lottieView: LottieAnimationView

    /** 提示文本 */
    private var textView: TextView

    /** 重试按钮 */
    private var retryView: TextView

    /** 重试监听 */
    private var listener: OnRetryListener? = null

    /**
     * 点击事件包装类
     */
    private val clickWrapper: OnClickListener by lazy { OnClickListener { listener?.onRetry(this@StatusLayout) } }

    private val mBinging by lazy { getViewBinding() }
    private fun getViewBinding(): WidgetStatusLayoutBinding {
        return WidgetStatusLayoutBinding.inflate(LayoutInflater.from(mContext), this, false)
    }

    /**
     * 初始化提示的布局
     */
    init {
        mainLayout = mBinging.root
        lottieView = mBinging.ivStatusIcon
        textView = mBinging.ivStatusText
        retryView = mBinging.ivStatusRetry
        if (mainLayout.background == null) {
            // 默认使用 windowBackground 作为背景
            val typedArray: TypedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.windowBackground))
            mainLayout.background = typedArray.getDrawable(0)
            mainLayout.isClickable = true
            typedArray.recycle()
        }
        retryView.setOnClickListener(clickWrapper)
        addView(mainLayout)
    }

    /**
     * 显示
     */
    fun show() {
        if (isShow()) {
            return
        }
        retryView.visibility = if (listener == null) INVISIBLE else VISIBLE
        // 显示布局
        mainLayout.visibility = VISIBLE
    }

    /**
     * 隐藏
     */
    fun hide() {
        if (!isShow()) {
            return
        }
        //隐藏布局
        mainLayout.visibility = INVISIBLE
    }

    /**
     * 是否显示了
     */
    fun isShow(): Boolean {
        return mainLayout.visibility == VISIBLE
    }

    /**
     * 设置提示图标，请在show方法之后调用
     */
    fun setIcon(@DrawableRes id: Int) {
        setIcon(ContextCompat.getDrawable(mContext, id))
    }

    fun setIcon(drawable: Drawable?) {
        lottieView.apply {
            if (isAnimating) {
                cancelAnimation()
            }
            setImageDrawable(drawable)
        }
    }

    /**
     * 设置提示动画
     */
    fun setAnimResource(@RawRes id: Int) {
        lottieView.apply {
            setAnimation(id)
            if (!isAnimating) {
                playAnimation()
            }
        }
    }

    /**
     * 设置提示文本，请在show方法之后调用
     */
    fun setHint(@StringRes id: Int) {
        setHint(resources.getString(id))
    }

    fun setHint(text: CharSequence?) {
        textView.text = text ?: ""
    }

    /**
     * 设置重试监听器
     */
    fun setOnRetryListener(listener: OnRetryListener?) {
        this.listener = listener
        if (isShow()) {
            retryView.visibility = if (this.listener == null) INVISIBLE else VISIBLE
        }
    }

    /**
     * 重试监听器
     */
    interface OnRetryListener {

        /**
         * 点击了重试
         */
        fun onRetry(layout: StatusLayout)
    }
}