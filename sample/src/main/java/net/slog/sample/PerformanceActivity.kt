package net.slog.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_simple_log.*
import net.slog.SLoggerFactory
import net.slog.composor.ComposorBinder
import net.slog.file.LogFileDispatcher
import java.io.File
import kotlin.system.measureTimeMillis

class PerformanceActivity : AppCompatActivity() {

    init {
        SLoggerFactory.initialize(ComposorBinder(listOf(LogFileDispatcher(File("/sdcard/slog")).dispatcher)))
    }
    val log = SLoggerFactory.getLogger("SimpleLogActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        button.setOnClickListener {
            measureTimeMillis {
                repeat(10000) {
                    log.verbose("one day has %d %s", 24, "hours")
                }
            }.let {
                println("performance 10k verbose time used $it ms")
            }
        }

        button2.setOnClickListener {
            measureTimeMillis {
                repeat(10000) {
                    log.info("one day has %d %s", 24, "hours")
                }
            }.let {
                println("performance 10k info time used $it ms")
            }
        }
    }
}
