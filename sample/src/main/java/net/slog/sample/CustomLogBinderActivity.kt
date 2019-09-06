package net.slog.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import net.slog.SLoggerFactory
import net.slog.composor.ComposorBinderBuilder
import net.slog.composor.LogLevel
import net.slog.composor.logcat.LogcatDispatcher
import net.slog.file.OkLogFileDispatcher
import java.io.File

/**
 * 自定义LogBinder
 */
class CustomLogBinderActivity : AppCompatActivity() {
    val binder = ComposorBinderBuilder()
        .addDispatcher(LogcatDispatcher())
        .addDispatcher(OkLogFileDispatcher(File("/sdcard/slog/custom")))
        .logLevel(LogLevel.Verbose)
        .build()

    val log = SLoggerFactory.getLogger("CustomLogBinderActivity", binder)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_log_binder)

        log.info("custom log")
    }
}
