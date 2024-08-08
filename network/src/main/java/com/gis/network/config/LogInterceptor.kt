package com.gis.network.config

import com.gis.common.log.LogHelper
import com.gis.network.util.JsonUtil

import okhttp3.*
import okio.Buffer
import okio.BufferedSource
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.net.URLDecoder
import java.nio.charset.Charset

class LogInterceptor(block: (LogInterceptor.() -> Unit)? = null) : Interceptor {
    private var logLevel: LogLevel = LogLevel.NONE//打印日期的标记
    private var colorLevel: ColorLevel = ColorLevel.DEBUG//默认是debug级别的logcat
    private var logTag = TAG//日志的Logcat的Tag

    init {
        block?.invoke(this)
    }

    /**
     * 设置LogLevel
     */
    fun logLevel(level: LogLevel): LogInterceptor {
        logLevel = level
        return this
    }

    /**
     * 设置colorLevel
     */
    fun colorLevel(level: ColorLevel): LogInterceptor {
        colorLevel = level
        return this
    }

    /**
     * 设置Log的Tag
     */
    fun logTag(tag: String): LogInterceptor {
        logTag = tag
        return this
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        //请求
        val request = chain.request()
        //响应
        return kotlin.runCatching { chain.proceed(request) }
            .onFailure {
                it.printStackTrace()
                logIt(it.message.toString(), ColorLevel.ERROR)
            }.onSuccess { response ->
                if (logLevel == LogLevel.NONE) {
                    return response
                }
                //记录请求日志
                logRequest(request, chain.connection())
                //记录响应日志
                logResponse(response)
            }.getOrThrow()
    }

    /**
     * 记录请求日志
     */
    private fun logRequest(request: Request, connection: Connection?) {
        val sb = StringBuilder()
        sb.appendLine("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->")
        when (logLevel) {
            LogLevel.NONE -> {
                /*do nothing*/
            }
            LogLevel.BASIC -> {
                logBasicReq(sb, request, connection)
            }
            LogLevel.HEADERS -> {
                logHeadersReq(sb, request, connection)
            }
            LogLevel.BODY -> {
                logBodyReq(sb, request, connection)
            }
        }
        sb.appendLine("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->")
        splitLog(sb,ColorLevel.DEBUG)
    }

    //region log  request

    private fun logBodyReq(sb: StringBuilder, request: Request, connection: Connection?) {
        logHeadersReq(sb, request, connection)
        //读取出request Body的内容
        val req = request.newBuilder().build()
        val sink = Buffer()
        req.body?.writeTo(sink)
        sb.appendLine("RequestBody: ${JsonUtil.json(sink.readUtf8())}")
    }

    private fun logHeadersReq(sb: StringBuilder, request: Request, connection: Connection?) {
        logBasicReq(sb, request, connection)
        val headersStr = request.headers.joinToString("") { header ->
            "请求 Header: {${header.first}=${header.second}}\n"
        }
        sb.appendLine(headersStr)
    }

    private fun logBasicReq(sb: StringBuilder, request: Request, connection: Connection?) {
        sb.appendLine("请求 method: ${request.method} url: ${decodeUrlStr(request.url.toString())} tag: ${request.tag()} protocol: ${connection?.protocol() ?: Protocol.HTTP_1_1}")
    }

    //endregion

    /**
     * 记录响应日志
     * [response] 响应数据
     */
    private fun logResponse(response: Response) {
        val sb = StringBuilder()
        sb.appendLine("<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<")
        when (logLevel) {
            LogLevel.NONE -> {
                /*do nothing*/
            }
            LogLevel.BASIC -> {
                logBasicRsp(sb, response)
            }
            LogLevel.HEADERS -> {
                logHeadersRsp(response, sb)
            }
            LogLevel.BODY -> {
                logHeadersRsp(response, sb)
                //body.string会抛IO异常
                kotlin.runCatching {
                    //peek类似于clone数据流，监视，窥探,不能直接用原来的body的string流数据作为日志，会消费掉io，所以这里是peek，监测
//                    val peekBody = response.peekBody(1024 * 1024)
//                    sb.appendln(UnicodeUtil.unicodeDecode(peekBody.string()))

                    val responseBody:ResponseBody? = response.body
                    val source: BufferedSource? = responseBody?.source()
                    source?.request(Long.MAX_VALUE)
                    val buffer:Buffer? = source?.buffer
                    val UTF8:Charset = Charset.forName("UTF-8")
                    val readString = buffer?.clone()?.readString(UTF8)
                    sb.appendLine("响应 Body: "+ readString?.let { JsonUtil.json(it) })
                }.getOrNull()
            }
        }
        sb.appendLine("<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<")
        splitLog(sb, ColorLevel.INFO)
    }


