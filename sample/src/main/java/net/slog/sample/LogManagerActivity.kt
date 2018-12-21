package net.slog.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_log_manager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.slog.SLoggerFactory
import net.slog.file.LogFileManager
import net.slog.file.TimeRange
import net.slog.file.toMB
import java.io.File
import kotlin.coroutines.CoroutineContext

class LogManagerActivity : AppCompatActivity(), CoroutineScope {
    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    val log = SLoggerFactory.getLogger("LogManagerActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_manager)

        get_log_file_list_btn.setOnClickListener {
            launch {
                log.debug("get latest 3 log file: ${LogFileManager.getLogFileList().take(3)}")
            }
        }

        compress_log_file_btn.setOnClickListener {
            launch {
                LogFileManager.compressLogFile(emptyList(),
                        200 * 1024L,
                        TimeRange(System.currentTimeMillis()),
                        File("/sdcard/slog/temp", "compress_log.zip"))
                        .also { log.debug("compressLogFile list: $it") }
            }
        }

        compress_log_file_by_range_btn.setOnClickListener {
            launch {
                LogFileManager.compressLogFile(emptyList(),
                        200 * 1024L,
                        TimeRange(System.currentTimeMillis() - 24 * 60 * 60 * 1000, System.currentTimeMillis() - 30 * 60 * 1000),
                        File("/sdcard/slog/temp", "compress_log.zip"))
                        .also { log.debug("compressLogFile list: $it") }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
