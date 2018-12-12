package net.slog;

/**
 * 提供静态日志方法的工具类，建议使用{@link SLogger}和{@link SLoggerFactory#getLogger(String)}来访问日志，这样更灵活
 * Created by zhongyongsheng on 2018/3/19.
 */

public class SLog {

    static SLogger S_LOGGER = SLoggerFactory.getLogger("SLog");

    static STagLogger S_TAGLOGGER = (STagLogger) S_LOGGER;

    /**
     * 是否输出Trace日志
     * @return
     */
    public static boolean isTraceEnable() {
        return S_LOGGER.isTraceEnable();
    }

    /**
     * 是否输出Debug日志
     * @return
     */
    public static boolean isDebugEnable() {
        return S_LOGGER.isDebugEnable();
    }

    /**
     * 是否输出Info日志
     * @return
     */
    public static boolean isInfoEnable() {
        return S_LOGGER.isInfoEnable();
    }

    /**
     * verbose日志
     * @param tag
     * @param msg
     * @param objs
     */
    public static void verbose(String tag, String msg, Object... objs) {
        S_TAGLOGGER.verbose(tag, msg, objs);
    }

    /**
     * debug日志
     * @param tag
     * @param msg
     * @param objs
     */
    public static void debug(String tag, String msg, Object... objs) {
        S_TAGLOGGER.debug(tag, msg, objs);
    }

    /**
     * info日志
     * @param tag
     * @param msg
     * @param objs
     */
    public static void info(String tag, String msg, Object... objs) {
        S_TAGLOGGER.info(tag, msg, objs);
    }

    /**
     * warn日志
     * @param tag
     * @param msg
     * @param objs
     */
    public static void warn(String tag, String msg, Object... objs) {
        S_TAGLOGGER.warn(tag, msg, objs);
    }

    /**
     * error日志
     * @param tag
     * @param msg
     * @param objs
     */
    public static void error(String tag, String msg, Object... objs) {
        S_TAGLOGGER.error(tag, msg, objs);
    }

    /**
     * error日志
     * @param tag
     * @param msg
     * @param throwable
     * @param objs
     */
    public static void error(String tag, String msg, Throwable throwable, Object... objs) {
        S_TAGLOGGER.error(tag, msg, throwable, objs);
    }
}
