package net.slog.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_simple_log.*
import net.slog.SLoggerFactory

class SimpleLogActivity : AppCompatActivity() {
    val log = SLoggerFactory.getLogger("SimpleLogActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_log)

        button.setOnClickListener {
            log.verbose("simple log")
        }

        button2.setOnClickListener {
            log.verbose("simple log with vararg %d, %s", 1, "string")
        }
    }
}
