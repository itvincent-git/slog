package net.slog.composor

import net.slog.SLogBinder

/**
 * logger实现为使用ComposorLog记录
 * Created by zhongyongsheng on 2018/12/17.
 */
class ComposorLogBindLogger(val mTag: String = ""): SLogBinder.SLogBindLogger{

    override fun isTraceEnable(): Boolean {
        return LogComposorHolder.logComposor.mLogLevel <= LogLevel.Verbose
    }

    override fun isDebugEnable(): Boolean {
        return LogComposorHolder.logComposor.mLogLevel <= LogLevel.Debug
    }

    override fun isInfoEnable(): Boolean {
        return LogComposorHolder.logComposor.mLogLevel <= LogLevel.Info
    }

    override fun verbose(msg: String?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(mTag, LogLevel.Verbose, msg, null,  *objs)
    }

    override fun verbose(tag: String?, msg: String?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(tag ?: "", LogLevel.Verbose, msg, null,  *objs)
    }

    override fun debug(msg: String?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(mTag, LogLevel.Debug, msg, null, *objs)
    }

    override fun debug(tag: String?, msg: String?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(tag ?: "", LogLevel.Debug, msg, null, *objs)
    }

    override fun info(msg: String?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(mTag, LogLevel.Info, msg, null, *objs)
    }

    override fun info(tag: String?, msg: String?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(tag ?: "", LogLevel.Info, msg, null, *objs)
    }

    override fun warn(msg: String?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(mTag, LogLevel.Warn, msg, null, *objs)
    }

    override fun warn(tag: String?, msg: String?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(tag ?: "", LogLevel.Warn, msg, null, *objs)
    }

    override fun error(msg: String?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(mTag, LogLevel.Error, msg, null, *objs)
    }

    override fun error(msg: String?, throwable: Throwable?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(mTag, LogLevel.Error, msg, throwable, *objs)
    }

    override fun error(tag: String?, msg: String?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(mTag, LogLevel.Error, msg, null, *objs)
    }

    override fun error(tag: String?, msg: String?, throwable: Throwable?, vararg objs: Any?) {
        LogComposorHolder.logComposor.processLog(tag ?: "", LogLevel.Error, msg, throwable, *objs)
    }

    override fun flush() {
        //nothing to do
    }

}