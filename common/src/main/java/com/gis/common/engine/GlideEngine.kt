package com.gis.common.engine

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.gis.common.R
import com.luck.picture.lib.engine.ImageEngine
import java.io.File

/**
 * Created by chengzf on 2023/5/12.
 * https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/GlideEngine.java
 */
class GlideEngine: ImageEngine {

    companion object{

        private val mGlideEngine: GlideEngine by lazy { GlideEngine() }

        @JvmStatic
        fun getInstance(): GlideEngine {
            return mGlideEngine
        }

    }
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context))return
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    override fun loadImage(context: Context, imageView: ImageView, url: String, maxWidth: Int, maxHeight: Int) {
        if (!ActivityCompatHelper.assertValidRequest(context))return
        Glide.with(context)
            .load(url)
            .override(maxWidth, maxHeight)
            .into(imageView)
    }

    /**
     * 加载相册目录封面
     */
    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context))return
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .sizeMultiplier(0.5f)
            .transform(CenterCrop(), RoundedCorners(8))
            .placeholder(R.drawable.ps_image_placeholder)
            .into(imageView)
    }

    /**
     * 加载相册目录封面
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context))return
        Glide.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .placeholder(R.drawable.ps_image_placeholder)
            .into(imageView)
    }

    override fun pauseRequests(context: Context) {
        if (!ActivityCompatHelper.assertValidRequest(context))return
        Glide.with(context).pauseRequests()
    }

    override fun resumeRequests(context: Context) {
        if (!ActivityCompatHelper.assertValidRequest(context))return
        Glide.with(context).resumeRequests()
    }

    /**
     * 根据url获取图片缓存
     * Glide 4.x请调用此方法
     * 注意：此方法必须在子线程中进行
     *
     * @param context
     * @param url
     * @return
     */
    fun getCacheFileTo4x(context: Context, url: String):File?{
        return try {
            Glide.with(context).downloadOnly().load(url).submit().get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 根据url获取图片缓存
     * Glide 3.x请调用此方法
     * 注意：此方法必须在子线程中进行
     *
     * @param context
     * @param url
     * @return
     */
    fun getCacheFileTo3x(context: Context, url: String):File?{
        return try {
            Glide.with(context).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}