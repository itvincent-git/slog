package net.slog.composor

import net.slog.ILoggerFactory
import net.slog.SLogBinder

/**
 * Composor实现
 * Created by zhongyongsheng on 2018/3/19.
 */

class ComposorLoggerFactory : ILoggerFactory {
    override fun getLogger(cls: Class<*>): SLogBinder.SLogBindLogger {
        return ComposorLogBindLogger(cls.simpleName)
    }

    override fun getLogger(name: String): SLogBinder.SLogBindLogger {
        return ComposorLogBindLogger(name)
    }
}
