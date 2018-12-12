package net.slog;

/**
 * 提供一个接口化的日志接口，实例通过{@link SLoggerFactory#getLogger(String)}方法返回
 * <pre>
 *     protected SLogger log = SLoggerFactory.getLogger("ThisClass");
 *
 *      ...
 *
 *     log.info("log msg: %s", "hello world");
 * </pre>
 *
 * Created by zhongyongsheng on 2018/3/19.
 */

public interface SLogger {

    /**
     * 是否输出Trace日志
     * @return
     */
    boolean isTraceEnable();

    /**
     * 是否输出Debug日志
     * @return
     */
    boolean isDebugEnable();

    /**
     * 是否输出Info日志
     * @return
     */
    boolean isInfoEnable();

    /**
     * verbose日志
     * @param msg
     * @param objs
     */
    void verbose(String msg, Object... objs);

    /**
     * debug日志
     * @param msg
     * @param objs
     */
    void debug(String msg, Object... objs);

    /**
     * info日志
     * @param msg
     * @param objs
     */
    void info(String msg, Object... objs);

    /**
     * warn日志
     * @param msg
     * @param objs
     */
    void warn(String msg, Object... objs);

    /**
     * error日志
     * @param msg
     * @param objs
     */
    void error(String msg, Object... objs);

    /**
     * error日志
     * @param msg
     * @param throwable
     * @param objs
     */
    void error(String msg, Throwable throwable, Object... objs);

    /**
     * 刷新日志到binder
     */
    void flush();
}
