package com.gis.common.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions


/**
 * 设置圆形图片
 */
fun ImageView.setCircleImg(img: String){
    Glide.with(this)
        .load(img)
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .into(this)
}

/**
 * 设置图片
 */
fun ImageView.setLoadImg(img: Any){
    Glide.with(this)
        .load(img)
        .into(this)
}
