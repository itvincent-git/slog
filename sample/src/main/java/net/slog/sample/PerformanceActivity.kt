package net.slog.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_performance.*
import net.slog.SLoggerFactory
import net.slog.composor.ComposorBinder
import net.slog.composor.logcat.LogcatDispatcher
import net.slog.file.LogFileDispatcher
import java.io.File
import kotlin.system.measureTimeMillis

class PerformanceActivity : AppCompatActivity() {

    init {
        SLoggerFactory.initialize(
                ComposorBinder(
                        listOf(LogcatDispatcher(), LogFileDispatcher(File("/sdcard/slog")
                        ))))
    }
    val log = SLoggerFactory.getLogger("PerformanceActivity")
    val count = 100
    var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        v_button.text = "$count verbose"
        v_button.setOnClickListener {
            measureTimeMillis {
                repeat(count) {
                    log.verbose("performance test %s %d", "number", counter++)
                }
            }.let {
                log.verbose("performance 10k verbose time used $it ms")
            }
        }

        vv_button.text = "$count info"
        vv_button.setOnClickListener {
            measureTimeMillis {
                repeat(count) {
                    log.info("performance test %s %d", "number", counter++)
                    //println("test after $counter")
                }
            }.let {
                log.verbose("performance 10k info time used $it ms")
            }
        }
    }
}
