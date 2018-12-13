package net.slog.composor

import net.slog.ILoggerFactory
import net.slog.SLogBinder

/**
 * Composor实现
 * Created by zhongyongsheng on 2018/3/19.
 */

class ComposorLoggerFactory(val composorDispatchers: List<ComposorDispatch>) : ILoggerFactory {
    override fun getLogger(cls: Class<*>): SLogBinder.SLogBindLogger {
        return LogComposor(cls.simpleName, composorDispatchers)
    }

    override fun getLogger(name: String): SLogBinder.SLogBindLogger {
        return LogComposor(name, composorDispatchers)
    }
}
