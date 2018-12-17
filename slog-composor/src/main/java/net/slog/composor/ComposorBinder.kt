package net.slog.composor

import net.slog.ILoggerFactory
import net.slog.SLogBinder

/**
 * composor实现
 * Created by zhongyongsheng on 2018/3/19.
 */

class ComposorBinder internal constructor(composorDispatchers: List<ComposorDispatch>) : SLogBinder {
    internal var loggerFactory: ILoggerFactory = ComposorLoggerFactory(composorDispatchers)

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

    fun addDispatcher(dispatch: ComposorDispatch): ComposorBinderBuilder {
        mComposorDispatchers.add(dispatch)
        return this
    }

    fun logLevel(logLevel: LogLevel): ComposorBinderBuilder {
        mLogLevel = logLevel
        return this
    }

    fun build(): ComposorBinder {
        return ComposorBinder(mComposorDispatchers)
    }
}
