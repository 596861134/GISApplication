package com.gis.common.extension

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.gis.common.widget.spannable.replaceSpan
import com.gis.common.widget.spannable.span.CenterImageSpan


/**
 * 商品名称图文混排
 * 在文本前面添加图片，并设置图片宽高
 */
fun TextView.addSpannable(name: String, urlPath: String?) {
    urlPath?.let {
        Glide.with(context)
            .load(urlPath).into(object : CustomTarget<Drawable>(){
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    // 获取图片实际宽高
                    val imageWidth = resource.minimumWidth
                    val imageHeight = resource.minimumHeight
                    val dip2px = 15.dp()
                    val dipWidth: Float = dip2px / imageHeight.toFloat() * imageWidth.toFloat()

                    val span = "[晕] $name".replaceSpan("[晕]") {
                        CenterImageSpan(resource).setDrawableSize(dipWidth.toInt(), dip2px.toInt())
                    }
                    text = span
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    text = name
                }
            })
    }?:kotlin.run {
        text = name
    }

}

/**
 * 设置下划线
 */
fun TextView.addUnderline(@ColorInt color:Int){
    paint.flags = Paint.UNDERLINE_TEXT_FLAG //下划线
    paint.isAntiAlias = true//抗锯齿
    paint.color = color
//    paint.setTypeface(Typeface.DEFAULT_BOLD)
}

/**
 * 设置TextView drawable
 * @param res 资源
 * @param orientation 0-left,1-top,2-right,3-bottom
 */
fun TextView.setDrawable(res:Int, orientation: Int = 0) {
    if (res == 0){
        setCompoundDrawables(null, null, null, null)
    }else {
        val drawable = ContextCompat.getDrawable(this.context, res)
        drawable?.setBounds(0,0, drawable.minimumWidth, drawable.minimumHeight)
        when(orientation){
            0 -> setCompoundDrawables(drawable, null, null, null)
            1 -> setCompoundDrawables(null, drawable, null, null)
            2 -> setCompoundDrawables(null, null, drawable, null)
            3 -> setCompoundDrawables(null, null, null, drawable)
        }
    }
}