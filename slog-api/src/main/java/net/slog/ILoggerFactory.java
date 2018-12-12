package net.slog;

/**
 * 日志工厂接口，可按实现返回不同的日志逻辑
 * Created by zhongyongsheng on 2018/3/19.
 */

public interface ILoggerFactory {

    SLogBinder.SLogBindLogger getLogger(Class cls);

    SLogBinder.SLogBindLogger getLogger(String name);
}
