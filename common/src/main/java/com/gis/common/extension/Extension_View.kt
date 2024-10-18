package com.gis.common.extension

import android.view.View
import com.gis.common.utils.DoubleCLickUtils

/**
 * @author:      tainzhi
 * @mail:        qfq61@qq.com
 * @date:        2020/4/2 上午12:05
 * @description:
 **/

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.inVisible() {
    this.visibility = View.INVISIBLE
}

fun View.doubleClick() =  (DoubleCLickUtils.isFastDoubleClick(this))


fun View.doubleTimeClick(time: Long) = (DoubleCLickUtils.isFastDoubleClick(this, time))


fun View.getWindowLocation(offsetX: Int = 0, offsetY: Int = 0): IntArray {
    val intArray = IntArray(2)
    this.getLocationInWindow(intArray)
    intArray[0] += offsetX
    intArray[1] += offsetY
    return intArray
}

fun View.getScreenLocation(offsetX: Int = 0, offsetY: Int = 0): IntArray {
    val intArray = IntArray(2)
    this.getLocationOnScreen(intArray)
    intArray[0] += offsetX
    intArray[1] += offsetY
    return intArray
}


/**
 * 判断点击区域(x,y) 是否在view的区域内
 */
fun View.isTouchPointInView(x:Int, y:Int):Boolean {
    if (this.isNull()) {
        return false
    }
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    val left = location[0]
    val top = location[1]
    val right = left + this.measuredWidth
    val bottom = top + this.measuredHeight
    return y in top..bottom && x in left..right
}
