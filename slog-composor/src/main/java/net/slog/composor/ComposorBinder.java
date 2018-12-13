package net.slog.composor;

import net.slog.ILoggerFactory;
import net.slog.SLogBinder;

/**
 * composor实现
 * Created by zhongyongsheng on 2018/3/19.
 */

public class ComposorBinder implements SLogBinder {
    ILoggerFactory loggerFactory;

    public ComposorBinder() {
        loggerFactory = new ComposorLoggerFactory();
    }

    @Override
    public ILoggerFactory getILoggerFactory() {
        return loggerFactory;
    }
}
