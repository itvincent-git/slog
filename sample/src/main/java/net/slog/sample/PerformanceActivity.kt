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

        button.text = "$count verbose"
        button.setOnClickListener {
            measureTimeMillis {
                repeat(count) {
                    log.verbose("performance test %s %d", "number", counter++)
                }
            }.let {
                Log.i("PerformanceActivity", "performance 10k verbose time used $it ms")
            }
        }

        button2.text = "$count info"
        button2.setOnClickListener {
            measureTimeMillis {
                repeat(count) {
                    log.info("performance test %s %d", "number", counter++)
                    //println("test after $counter")
                }
            }.let {
                Log.i("PerformanceActivity", "performance 10k info time used $it ms")
            }
        }
    }
}
