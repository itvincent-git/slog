package net.slog.composor;

import net.slog.ILoggerFactory;
import net.slog.SLogBinder;

/**
 * Composor实现
 * Created by zhongyongsheng on 2018/3/19.
 */

public class ComposorLoggerFactory implements ILoggerFactory {
    @Override
    public SLogBinder.SLogBindLogger getLogger(Class cls) {
        return new LogComposor(cls.getSimpleName());
    }

    @Override
    public SLogBinder.SLogBindLogger getLogger(String name) {
        return new LogComposor(name);
    }
}
