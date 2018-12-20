package net.slog.composor

import android.os.*
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

/**
 * utils for Composor
 *
 * Created by zhongyongsheng on 2018/12/13.
 */

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

object ComposorUtil {
    val handler: SafeDispatchHandler by lazy {
        HandlerThread("LogComposor", Process.THREAD_PRIORITY_BACKGROUND)
                .let {
                    it.start()
                    SafeDispatchHandler(it.looper)
                }
    }

    /**
     * 处理当系统crash时，等待把日志写完再结束
     */
    fun setComposorUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(ComposorUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler()))
    }
}

class SafeDispatchHandler(looper: Looper) : Handler(looper) {
    var channel: Channel<Boolean>? = null

    override fun dispatchMessage(msg: Message) {
        try {
            super.dispatchMessage(msg)
            if (channel != null && !hasMessages(0)) {
                GlobalScope.async {
                    channel?.send(true)
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "dispatchMessage error", t)
        }
    }

    fun waitMessageFinish() = runBlocking {
        channel = Channel()
        channel?.receive()
    }

    companion object {
        private const val TAG = "SafeDispatchHandler"
    }
}

/**
 * 处理当系统crash时，等待把日志写完再结束
 */
class ComposorUncaughtExceptionHandler(val defaultHandler: Thread.UncaughtExceptionHandler): Thread.UncaughtExceptionHandler {
    val TAG = "ComposorUEH"

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        LogComposorHolder.logComposor.dispatchMsg(TAG, LogLevel.Error, "Crash happen > uncaughtException > ${Log.getStackTraceString(e)}")
        //Log.d(TAG, "wait for log task finishing")//调试时用
        ComposorUtil.handler.waitMessageFinish()
        //Log.d(TAG, "log task finished")//调试时用
        defaultHandler.uncaughtException(t, e)
    }

}
