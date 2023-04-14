package com.gis.common.extension

import androidx.appcompat.app.AppCompatActivity

//
//
//
// create by iso88591
// time :2020/11/16 11:08 AM
// email:18179157787@163.com
//
//
//

/**
 * 模板代码 如果没登陆去登陆 否则去完成回掉的事情
 */
inline fun AppCompatActivity.doOrLoginWhenNotLogin(buriedPointTag: String, doCall: (AppCompatActivity) -> Unit) {
	/*if (LocalUserInfoManager.isLogin()) {
		doCall(this)
	} else {
		LoginActivity.start(this, buriedPointTag)
	}*/
}