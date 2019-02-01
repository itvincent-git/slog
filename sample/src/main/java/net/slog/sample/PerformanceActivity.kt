package net.slog.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_performance.*
import net.slog.SLoggerFactory
import kotlin.system.measureTimeMillis

class PerformanceActivity : AppCompatActivity() {

    val log = SLoggerFactory.getLogger("PerformanceActivity")
    val count = 1000
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
                log.verbose("performance $count verbose time used $it ms")
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
                log.verbose("performance $count info time used $it ms")
            }
        }

        val list = mutableListOf<Int>()
        info_list_button.text = "$count info list"
        info_list_button.setOnClickListener {
            measureTimeMillis {
                repeat(count) {
                    list += counter++
                    log.info("performance test %s", list)
                    //println("test after $counter")
                }
            }.let {
                log.verbose("performance $count info time used $it ms")
            }
        }
    }
}
