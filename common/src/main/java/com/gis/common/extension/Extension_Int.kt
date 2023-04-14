package com.gis.common.extension

//
//
//
// create by iso88591
// time :2020/10/21 9:37 PM
// email:18179157787@163.com
//
//
//

//"%02d" 保证2位数字 不会使得 出现  99:9:19  而会 99:09:19
//并不会 使得 999 -> 99
fun Int.parse2targetNumsNumString():String{
	return "%02d".format(this)
}