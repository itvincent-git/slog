package net.slog.composor

import kotlinx.coroutines.*
import net.slog.SLogBinder
import net.slog.logcat.LogcatLogger
import kotlin.coroutines.CoroutineContext

/**
 * Created by zhongyongsheng on 2018/12/12.
 */
class LogComposor(val tag: String? = "") : SLogBinder.SLogBindLogger, CoroutineScope {

    val job: Job = Job()
    val dispatchers = mutableListOf<((String) -> Unit)>()
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
                    dispatchMsg(formatMsg)
                }
            } else {
                async {
                    dispatchMsg(msg)
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

    fun dispatchMsg(msg: String) {
        logcat.verbose(msg)
        dispatchers.forEach {
            it.invoke(msg)
        }
    }

    companion object {
        fun toStringiflyArray(arr: Array<out Any?>): Array<Any?> {
            val result = Array<Any?>(arr.size) {}
            arr.forEachIndexed { index, any ->
                result[index] = (any.notPrimitiveToString())
            }
            return result
        }


    }

}

fun Any?.notPrimitiveToString(): Any? {
    return when (this) {
        is Int -> this
        is Long -> this
        is Short -> this
        is Byte -> this
        is String -> this
        is Float -> this
        is Double -> this
        is Boolean -> this
        else -> this.toString()
    }
}