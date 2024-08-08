package com.gis.common.dialog.impl

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gis.common.CommonUtil.mContext
import com.gis.common.R
import com.gis.common.action.AnimAction
import com.gis.common.databinding.DialogCommonViewBinding
import com.gis.common.dialog.BaseDialog
import com.gis.common.utils.ScreenUtils

/**
 * 公共弹窗
 */
class MessageCommonDialog {

    class Builder(mContext: Context) : BaseDialog.Builder<Builder>(mContext) {

        private val mBinding: DialogCommonViewBinding = DialogCommonViewBinding.inflate(LayoutInflater.from(mContext))

        private val mTvContent:TextView by lazy { mBinding.tvContent }
        private val mIvContent:ImageView by lazy { mBinding.ivContent }
        private val mTitle:TextView by lazy { mBinding.tvTitle }
        private val tvConform: Button by lazy { mBinding.btnConform }
        private val tvCancel: Button by lazy { mBinding.btnCancel }
        private val ivClose: ImageView by lazy { mBinding.ivClose }

        init {
            setContentView(R.layout.dialog_common_view)
            setAnimStyle(AnimAction.ANIM_IOS)
            setBackgroundDimEnabled(true)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            setWidth((ScreenUtils.getScreenWidth(getActivity()) / 4 * 3))

            tvConform.setOnClickListener { dismiss() }
            tvCancel.setOnClickListener { dismiss() }
            ivClose.setOnClickListener { dismiss() }

            // 配合xml使用，超过固定行数可滚动
            mTvContent.movementMethod = ScrollingMovementMethod.getInstance()
        }

        fun setFullWidth():Builder = also {
            this.setWidth(ScreenUtils.getScreenWidth(getActivity()))
        }

        fun setAnimBottomStyle() :Builder = also {
            setGravity(Gravity.BOTTOM)
        }

        /**
         * 设置标题，不设置默认隐藏
         */
        fun setTitle(title:String): Builder = also {
            if (title.isEmpty()){
                mTitle?.visibility = View.GONE
            }else {
                mTitle?.visibility = View.VISIBLE
                mTitle?.text = title
            }
        }

        /**
         * 设置标题，不设置默认隐藏
         */
        fun setTitle(title:String, gravity:Int = Gravity.CENTER): Builder = also {
            if (title.isEmpty()){
                mTitle.visibility = View.GONE
            }else {
                mTitle.visibility = View.VISIBLE
                mTitle.text = title
            }

            mTitle.gravity = gravity
        }

        /**
         * 设置文本内容
         */
        fun setTips(content: String, local: Boolean? = false): Builder = also {
            mTvContent.text = content
            if (local == true){
                mTvContent.gravity = Gravity.CENTER
            }
        }

        /**
         * 设置文本内容
         */
        fun setTips(content: CharSequence, local: Boolean? = false): Builder = also {
            mTvContent.text = content
            if (local == true){
                mTvContent.gravity = Gravity.CENTER
            }
        }

        /**
         * 设置文本内容颜色
         */
        fun setTipsTextColor(color:Int):Builder = also {
            mTvContent.setTextColor(ContextCompat.getColor(mContext, color))
        }

        /**
         * 配合SpannableStringBuilder 实现点击事件
         */
        fun setTipsMovementMethod():Builder = also {
            mTvContent.highlightColor = Color.TRANSPARENT
            mTvContent.movementMethod = LinkMovementMethod.getInstance()
        }

        /**
         * 隐藏文本内容，适用仅展示图片场景，需要主动调用，默认展示
         */
        fun setTvContentVisible(state: Boolean):Builder = also {
            mTvContent.visibility = if (state) View.VISIBLE else View.GONE
        }

        /**
         * 设置图片，不设默认隐藏
         */
        fun setImageSrc(src:Any?):Builder = also {
            if (null == src || "" == src){
                mIvContent.visibility = View.GONE
            }else {
                mIvContent.visibility = View.VISIBLE
                Glide.with(mContext).load(src).into(mIvContent)
            }
        }

        /**
         * 设置取消按钮文案
         */
        fun setCancelText(title:String): Builder = also {
            tvCancel.visibility = if (title.isNotEmpty()) View.VISIBLE else View.GONE
            tvCancel.text = title
        }

        /**
         * 设置取消按钮展示
         */
        fun setCancelVisible(state: Boolean): Builder = also {
            tvCancel.visibility = if (state) View.VISIBLE else View.GONE
        }

        /**
         * 设置取消按钮点击事件
         */
        fun setOnCancelClick(click: () -> Unit): Builder = also {
            tvCancel.setOnClickListener {
                click.invoke()
                dismiss()
            }
        }

        /**
         * 设置取消按钮展示，右上角x
         */
        fun setCloseVisible(state: Boolean): Builder = also {
            ivClose.visibility = if (state) View.VISIBLE else View.GONE
        }

        /**
         * 设置取消按钮点击事件，右上角x
         */
        fun setOnCloseClick(click: () -> Unit): Builder = also {
            ivClose.setOnClickListener {
                click.invoke()
                dismiss()
            }
        }

        /**
         * 设置确认按钮文案
         */
        fun setConformText(title:String): Builder = also {
            tvConform.visibility = if (title.isNotEmpty()) View.VISIBLE else View.GONE
            tvConform.text = title
        }

        /**
         * 设置确认按钮展示，需要主动调用，默认展示
         */
        fun setConformVisible(state: Boolean): Builder = also {
            tvConform.visibility = if (state) View.VISIBLE else View.GONE
        }

        /**
         * 设置确认点击事件
         */
        fun setOnClick(click: () -> Unit): Builder = also {
            tvConform.setOnClickListener {
                click.invoke()
                dismiss()
            }
        }

        /**
         * 设置确认点击事件，给java用
         */
        fun setConformListener(listener:OnConformListener?): Builder {
            tvConform.setOnClickListener {
                listener?.onClick()
                dismiss()
            }
            return this
        }

        /**
         * 设置确认点击事件，给java用
         */
        fun setCancelListener(listener:OnConformListener?): Builder {
            tvCancel.setOnClickListener {
                listener?.onClick()
                dismiss()
            }
            return this
        }

        interface OnConformListener{
            fun onClick()
        }
    }
}