package net.slog.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_crash_test.*
import net.slog.SLoggerFactory
import java.lang.RuntimeException

class CrashTestActivity : AppCompatActivity() {
    val log = SLoggerFactory.getLogger("CrashTestActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_test)

        crash_btn.setOnClickListener {
            repeat(5_000) {
                log.info("crash before log test $it")
            }
            throw RuntimeException("test exception")
        }
    }
}
