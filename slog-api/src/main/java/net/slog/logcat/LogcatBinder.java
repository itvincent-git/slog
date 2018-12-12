package net.slog.logcat;

import net.slog.ILoggerFactory;
import net.slog.SLogBinder;

/**
 * logcat实现
 * Created by zhongyongsheng on 2018/3/19.
 */

public class LogcatBinder implements SLogBinder {
    ILoggerFactory loggerFactory;

    public LogcatBinder() {
        loggerFactory = new LogcatLoggerFactory();
    }

    @Override
    public ILoggerFactory getILoggerFactory() {
        return loggerFactory;
    }
}
