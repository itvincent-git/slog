package net.slog.logcat;

import net.slog.ILoggerFactory;
import net.slog.SLogBinder;

/**
 * logcat实现
 * Created by zhongyongsheng on 2018/3/19.
 */

public class LogcatLoggerFactory implements ILoggerFactory {
    @Override
    public SLogBinder.SLogBindLogger getLogger(Class cls) {
        return new LogcatLogger(cls.getSimpleName());
    }

    @Override
    public SLogBinder.SLogBindLogger getLogger(String name) {
        return new LogcatLogger(name);
    }
}
