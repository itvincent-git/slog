package net.slog.composor

import android.util.Log
import kotlinx.coroutines.*
import net.slog.SLogBinder
import net.slog.logcat.LogcatLogger
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import kotlin.coroutines.CoroutineContext

/**
 * Created by zhongyongsheng on 2018/12/12.
 */
enum class LogLevel { Verbose, Debug, Info, Warn, Error }

typealias ComposorDispatch = (LogLevel, String) -> Unit

class LogComposor(val tag: String? = "", val composorDispatchers: List<ComposorDispatch>) : SLogBinder.SLogBindLogger, CoroutineScope {

    val job: Job = Job()
    val logcat = LogcatLogger(tag)

    override val coroutineContext: CoroutineContext
        get() = job + newSingleThreadContext("LogComposor")


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
            if (objs.isNotEmpty()) {
                stringiflyArray = toStringiflyArray(objs)
                async {
                    val formatMsg = msg.format(*stringiflyArray)
                    dispatchMsg(LogLevel.Verbose, formatMsg)
                }
            } else {
                async {
                    dispatchMsg(LogLevel.Verbose, msg)
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

    private fun dispatchMsg(logLevel: LogLevel, msg: String) {
        logcat.verbose(msg)
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
                result[index] = (any.notPrimitiveToString())
            }
            return result
        }


    }
}
