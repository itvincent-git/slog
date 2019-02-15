package net.slog;

import android.support.annotation.NonNull;

import net.slog.logcat.LogcatBinder;

/**
 * 日志工厂，可返回SLogger的实现
 * Created by zhongyongsheng on 2018/3/19.
 */

public class SLoggerFactory {

    protected static SLogBinder sDefaultLogBinder = new LogcatBinder();
    private static boolean isInit = false;

    /**
     * 初始化SLoggerFactory
     * @param defaultBinder 使用哪种绑定作为默认SLog的日志
     */
    public static void initialize(SLogBinder defaultBinder) {
        sDefaultLogBinder = defaultBinder;
        isInit = true;
    }

    /**
     * 请使用{@link SLoggerFactory#getLogger(String)}
     * @param cls
     * @return
     */
    @Deprecated
    @NonNull
    public static SLogger getLogger(Class cls) {
        return getLogger(cls, sDefaultLogBinder);
    }

    /**
     * 请使用{@link SLoggerFactory#getLogger(String,SLogBinder)}
     * @param cls
     * @param sLogBinder
     * @return
     */
    @Deprecated
    public static SLogger getLogger(Class cls, SLogBinder sLogBinder) {
        if (!isInit) throw new IllegalStateException("initialize must call first");
        if (cls == null || sLogBinder == null) throw new IllegalArgumentException("cls and binder must not null");
        ILoggerFactory factory = sLogBinder.getILoggerFactory();
        return factory.getLogger(cls);
    }

    /**
     * 返回SLogger，建议使用这个方法，不用担心混淆类名的问题。
     * 避免使用 {@link SLoggerFactory#getLogger(Class)}
     * @param name 使用该名称为TAG
     * @return
     */
    @NonNull
    public static SLogger getLogger(String name) {
        return getLogger(name, sDefaultLogBinder);
    }

    /**
     * 返回SLogger，建议使用这个方法，不用担心混淆类名的问题。
     * 避免使用 {@link SLoggerFactory#getLogger(Class)}
     * @param name 使用该名称为TAG
     * @param sLogBinder 使用该binder输出日志
     * @return
     */
    public static SLogger getLogger(String name, SLogBinder sLogBinder) {
        if (!isInit) throw new IllegalStateException("initialize must call first");
        if (name == null || sLogBinder == null) throw new IllegalArgumentException("name and binder must not null");
        ILoggerFactory factory = sLogBinder.getILoggerFactory();
        return factory.getLogger(name);
    }
}
