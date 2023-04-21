package com.gis.common.dialog.impl

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.gis.common.R
import com.gis.common.action.AnimAction
import com.gis.common.dialog.BaseDialog
import com.gis.common.utils.ScreenUtils

/**
 * 公共弹窗
 */
class MessageCommonDialog {

    class Builder(context: Context) : BaseDialog.Builder<Builder>(context) {

        private var mContent:TextView?
        private var mIvContent:ImageView?
        private var mTitle:TextView?
        private var tvConform: Button?
        private var tvCancel: Button?
        private var ivClose: ImageView?

        init {
            setContentView(R.layout.dialog_common_view)
            setAnimStyle(AnimAction.ANIM_IOS)
            setBackgroundDimEnabled(true)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            setWidth((ScreenUtils.getScreenWidth(getActivity()) / 4 * 3))

            mContent = findViewById(R.id.tv_content)
            mIvContent = findViewById(R.id.iv_content)
            mTitle = findViewById(R.id.tv_title)
            tvConform = findViewById(R.id.btn_conform)
            tvCancel = findViewById(R.id.btn_cancel)
            ivClose = findViewById(R.id.iv_close)

            tvConform?.setOnClickListener { dismiss() }
            tvCancel?.setOnClickListener { dismiss() }
            ivClose?.setOnClickListener { dismiss() }
        }

        fun setFullWidth():Builder{
            this.setWidth(ScreenUtils.getScreenWidth(getActivity()))
            return this
        }

        fun setAnimBottomStyle() :Builder{
            setGravity(Gravity.BOTTOM)
            return this
        }

        fun setImageSrc(src:Any?):Builder{
            if (null == src || "" == src){
                mIvContent?.visibility = View.GONE
            }else {
                mIvContent?.let {
                    it.visibility = View.VISIBLE
                    Glide.with(getContext()).load(src).into(it)
                }

            }
            return this
        }

        fun setTitle(title:String): Builder{
            if (title.isEmpty()){
                mTitle?.visibility = View.GONE
            }else {
                mTitle?.visibility = View.VISIBLE
                mTitle?.text = title
            }
            return this
        }

        fun setCloseImage(src:Any?):Builder{
            if (null == src || "" == src){
                ivClose?.visibility = View.GONE
            }else {
                ivClose?.let {
                    it.visibility = View.VISIBLE
                    Glide.with(getContext()).load(src).into(it)
                }

            }
            return this
        }

        fun setConformText(title:String): Builder{
            tvConform?.text = title
            return this
        }

        fun setCancelText(title:String): Builder{
            tvCancel?.text = title
            return this
        }

        fun setTips(content: String, local: Boolean? = false): Builder {
            mContent?.text = content
            if (local == true){
                mContent?.gravity = Gravity.CENTER
            }
            return this
        }

        fun setTips(content: CharSequence, local: Boolean? = false): Builder {
            mContent?.text = content
            if (local == true){
                mContent?.gravity = Gravity.CENTER
            }
            return this
        }

        /**
         * 配合SpannableStringBuilder 实现点击事件
         */
        fun setTipsMovementMethod():Builder{
            mContent?.highlightColor = Color.TRANSPARENT
            mContent?.movementMethod = LinkMovementMethod.getInstance()
            return this
        }

        fun setCancelVisible(state: Boolean): Builder {
            tvCancel?.visibility = if (state) View.VISIBLE else View.GONE
            return this
        }

        fun setConformVisible(state: Boolean): Builder {
            tvConform?.visibility = if (state) View.VISIBLE else View.GONE
            return this
        }

        fun setCloseVisible(state: Boolean): Builder {
            ivClose?.visibility = if (state) View.VISIBLE else View.GONE
            return this
        }

        fun setOnCloseClick(click: () -> Unit): Builder {
            ivClose?.setOnClickListener {
                click.invoke()
                dismiss()
            }
            return this
        }

        fun setOnClick(click: () -> Unit): Builder {
            tvConform?.setOnClickListener {
                click.invoke()
                dismiss()
            }
            return this
        }

        fun setOnCancelClick(click: () -> Unit): Builder {
            tvCancel?.setOnClickListener {
                click.invoke()
                dismiss()
            }
            return this
        }

        /**
         * 给java用
         */
        fun setConformListener(listener:OnConformListener?): Builder {
            tvConform?.setOnClickListener {
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