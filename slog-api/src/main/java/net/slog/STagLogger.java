package net.slog;

/**
 * 带tag的logger
 * Created by zhongyongsheng on 2018/4/26.
 */
public interface STagLogger {

    void verbose(String tag, String msg, Object... objs);

    void debug(String tag, String msg, Object... objs);

    void info(String tag, String msg, Object... objs);

    void warn(String tag, String msg, Object... objs);

    void error(String tag, String msg, Object... objs);

    void error(String tag, String msg, Throwable throwable, Object... objs);

}
