package net.slog.composor

import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.util.Log
import kotlinx.coroutines.*
import net.slog.SLogBinder
import net.slog.logcat.LogcatLogger
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by zhongyongsheng on 2018/12/12.
 */
enum class LogLevel { Verbose, Debug, Info, Warn, Error }

typealias ComposorDispatch = (LogLevel, String) -> Unit

class LogComposor(val tag: String? = "", val composorDispatchers: List<ComposorDispatch>) : SLogBinder.SLogBindLogger {

    val logcat = LogcatLogger(tag)
    val dateFormat = SimpleDateFormat("MM-dd hh:mm:ss.SSS")

    val handler: Handler by lazy {
        HandlerThread("LogComposor", Process.THREAD_PRIORITY_BACKGROUND)
            .let {
                it.start()
                Handler(it.looper)
            }
    }

    override fun isTraceEnable(): Boolean {
        return true
    }

    override fun isDebugEnable(): Boolean {
        return true
    }

    override fun isInfoEnable(): Boolean {
        return true
    }

    override fun verbose(msg: String?, vararg objs: Any?) {
        if (msg != null) {
            var stringiflyArray: Array<Any?>? = null
            val currentTime = System.currentTimeMillis()
            if (objs.isNotEmpty()) {
                stringiflyArray = toStringiflyArray(objs)
                handler.post {
                    val formatMsg = msg.format(*stringiflyArray)
                    dispatchMsg(LogLevel.Verbose, currentTime, formatMsg)
                }
            } else {
                handler.post {
                    dispatchMsg(LogLevel.Verbose, currentTime, msg)
                }
            }
        }
    }

    override fun verbose(tag: String?, msg: String?, vararg objs: Any?) {
    }

    override fun debug(msg: String?, vararg objs: Any?) {
    }

    override fun debug(tag: String?, msg: String?, vararg objs: Any?) {
    }

    override fun info(msg: String?, vararg objs: Any?) {
        if (msg != null) {
            var stringiflyArray: Array<Any?>? = null
            val currentTime = System.currentTimeMillis()
            if (objs.isNotEmpty()) {
                stringiflyArray = toStringiflyArray(objs)
                handler.post {
                    val formatMsg = msg.format(*stringiflyArray)
                    dispatchMsg(LogLevel.Info, currentTime, formatMsg)
                }
            } else {
                handler.post {
                    dispatchMsg(LogLevel.Info, currentTime, msg)
                }
            }
        }
    }

    override fun info(tag: String?, msg: String?, vararg objs: Any?) {
    }

    override fun warn(msg: String?, vararg objs: Any?) {
    }

    override fun warn(tag: String?, msg: String?, vararg objs: Any?) {
    }

    override fun error(msg: String?, vararg objs: Any?) {
    }

    override fun error(msg: String?, throwable: Throwable?, vararg objs: Any?) {
    }

    override fun error(tag: String?, msg: String?, vararg objs: Any?) {
    }

    override fun error(tag: String?, msg: String?, throwable: Throwable?, vararg objs: Any?) {
    }

    override fun flush() {
    }

    private fun dispatchMsg(logLevel: LogLevel, currentTime: Long, msg: String) {
        logcat.verbose("[${dateFormat.format(Date(currentTime))}]$msg")
        composorDispatchers.forEach {
            try {
                it.invoke(logLevel, msg)
            } catch (t : Throwable) {
                Log.e(TAG, "dispatchMsg error", t)
            }
        }
    }

    companion object {
        val TAG = "LogComposor"

        fun toStringiflyArray(arr: Array<out Any?>): Array<Any?> {
            val result = Array<Any?>(arr.size) {}
            arr.forEachIndexed { index, any ->
                result[index] = any.notPrimitiveToString()
            }
            return result
        }


    }
}
