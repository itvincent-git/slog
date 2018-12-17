package net.slog.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_simple_log.*
import net.slog.SLoggerFactory

class SimpleLogActivity : AppCompatActivity() {

    val log = SLoggerFactory.getLogger("SimpleLogActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_log)

        v_button.setOnClickListener {
            log.verbose("verbose log")
        }

        vv_button.setOnClickListener {
            log.verbose("verbose log with vararg %d, %s, %s",
                    1, "string", listOf(SimpleData("Lily", 11), SimpleData("Mike", 11)))
        }

        d_button.setOnClickListener {
            log.debug("debug log")
        }

        dv_button.setOnClickListener {
            log.debug("debug log with vararg %d, %s, %s",
                    1, "string", listOf(SimpleData("Lily2", 12), SimpleData("Mike2", 12)))
        }

        i_button.setOnClickListener {
            log.info("info log")
        }

        iv_button.setOnClickListener {
            log.info("info log with vararg %d, %s, %s",
                    1, "string", listOf(SimpleData("Lily3", 13), SimpleData("Mike3", 13)))
        }

        w_button.setOnClickListener {
            log.warn("warn log")
        }

        wv_button.setOnClickListener {
            log.warn("warn log with vararg %d, %s, %s",
                    1, "string", listOf(SimpleData("Lily4", 14), SimpleData("Mike4", 14)))
        }

        e_button.setOnClickListener {
            log.error("error log")
            log.error("error with throwable", RuntimeException("test exception"))
        }

        ev_button.setOnClickListener {
            log.error("error log with vararg %d, %s, %s",
                    1, "string", listOf(SimpleData("Lily5", 15), SimpleData("Mike5", 15)))
            log.error("error log with throwable with vararg %d, %s, %s", RuntimeException("test exception"),
                    1, "string", listOf(SimpleData("Lily5", 15), SimpleData("Mike5", 15)))
        }
    }

    data class SimpleData(val name: String, val age: Int)
}
