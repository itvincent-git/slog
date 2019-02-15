package net.slog.composor.logcat

import android.util.Log
import net.slog.composor.ComposorDispatch
import net.slog.composor.LogLevel

/**
 * logcat dispatcher
 * @author zhongyongsheng
 */
class LogcatDispatcher: ComposorDispatch {

    override fun dispatchMessage(tag: String, logLevel: LogLevel, msg: String) {
        when (logLevel) {
            LogLevel.Verbose -> Log.v(tag, msg)
            LogLevel.Debug -> Log.d(tag, msg)
            LogLevel.Info -> Log.i(tag, msg)
            LogLevel.Warn -> Log.w(tag, msg)
            LogLevel.Error -> Log.e(tag, msg)
        }
    }

    override fun flushMessage() {
        // nothing to do
    }
}