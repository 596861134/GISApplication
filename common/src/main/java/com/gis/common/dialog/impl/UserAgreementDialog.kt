package com.gis.common.dialog.impl

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.gis.common.R
import com.gis.common.action.AnimAction
import com.gis.common.databinding.DialogUserAgreementBinding
import com.gis.common.dialog.BaseDialog
import com.gis.common.extension.doubleTimeClick
import com.gis.common.extension.getResColor
import com.gis.common.extension.getResString
import com.gis.common.extension.showToast
import com.gis.common.manager.AppActivityManager
import com.gis.common.utils.ScreenUtils

/**
 *
 * @ClassName:      UserAgreementDialog
 * @Description:    java类作用描述
 * @Author:         作者
 * @CreateDate:     2022/7/19 11:09 下午
 */
class UserAgreementDialog {

    class Builder(context: Context) : BaseDialog.Builder<Builder>(context){
        private val mBindView: DialogUserAgreementBinding = DialogUserAgreementBinding.inflate(LayoutInflater.from(getContext()))

        init {
            setContentView(mBindView.root)
            setAnimStyle(AnimAction.ANIM_IOS)
            setBackgroundDimEnabled(true)
            setCanceledOnTouchOutside(false)
            setGravity(Gravity.CENTER)
            setWidth((ScreenUtils.getScreenWidth(context) / 5 * 4))
            setAgreement(mBindView.tvAgreemenContent)
            mBindView.tvCancel.setOnClickListener {
                if (it.doubleTimeClick(1000)) return@setOnClickListener
                dismiss()
                AppActivityManager.getInstance().exitApp(getContext())
            }
        }

        fun setOnClick(callback:(() -> Unit)? = null) = also{
            mBindView.tvAgree.setOnClickListener {
                if (it.doubleTimeClick(1000)) return@setOnClickListener
                callback?.invoke()
            }
        }

        //用户协议和隐私协议
        private fun setAgreement(tvAgreement: TextView) = also {
            val linkWord1 = "《造旺计划用户协议》"
            val linkWord2 = "《造旺计划隐私协议》"
            val word =
                (R.string.agreement_text1.getResString() + linkWord1 + "、" + linkWord2
                        + R.string.agreement_text2.getResString())
            val spannableStringBuilder = SpannableStringBuilder(word)
            val index1 = word.indexOf(linkWord1)
            val index2 = word.indexOf(linkWord2)
            spannableStringBuilder.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    "用户协议".showToast()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = R.color.color_416FFC.getResColor() //设置文件颜色
                    ds.isUnderlineText = false //设置下划线
                }
            }, index1, index1 + linkWord1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableStringBuilder.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    "隐私协议".showToast()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = R.color.color_416FFC.getResColor() //设置文件颜色
                    ds.isUnderlineText = false //设置下划线
                }
            }, index2, index2 + linkWord2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvAgreement.text = spannableStringBuilder
            tvAgreement.movementMethod = LinkMovementMethod.getInstance()
        }
    }

}