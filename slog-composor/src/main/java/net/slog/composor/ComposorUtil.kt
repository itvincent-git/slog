package net.slog.composor

import android.os.*
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.*

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

private const val THREAD_NAME = "LogComposor"

object ComposorUtil {
    val handler: SafeDispatchHandler by lazy {
        HandlerThread(THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND)
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

    val locale = Locale.SIMPLIFIED_CHINESE

    /**
     * custom app scope
     */
    val appScope = GlobalScope + Dispatchers.IO

    /**
     * is crash happening
     */
    var isCrashHappening = false
}

class SafeDispatchHandler(looper: Looper) : Handler(looper) {
    private var channel: Channel<Boolean>? = null

    override fun dispatchMessage(msg: Message) {
        try {
            super.dispatchMessage(msg)
            if (channel != null && !hasMessages(0)) {
                ComposorUtil.appScope.launch {
                    channel?.send(true)
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "dispatchMessage error", t)
        }
    }

    fun waitMessageFinish() = runBlocking {
        if (hasMessages(0)) {
            channel = Channel()
            withTimeoutOrNull(3000) {
                channel?.receive()
            }
        }
    }

    companion object {
        private const val TAG = "SafeDispatchHandler"
    }
}

/**
 * 处理当系统crash时，等待把日志写完再结束
 */
internal class ComposorUncaughtExceptionHandler(val defaultHandler: Thread.UncaughtExceptionHandler): Thread.UncaughtExceptionHandler {
    private val TAG = "ComposorUEH"

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        ComposorUtil.isCrashHappening = true
        LogComposorHolder.logComposor.dispatchMsg(TAG, LogLevel.Error, "Crash happen > uncaughtException > ${Log.getStackTraceString(e)}")
        ComposorUtil.handler.waitMessageFinish()
        defaultHandler.uncaughtException(t, e)
    }
}
