package net.slog.sample

import android.app.Application
import android.util.Log
import net.slog.SLoggerFactory
import net.slog.composor.ComposorBinderBuilder
import net.slog.composor.ComposorUtil
import net.slog.composor.LogLevel
import net.slog.composor.logcat.LogcatDispatcher
import net.slog.file.LogFileDispatcher
import net.slog.file.OkLogFileDispatcher
import java.io.File

/**
 * 在Application中初始化SLog
 * Created by zhongyongsheng on 2018/12/17.
 */
class SLogApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        //used for test custom UncaughtExceptionHandler
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e("SLogApplication", "UncaughtExceptionHandler $t", e)
            defaultHandler.uncaughtException(t, e)
        }

        OkLogFileDispatcher.debug = true

        SLoggerFactory.initialize(
                ComposorBinderBuilder()
                        .addDispatcher(LogcatDispatcher())
                        //.addDispatcher(LogFileDispatcher(File("/sdcard/slog")))
                        .addDispatcher(OkLogFileDispatcher(File("/sdcard/slog")))
                        .logLevel(LogLevel.Verbose)
                        .build())
    }
}