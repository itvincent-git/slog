package net.slog.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_simple_log.*
import net.slog.SLoggerFactory
import net.slog.composor.ComposorBinder
import net.slog.composor.logcat.LogcatDispatcher
import net.slog.file.LogFileDispatcher
import java.io.File
import java.lang.RuntimeException

class SimpleLogActivity : AppCompatActivity() {

    init {
        SLoggerFactory.initialize(
                ComposorBinder(
                        listOf(LogcatDispatcher(), LogFileDispatcher(File("/sdcard/slog")))))
    }
    val log = SLoggerFactory.getLogger("SimpleLogActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_log)

        v_button.setOnClickListener {
            log.verbose("verbose log")
        }

        vv_button.setOnClickListener {
            log.verbose("verbose log with vararg %d, %s, %s",
                    1, "string", listOf(SimpleData("Lily", 16), SimpleData("Mike", 15)))
        }

        d_button.setOnClickListener {
            log.debug("debug log")
        }

        i_button.setOnClickListener {
            log.info("info log")
        }

        w_button.setOnClickListener {
            log.warn("warn log")
        }

        e_button.setOnClickListener {
            log.error("error log")
            log.error("error with throwable", RuntimeException("test exception"))
        }
    }

    data class SimpleData(val name: String, val age: Int)
}
