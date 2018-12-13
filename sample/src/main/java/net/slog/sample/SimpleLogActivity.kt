package net.slog.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_simple_log.*
import net.slog.SLoggerFactory
import net.slog.composor.ComposorBinder

class SimpleLogActivity : AppCompatActivity() {

    init {
        SLoggerFactory.initialize(ComposorBinder())
    }
    val log = SLoggerFactory.getLogger("SimpleLogActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_log)

        button.setOnClickListener {
            log.verbose("simple log")
        }

        button2.setOnClickListener {
            log.verbose("simple log with vararg %d, %s, %s",
                    1, "string", listOf(SimpleData("Lily", 16), SimpleData("Mike", 15)))
        }
    }

    data class SimpleData(val name: String, val age: Int)
}
