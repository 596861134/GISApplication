package com.gis.common.extension

import com.gis.common.utils.OkhttpUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

fun Any.toRequestBody(ignoreNullField:Boolean=false):RequestBody{

   var targetString:String=""
   var gson: Gson

   if(this is String){
      targetString = this
   }else{
      gson = if (ignoreNullField) {
         GsonBuilder().serializeNulls().create()
      } else {
         Gson()
      }
   
      targetString = gson.toJson(this)
   }
   
//   return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), targetString)
   return targetString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}

val emptyBody: RequestBody = OkhttpUtils.objToRequestBody(HashMap<String, String>(1))
