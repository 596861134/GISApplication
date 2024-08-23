package com.gis.common.extension

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.gis.common.BuildConfig
import com.gis.common.CommonUtil
import com.gis.common.R
import com.gis.common.log.LogHelper
import com.gis.common.utils.DisplayUtil
import com.hjq.toast.Toaster
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.io.File
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

/**
 * @author czf
 * @date 2020/10/23
 */

fun String.showToast(isLong:Boolean = false) {
    if (isLong) Toaster.showLong(this) else Toaster.showShort(this)
}

fun Any.showToast(isLong:Boolean = false){
    if (isLong) Toaster.showLong(this) else Toaster.showShort(this)
}

/**
 * 生成一个0-几范围内的随机数
 */
fun Int.randomInt() = Random.nextInt(0, this)

fun <T : Any> Observable<T>.subIoObsMain(observer: Observer<T>) {
    this
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(observer)
}

fun Int.delay(runnable: Runnable) {
    Handler(Looper.getMainLooper()).postDelayed(runnable, this.toLong())
}

fun Int.getResString() = ContextCompat.getString(CommonUtil.mContext, this)

fun Int.getResDimen() = CommonUtil.mContext.resources.getDimension(this)

fun Int.getResDrawable() = ContextCompat.getDrawable(CommonUtil.mContext, this)

fun Int.getResColor() = ContextCompat.getColor(CommonUtil.mContext, this)

fun Int.delay(action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ action.invoke() }, this.toLong())
}

fun Float.dip2px(context:Context? = null) =
    if (context.isNotNull()) DisplayUtil.dip2px(context,this) else DisplayUtil.dip2px(this)

fun Float.px2dip(context:Context? = null) =
    if (context.isNotNull()) DisplayUtil.px2dip(context,this) else DisplayUtil.px2dip(this)

fun Float.sp2px(context:Context? = null) =
    if (context.isNotNull()) DisplayUtil.sp2px(context,this) else DisplayUtil.sp2px(this)

fun Float.px2sp(context:Context? = null) =
    if (context.isNotNull()) DisplayUtil.px2sp(context,this) else DisplayUtil.px2sp(this)


fun Any?.isNull() = this == null

fun Any?.isNotNull() = !isNull()

fun Any?.isAnyNullOrEmpty() = this.isNull() || this?.toString()?.isEmpty().truely()

fun Boolean?.truely() = this != null && this

fun Boolean?.falsely() = !truely()

enum class LogEnum {
    VERBOSE, DEBUG, INFO, WARN, ERROR
}

fun String.log(logEnum: LogEnum = LogEnum.ERROR) {
    if (BuildConfig.LOG_ENABLE) {
        when (logEnum) {
            LogEnum.VERBOSE -> LogHelper.v("CZF", this)
            LogEnum.DEBUG -> LogHelper.d("CZF", this)
            LogEnum.INFO -> LogHelper.i("CZF", this)
            LogEnum.WARN -> LogHelper.w("CZF", this)
            LogEnum.ERROR -> LogHelper.e("CZF", this)
        }
    }
}

fun String.logWithTag(tag: String, logEnum: LogEnum = LogEnum.ERROR) {
    if (BuildConfig.LOG_ENABLE) {
        when (logEnum) {
            LogEnum.VERBOSE -> LogHelper.v(tag, this)
            LogEnum.DEBUG -> LogHelper.d(tag, this)
            LogEnum.INFO -> LogHelper.i(tag, this)
            LogEnum.WARN -> LogHelper.w(tag, this)
            LogEnum.ERROR -> LogHelper.e(tag, this)
        }
    }
}

/**
 * 获取对应格式的当前时间
 */
fun getCurrentTime(format:String):String{
    val dateTime = DateTime()
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern(format)
    return formatter.print(dateTime)
}


/**
 * 获取UUID
 */
fun getUUID() = UUID.randomUUID().toString().uppercase(Locale.getDefault())

/**
 * 滑动到指定位置，并使指定位置位于列表最上面
 */
fun RecyclerView.scrollItemToTop(position: Int) {
    val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}

/**
 * 获取缓存路径
 * /storage/emulated/0/Android/media/com.want.hotkidclub.ceo/造旺计划/
 */
fun Activity.getOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, R.string.app_space.getResString()).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
}

/**
 * 包装Image在WebView中的展示
 */
fun String.imageUrlToHtml(): String {
    val bodyHTML = "<img src=${this} style=\"width:100%; max-width:500%;\">"

    val head = ("<head>"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes\"> "
            + "<style>img{max-width:500%; width:100%; height:auto;}*{margin:0px;}</style>"
            + "</head>")

    return "<html>$head<body>$bodyHTML</body></html>"
}

/**
 * 包装富文本在WebView中的展示
 */
fun String.charToHtml(): String {
    val data = "<html>" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes\"> " +
//            "<body style=\"overflow-wrap:break-word;word-break:break-all;white-space: normal; font-size:12px; max-width:500%; width:100%; height:auto; \">${this}</body>" +
            "<body style=\"overflow-wrap:break-word;word-break:break-all;white-space: normal; font-size:12px; max-width:500%; \">${this}</body>" +
            "</html>"

    val js = "<script type=\"text/javascript\">" +
            "var imgs = document.getElementsByTagName('img');" + // 找到img标签
            "for(var i = 0; i<imgs.length; i++){" +  // 逐个改变
            "imgs[i].style.width = '100%';" +  // 宽度改为100%
            "imgs[i].style.height = 'auto';" +
            "}" +
            "</script>"

    val tableJs = "<script type=\"text/javascript\">" +
            "var tables = document.getElementsByTagName('table');" + // 找到table标签
            "for(var i = 0; i<tables.length; i++){" +  // 逐个改变
            "tables[i].border = '1';" +
            "tables[i].style.borderCollapse = 'collapse';" +
            "}" +
            "</script>"

    val jsCode = "<script> " +
            "var imgs=document.getElementsByTagName('img');" +
            "var array = new Array();" +
            "for(var i=0;i<imgs.length;i++){" +
            "array[i] = imgs[i].src;" +
            "imgs[i].onclick=function(){" +
            "wv.onImageClick(this.src); " +
            "};" +
            "}" +
            "wv.getImage(array);" +
            "</script>"

    val picJsCode = "<script> " +
            "var doms = document.getElementsByClassName('image');" +
            "for (var dom of doms) {" +
            "dom.style.cssText = 'display: table; clear: both; text-align: center; margin: 1em auto;';" +
            "}" +
            "</script>"

    return data + tableJs + js + jsCode + picJsCode
}