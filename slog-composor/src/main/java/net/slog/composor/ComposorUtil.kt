package net.slog.composor

/**
 * Created by zhongyongsheng on 2018/12/13.
 */

fun Any?.notPrimitiveToString(): Any? {
    return when (this) {
        is Int -> this
        is Long -> this
        is Short -> this
        is Byte -> this
        is String -> this
        is Float -> this
        is Double -> this
        is Boolean -> this
        else -> this.toString()
    }
}
