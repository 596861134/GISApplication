package com.czf.gis

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Created by chengzf on 2023/5/12.
 */
class TextClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), DefaultLifecycleObserver {

    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    private val mPaint = createPaint()
    private val mHelperPaint = createPaint(color = Color.RED)
    private var mWidth:Float = -1F
    private var mHeight:Float by Delegates.notNull()

    private var mHourR: Float by Delegates.notNull()
    private var mMinuteR: Float by Delegates.notNull()
    private var mSecondR: Float by Delegates.notNull()

    private var mHourDeg: Float by Delegates.notNull()
    private var mMinuteDeg: Float by Delegates.notNull()
    private var mSecondDeg: Float by Delegates.notNull()

    private var mAnimator:ValueAnimator by Delegates.notNull()

    /**
     * 绘制方法的回调
     */
    private var mBlock:(() -> Unit)? = null

    init {
        mAnimator = ValueAnimator.ofFloat(6F, 0F)
        mAnimator.duration = 150
        mAnimator.interpolator = LinearInterpolator()
        doInvalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mWidth = (measuredWidth - paddingLeft - paddingRight).toFloat()
        mHeight = (measuredHeight - paddingTop - paddingBottom).toFloat()

        mHourR = mWidth * 0.143F
        mMinuteR = mWidth * 0.35F
        mSecondR = mWidth * 0.35F
    }

    fun initSize(width:Float, height:Float){
        if (this.mWidth < 0){
            this.mWidth = width
            this.mHeight = height
        }
        mHourR = mWidth * 0.143F
        mMinuteR = mWidth * 0.35F
        mSecondR = mWidth * 0.35F
    }

    private fun stopInvalidate(){
        mAnimator.removeAllUpdateListeners()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        startTimer()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        stopTimer()
    }

    /**
     * 开始绘制
     */
    fun doInvalidate(block:(() -> Unit)? = null){
        this.mBlock = block

        Calendar.getInstance().run {
            val hour = get(Calendar.HOUR)
            val minute = get(Calendar.MINUTE)
            val second = get(Calendar.SECOND)

            mHourDeg = -360 / 12F * (hour - 1)
            mMinuteDeg = -360 / 60F * (minute - 1)
            mSecondDeg = -360 / 60F * (second - 1)

            //记录当前角度，然后让秒圈线性的旋转6°
            val hd = mHourDeg
            val md = mMinuteDeg
            val sd = mSecondDeg

            mAnimator.removeAllUpdateListeners()
            mAnimator.addUpdateListener {
                val av = it.animatedValue as Float

                if (minute == 0 && second == 0){
                    mHourDeg = hd + av * 5//时圈旋转角度是分秒的5倍，线性的旋转30°
                }

                if (second == 0){
                    mMinuteDeg = md + av
                }

                mSecondDeg = sd + av

                if (this@TextClockView.mBlock != null){
                    this@TextClockView.mBlock?.invoke()
                }else {
                    invalidate()
                }
            }
            mAnimator.start()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            it.drawColor(Color.BLACK)
            it.save()
            it.translate(mWidth / 2 , mHeight / 2)

            drawCenterInfo(canvas)
            drawHour(canvas, mHourDeg)
            drawMinute(canvas, mMinuteDeg)
            drawSecond(canvas, mSecondDeg)

            it.drawLine(0F, 0F, mWidth, 0F, mHelperPaint)
            canvas.restore()
        }
    }