    //region log response

    private fun logHeadersRsp(response: Response, sb: StringBuilder) {
        logBasicRsp(sb, response)
        val headersStr = response.headers.joinToString(separator = "") { header ->
            "响应 Header: {${header.first}=${header.second}}\n"
        }
        sb.appendLine(headersStr)
    }

    private fun logBasicRsp(sb: StringBuilder, response: Response) {
        val time1 = DateTime(response.sentRequestAtMillis) // 根据时间戳创建DateTime对象
        val time2 = DateTime(response.receivedResponseAtMillis) // 根据时间戳创建DateTime对象
        val interval = Interval(time1, time2) // 创建表示时间间隔的Interval对象
        // 两个时间戳之间的时间间隔，并按照时、分、秒和毫秒对应地获得相应的值
        val years = interval.toPeriod().years
        val days = interval.toPeriod().days
        val hours = interval.toPeriod().hours
        val minutes = interval.toPeriod().minutes
        val seconds = interval.toPeriod().seconds
        val millis = interval.toPeriod().millis
        sb.appendLine("响应 protocol: ${response.protocol} code: ${response.code} message: ${response.message}")
            .appendLine("响应 request Url: ${decodeUrlStr(response.request.url.toString())}")
            .appendLine("响应 sentRequestTime: ${toDateTimeStr(response.sentRequestAtMillis, MILLIS_PATTERN)}")
            .appendLine("响应 receivedResponseTime: ${toDateTimeStr(response.receivedResponseAtMillis, MILLIS_PATTERN)}")
            .appendLine("响应 requestDuration: ${seconds}s ${millis}ms")
    }

    //endregion

    /**
     * 对于url编码的string 解码
     */
    private fun decodeUrlStr(url: String): String? {
        return kotlin.runCatching {
            URLDecoder.decode(url, "utf-8")
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()
    }

    /**
     * 打印日志
     * [any]需要打印的数据对象
     * [tempLevel],便于临时调整打印color等级
     */
    private fun logIt(any: Any, tempLevel: ColorLevel? = null) {
        when (tempLevel ?: colorLevel) {
            ColorLevel.VERBOSE -> LogHelper.v(TAG, any.toString())
            ColorLevel.DEBUG -> LogHelper.d(TAG, any.toString())
            ColorLevel.INFO -> LogHelper.i(TAG, any.toString())
            ColorLevel.WARN -> LogHelper.w(TAG, any.toString())
            ColorLevel.ERROR -> LogHelper.e(TAG, any.toString())
        }
    }

    /**
     * 截取Log输出
     */
    private fun splitLog(sb: StringBuilder?, tempLevel: ColorLevel? = null) {
        var msg:String? = sb.toString()
        if (msg == null || msg.isEmpty()) {
            return
        }
        val segmentSize:Int = 3 * 1024
        val length:Long = msg.length.toLong()
        if (length <= segmentSize) { // 长度小于等于限制直接打印
            logIt(msg, tempLevel)
        } else {
            var count = 0
            while (msg!!.length > segmentSize) { // 循环分段打印日志
                val logContent = msg.substring(0, segmentSize)
                msg = msg.replace(logContent, "")
                if (count==0){
                    logIt(logContent, tempLevel)
                }else {
                    logIt("\r\n"+logContent, tempLevel)
                }
                count++
            }
            logIt("\r\n"+msg, tempLevel) // 打印剩余日志
        }
    }

    companion object {
        private const val TAG = "<NetWork>"//默认的TAG

        //时间格式化
        const val MILLIS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSZ"

        //转化为格式化的时间字符串S
        fun toDateTimeStr(millis: Long, pattern: String): String {
//            return SimpleDateFormat(pattern, Locale.getDefault()).format(millis)
            // 根据时间戳创建DateTime对象
            val dateTime = DateTime(millis)
            // 定义所需的时间格式
            val formatter: DateTimeFormatter = DateTimeFormat.forPattern(pattern)
            // 将DateTime对象按照指定时间格式转换为字符串
            return dateTime.toString(formatter)
        }
    }


    /**
     * 打印日志的范围
     */
    enum class LogLevel {
        NONE,//不打印
        BASIC,//只打印行首，请求/响应
        HEADERS,//打印请求和响应的 所有 header
        BODY,//打印所有
    }

    /**
     * Log颜色等级，应用于Android Logcat分为 v、d、i、w、e
     */
    enum class ColorLevel {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

}