package net.slog.composor

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
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
                    dispatchMsg(tag, level, appendExternalString(currentTime, tag, level, formatMsg))
                }
            } else {
                ComposorUtil.handler.post {

                    dispatchMsg(tag, level, appendExternalString(currentTime, tag, level, msg.let {
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
    protected fun appendExternalString(currentTime: Long, tag: String, logLevel: LogLevel, msg: String): String {
        return "${dateFormat.format(Date(currentTime))} ${logLevel.logMsg}$tag: $msg"
    }

    companion object {
        const val TAG = "LogComposor"
        const val mFormat = "HH:mm:ss.SSS"
        val dateFormat = SimpleDateFormat(mFormat)

        fun toStringiflyArray(arr: Array<out Any?>): Array<Any?> {
            val result = Array<Any?>(arr.size) {}
            arr.forEachIndexed { index, any ->
                result[index] = any.notPrimitiveToString()
            }
            return result
        }


    }
}
