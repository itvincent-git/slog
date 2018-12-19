package net.slog.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import net.slog.SLoggerFactory
import net.slog.file.LogFileManager

class LogManagerActivity : AppCompatActivity() {
    val log = SLoggerFactory.getLogger("LogManagerActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_manager)

        log.debug("get latest 3 log file: ${LogFileManager.getLogFileList().takeLast(3)}")
    }
}
