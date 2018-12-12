package net.slog.logcat;

import android.util.Log;

import net.slog.SLogBinder;

/**
 * logcat实现
 * Created by zhongyongsheng on 2018/3/19.
 */

public class LogcatLogger implements SLogBinder.SLogBindLogger {

    protected String mTag;

    public LogcatLogger(String tag) {
        mTag = tag != null ? tag : "";
    }

    @Override
    public boolean isTraceEnable() {
        return true;
    }

    @Override
    public boolean isDebugEnable() {
        return true;
    }

    @Override
    public boolean isInfoEnable() {
        return true;
    }

    protected String format(String value, Object... objs) {
        try {
            return String.format(value, objs);
        } catch (Exception e) {
            return value;
        }
    }

    @Override
    public void verbose(String msg, Object... objs) {
        Log.v(mTag, format(msg, objs));
    }

    @Override
    public void verbose(String tag, String msg, Object... objs) {
        Log.v(tag, format(msg, objs));
    }

    @Override
    public void debug(String msg, Object... objs) {
        Log.d(mTag, format(msg, objs));
    }

    @Override
    public void debug(String tag, String msg, Object... objs) {
        Log.d(tag, format(msg, objs));
    }

    @Override
    public void info(String msg, Object... objs) {
        Log.i(mTag, format(msg, objs));
    }

    @Override
    public void info(String tag, String msg, Object... objs) {
        Log.i(tag, format(msg, objs));
    }

    @Override
    public void warn(String msg, Object... objs) {
        Log.w(mTag, format(msg, objs));
    }

    @Override
    public void warn(String tag, String msg, Object... objs) {
        Log.w(tag, format(msg, objs));
    }

    @Override
    public void error(String msg, Object... objs) {
        Log.e(mTag, format(msg, objs));
    }

    @Override
    public void error(String tag, String msg, Object... objs) {
        Log.e(tag, format(msg, objs));
    }

    @Override
    public void error(String msg, Throwable throwable, Object... objs) {
        Log.e(mTag, format(msg, objs), throwable);
    }

    @Override
    public void error(String tag, String msg, Throwable throwable, Object... objs) {
        Log.e(tag, format(msg, objs), throwable);
    }

    @Override
    public void flush() {
        //nothing to do
    }
}
