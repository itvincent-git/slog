package net.slog.composor

import net.slog.ILoggerFactory
import net.slog.SLogBinder

/**
 * composor实现
 * Created by zhongyongsheng on 2018/3/19.
 */

class ComposorBinder(val composorDispatchers: List<ComposorDispatch>) : SLogBinder {
    internal var loggerFactory: ILoggerFactory = ComposorLoggerFactory(composorDispatchers)

    override fun getILoggerFactory(): ILoggerFactory {
        return loggerFactory
    }
}
