package net.slog.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_extend.*
import net.slog.SLoggerFactory

/**
 * 扩展测试
 */
class ExtendActivity : AppCompatActivity() {
    val log = SLoggerFactory.getLogger("ExtendActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extend)

        with_null_args_btn.setOnClickListener {
            log.info("With null args %s", null)
        }
    }
}