    /**
     * 绘制圆中信息
     */
    private fun drawCenterInfo(canvas: Canvas) {
        Calendar.getInstance().run {
            val hour = get(Calendar.HOUR_OF_DAY)
            val minute = get(Calendar.MINUTE)
            val minuteStr = if (minute < 10) "0$minute" else "$minute"

            mPaint.apply {
                textSize = mHourR * 0.4F
                alpha = 255
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("$hour:$minuteStr", 0F, mPaint.getBottomedY(), mPaint)

            //  绘制月份、星期
            val month = (this.get(Calendar.MONTH) + 1).let {
                if (it < 10) "0$it" else "$it"
            }
            val day = this.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = (get(Calendar.DAY_OF_WEEK) - 1).toText()

            mPaint.apply {
                textSize = mHourR * 0.16F
                alpha = 255
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("$month.$day 星期$dayOfWeek", 0F, mPaint.getToppedY(), mPaint)
        }
    }

    /**
     * 绘制小时
     */
    private fun drawHour(canvas: Canvas, degrees: Float) {
        mPaint.textSize = mHourR * 0.16F
        canvas.save()
        canvas.rotate(degrees)

        for (i in 0 until 12){
            canvas.save()
            //从x轴开始旋转，每30°绘制一下「几点」，12次就画完了「时圈」
            val iDeg = 360 / 12F * i
            canvas.rotate(iDeg)

            mPaint.alpha = if (iDeg + degrees == 0F) 255 else (0.6 * 255).toInt()
            mPaint.textAlign = Paint.Align.LEFT
            canvas.drawText("${(i + 1).toText()}点", mHourR, mPaint.getCenteredY(), mPaint)
            canvas.restore()
        }
        canvas.restore()
    }

    /**
     * 绘制分钟
     */
    private fun drawMinute(canvas: Canvas, degrees: Float) {
        mPaint.textSize = mHourR * 0.16F
        canvas.save()
        canvas.rotate(degrees)
        for (i in 0 until 60){
            canvas.save()
            val iDeg = 360 / 60F * i
            canvas.rotate(iDeg)
            mPaint.alpha = if (iDeg + degrees == 0F) 255 else (0.6 * 255).toInt()
            mPaint.textAlign = Paint.Align.RIGHT
            if (i < 59){
                canvas.drawText("${(i + 1).toText()}分", mMinuteR, mPaint.getCenteredY(), mPaint)
            }
            canvas.restore()
        }
        canvas.restore()
    }

    /**
     * 绘制秒
     */
    private fun drawSecond(canvas: Canvas, degrees: Float) {
        mPaint.textSize = mHourR * 0.16F
        canvas.save()
        canvas.rotate(degrees)
        for (i in 0 until 60){
            canvas.save()
            val iDeg = 360 / 60F * i
            canvas.rotate(iDeg)
            mPaint.alpha = if (iDeg + degrees == 0F) 255 else (0.6 * 255).toInt()
            mPaint.textAlign = Paint.Align.LEFT
            if (i < 59){
                canvas.drawText("${(i + 1).toText()}秒", mSecondR, mPaint.getCenteredY(), mPaint)
            }
            canvas.restore()
        }
        canvas.restore()

    }


    /**
     * 创建画笔
     */
    private fun createPaint(colorString: String? = null, color:Int = Color.WHITE):Paint {
        return Paint().apply {
            this.color = if (colorString != null) Color.parseColor(colorString) else color
            this.isAntiAlias = true
            this.style = Paint.Style.FILL
        }
    }

    /**
     * 扩展获取绘制文字时在x轴上 垂直居中的y坐标
     */
    private fun Paint.getCenteredY():Float{
        return this.fontSpacing / 2 - this.fontMetrics.bottom
    }


    /**
     * 扩展获取绘制文字时在x轴上 贴紧x轴的上边缘的y坐标
     */
    private fun Paint.getBottomedY():Float{
        return -this.fontMetrics.bottom
    }

    /**
     * 扩展获取绘制文字时在x轴上 贴近x轴的下边缘的y坐标
     */
    private fun Paint.getToppedY():Float{
        return -this.fontMetrics.ascent
    }

    private fun Int.toText():String{
        var result = ""
        val iArr = "$this".toCharArray().map { it.toString().toInt() }

        if (iArr.size > 1){
            if (iArr[0] != 1){
                result += NUMBER_TEXT_LIST[iArr[0]]
            }
            result += "十"
            if (iArr[1] > 0){
                result += NUMBER_TEXT_LIST[iArr[1]]
            }
        }else {
            result += NUMBER_TEXT_LIST[iArr[0]]
        }
        return result
    }

    fun startTimer(surfaceHolder: SurfaceHolder? = null){
        mCompositeDisposable.add(
            Observable.interval(0, 1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(getObserver(surfaceHolder))
        )
    }

    fun stopTimer(){
        stopInvalidate()
        mCompositeDisposable.clear()
    }

    private fun getObserver(surfaceHolder: SurfaceHolder? = null): DisposableObserver<Any> {
        return object : DisposableObserver<Any>() {
            override fun onNext(@NonNull o: Any) {
                Log.d("TAG", "第 $o 次轮询")
                if (surfaceHolder != null){
                    doInvalidate{
                        // 用于主题背景绘制
                        surfaceHolder.lockCanvas()?.let {
                            initSize(it.width.toFloat(), it.height.toFloat())
                            draw(it)
                            surfaceHolder.unlockCanvasAndPost(it)
                        }
                    }
                }else {
                    doInvalidate()
                }
            }

            override fun onError(@NonNull e: Throwable) {}
            override fun onComplete() {}
        }
    }

    companion object {
        private val NUMBER_TEXT_LIST = listOf(
            "日",
            "一",
            "二",
            "三",
            "四",
            "五",
            "六",
            "七",
            "八",
            "九",
            "十"
        )
    }
}