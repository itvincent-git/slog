package net.slog.composor

import android.util.Log
import net.slog.SLogBinder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zhongyongsheng on 2018/12/12.
 */
enum class LogLevel { Verbose, Debug, Info, Warn, Error }

typealias ComposorDispatch = (String, LogLevel, String) -> Unit

class LogComposor(val mTag: String = "", val composorDispatchers: List<ComposorDispatch>) : SLogBinder.SLogBindLogger {

    //val logcat = LogcatLogger(tag)
    val dateFormat = SimpleDateFormat(FORMAT)

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
        processLog(mTag, LogLevel.Verbose, msg, null,  *objs)
    }

    override fun verbose(tag: String?, msg: String?, vararg objs: Any?) {
        processLog(tag ?: "", LogLevel.Verbose, msg, null,  *objs)
    }

    override fun debug(msg: String?, vararg objs: Any?) {
        processLog(mTag, LogLevel.Debug, msg, null, *objs)
    }

    override fun debug(tag: String?, msg: String?, vararg objs: Any?) {
        processLog(tag ?: "", LogLevel.Debug, msg, null, *objs)
    }

    override fun info(msg: String?, vararg objs: Any?) {
        processLog(mTag, LogLevel.Info, msg, null, *objs)
    }

    override fun info(tag: String?, msg: String?, vararg objs: Any?) {
        processLog(tag ?: "", LogLevel.Info, msg, null, *objs)
    }

    override fun warn(msg: String?, vararg objs: Any?) {
        processLog(mTag, LogLevel.Warn, msg, null, *objs)
    }

    override fun warn(tag: String?, msg: String?, vararg objs: Any?) {
        processLog(tag ?: "", LogLevel.Warn, msg, null, *objs)
    }

    override fun error(msg: String?, vararg objs: Any?) {
        processLog(mTag, LogLevel.Error, msg, null, *objs)
    }

    override fun error(msg: String?, throwable: Throwable?, vararg objs: Any?) {
        processLog(mTag, LogLevel.Error, msg, throwable, *objs)
    }

    override fun error(tag: String?, msg: String?, vararg objs: Any?) {
        processLog(mTag, LogLevel.Error, msg, null, *objs)
    }

    override fun error(tag: String?, msg: String?, throwable: Throwable?, vararg objs: Any?) {
        processLog(tag ?: "", LogLevel.Error, msg, throwable, *objs)
    }

    override fun flush() {
    }

    fun processLog(tag: String, level:LogLevel, msg: String?, throwable: Throwable?, vararg objs: Any?) {
        if (msg != null) {
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
                    dispatchMsg(tag, level, appendTimeString(currentTime, formatMsg))
                }
            } else {
                ComposorUtil.handler.post {

                    dispatchMsg(tag, level, appendTimeString(currentTime, msg.let {
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

    private fun dispatchMsg(tag: String, logLevel: LogLevel, msg: String) {
        //logcat.verbose(msg)
        composorDispatchers.forEach {
            try {
                it.invoke(tag, logLevel, msg)
            } catch (t : Throwable) {
                Log.e(TAG, "dispatchMsg error", t)
            }
        }
    }

    fun appendTimeString(currentTime: Long, msg: String): String {
        return "[${dateFormat.format(Date(currentTime))}]$msg"
    }

    companion object {
        val TAG = "LogComposor"
        val FORMAT = "MM-dd hh:mm:ss.SSS"

        fun toStringiflyArray(arr: Array<out Any?>): Array<Any?> {
            val result = Array<Any?>(arr.size) {}
            arr.forEachIndexed { index, any ->
                result[index] = any.notPrimitiveToString()
            }
            return result
        }


    }
}
