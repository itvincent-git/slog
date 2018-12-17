package net.slog.sample

import android.app.Application
import net.slog.SLoggerFactory
import net.slog.composor.ComposorBinderBuilder
import net.slog.composor.LogLevel
import net.slog.composor.logcat.LogcatDispatcher
import net.slog.file.LogFileDispatcher
import java.io.File

/**
 * Created by zhongyongsheng on 2018/12/17.
 */
class SLogApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        SLoggerFactory.initialize(
                ComposorBinderBuilder()
                        .addDispatcher(LogcatDispatcher())
                        .addDispatcher(LogFileDispatcher(File("/sdcard/slog")))
                        .logLevel(LogLevel.Verbose)
                        .build())
    }
}