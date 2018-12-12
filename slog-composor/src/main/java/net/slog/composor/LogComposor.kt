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
            val stringObjs = objsToStringObjs(objs)
            async {
                val formatMsg = msg.format(stringObjs)
                logcat.verbose(formatMsg)
                dispatchers.forEach {
                    it.invoke(formatMsg)
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

    companion object {
        fun objsToStringObjs(vararg objs: Any?): List<Any?> {
            return objs.map { it.toString() }
        }
    }

}
//
//interface LogAppender {
//
//    fun dispatchMsg(msg: String)
//}