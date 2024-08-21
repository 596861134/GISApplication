package com.gis.common.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.gis.common.R

/**
 * 图片可能是 网络图String，资源图Drawable，uri，bitmap，int等，所以用Any接收
 */

/**
 * 设置圆形图片
 */
fun ImageView.setCircleImg(img: Any?){
    if (img.isAnyNullOrEmpty()) return
    Glide.with(this)
        .load(img)
        .placeholder(R.mipmap.default_loading)
        .error(R.mipmap.default_loading)
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .into(this)
}

/**
 * 设置圆角图片
 */
fun ImageView.setRoundImg(img: Any?, round:Float){
    if (img.isAnyNullOrEmpty()) return
    Glide.with(this)
        .load(img)
        .placeholder(R.mipmap.default_loading)
        .error(R.mipmap.default_loading)
        .apply(RequestOptions().transform(RoundedCorners(round.dip2px())))
        .into(this)
}

/**
 * 设置普通图片
 */
fun ImageView.setLoadImg(img: Any?){
    if (img.isAnyNullOrEmpty()) return
    Glide.with(this)
        .load(img)
        .placeholder(R.mipmap.default_loading)
        .error(R.mipmap.default_loading)
        .into(this)
}
