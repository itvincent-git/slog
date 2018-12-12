package net.slog;

/**
 * 绑定log实现的接口，实现这个接口...
 * Created by zhongyongsheng on 2018/3/19.
 */

public interface SLogBinder {
    ILoggerFactory getILoggerFactory();

    interface SLogBindLogger extends SLogger, STagLogger {

    }
}
