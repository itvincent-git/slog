package net.slog.composor

import net.slog.ILoggerFactory
import net.slog.SLogBinder

/**
 * composor实现
 * Created by zhongyongsheng on 2018/3/19.
 */

class ComposorBinder internal constructor() : SLogBinder {
    internal var loggerFactory: ILoggerFactory = ComposorLoggerFactory()

    override fun getILoggerFactory(): ILoggerFactory {
        return loggerFactory
    }

}

/**
 * composor builder，用于生成ComposorBinder
 */
class ComposorBinderBuilder {
    private val mComposorDispatchers: MutableList<ComposorDispatch> = mutableListOf()
    private var mLogLevel = LogLevel.Verbose

    /**
     * 添加分发处理器
     */
    fun addDispatcher(dispatch: ComposorDispatch): ComposorBinderBuilder {
        mComposorDispatchers.add(dispatch)
        return this
    }

    /**
     * 定义当前日志级别
     */
    fun logLevel(logLevel: LogLevel): ComposorBinderBuilder {
        mLogLevel = logLevel
        return this
    }

    /**
     * 生成ComposorBinder
     */
    fun build(): ComposorBinder {
        LogComposorHolder.logComposor = LogComposor(mLogLevel, mComposorDispatchers)
        ComposorUtil.setComposorUncaughtExceptionHandler()
        return ComposorBinder()
    }
}

object LogComposorHolder {
    lateinit var logComposor: LogComposor
}
