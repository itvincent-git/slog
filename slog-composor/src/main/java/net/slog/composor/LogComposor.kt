package net.slog.composor

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * 目标：高性能、轻量化、接口化
 *
 * 日志组装，包括各种扩展信息组装成字符串后，通过ComposorDispatch分发到logcat、logfile
 * Created by zhongyongsheng on 2018/12/12.
 */
enum class LogLevel(val logMsg: String) {
    Verbose("V/"), Debug("D/"), Info("I/"), Warn("W/"), Error("E/");
}

typealias ComposorDispatch = (String, LogLevel, String) -> Unit

/**
 * 每次SLoggerFactory.getLogger()都会创建一个
 */
class LogComposor(val mLogLevel: LogLevel,
                  val mComposorDispatchers: List<ComposorDispatch>) {


    internal fun processLog(tag: String, level:LogLevel, msg: String?, throwable: Throwable?, vararg objs: Any?) {
        if (msg != null && level >= mLogLevel) {
            val currentTime = System.currentTimeMillis()
            val threadName = Thread.currentThread().name
            if (objs.isNotEmpty()) {
                val stringiflyArray = toStringiflyArray(objs)
                ComposorUtil.handler.post {

                    val formatMsg = msg.format(*stringiflyArray).let {
                        if (throwable != null) {
                            it + "\n" + Log.getStackTraceString(throwable)
                        } else {
                            it
                        }
                    }
                    dispatchMsg(tag, level, appendExternalString(currentTime, threadName, tag, level, formatMsg))
                }
            } else {
                ComposorUtil.handler.post {

                    dispatchMsg(tag, level, appendExternalString(currentTime, threadName, tag, level, msg.let {
                        if (throwable != null) {
                            it + "\n" + Log.getStackTraceString(throwable)
                        } else {
                            it
                        }
                    }))
                }
            }
        }
    }

    internal fun dispatchMsg(tag: String, logLevel: LogLevel, msg: String) {
        mComposorDispatchers.forEach {
            try {
                it.invoke(tag, logLevel, msg)
            } catch (t : Throwable) {
                Log.e(TAG, "dispatchMsg error", t)
            }
        }
    }

    /**
     * 扩展日志信息
     */
    private fun appendExternalString(currentTime: Long, threadName: String?, tag: String, logLevel: LogLevel, msg: String): String {
        return "${logDateFormat.format(Date(currentTime))} $threadName ${logLevel.logMsg}$tag: $msg"
    }

    companion object {
        const val TAG = "LogComposor"
        val logDateFormat = SimpleDateFormat("HH:mm:ss.SSS", ComposorUtil.locale)

        fun toStringiflyArray(arr: Array<out Any?>): Array<Any?> {
            val result = Array<Any?>(arr.size) {}
            arr.forEachIndexed { index, any ->
                result[index] = any.notPrimitiveToString()
            }
            return result
        }


    }
}
