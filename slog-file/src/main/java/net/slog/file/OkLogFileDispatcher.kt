package net.slog.file

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.slog.composor.ComposorDispatch
import net.slog.composor.ComposorUtil
import net.slog.composor.LogLevel
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.measureNanoTime

/**
 * 输出日志到文件，使用okio的方式
 * Created by zhongyongsheng on 2018/12/13.
 *
 * @param logDirectory 日志目录
 * @param logFilePrefix 日志文件前缀
 * @param logFileSurfix 日志文件后缀
 * @param fileMaxSize 单个日志文件大小
 * @param logFileLevel 指定达到这个级别及以上的日志才输出到文件，先判定LogComposor的logLevel，再判定该字段
 * @param autoCleanBeforeDaysLogs 自动清理N天之前的日志，默认为7天，小于等于0则不清理日志
 */
class OkLogFileDispatcher @JvmOverloads constructor(val logDirectory: File,
                                                  val logFilePrefix: String = "logs_",
                                                  val logFileSurfix: String = ".txt",
                                                  val fileMaxSize: Long = 1024 * 1024L,
                                                  val logFileLevel: LogLevel = LogLevel.Debug,
                                                  val autoCleanBeforeDaysLogs:Int = 7): ComposorDispatch {

    private var currentLogFile: File? = null
    private var currentBufferedSink: BufferedSink? = null
    private var writeByteCount = 0

    init {
        LogFileManager.initialize(logDirectory, logFilePrefix, logFileSurfix)
    }

    private val bufferSink: BufferedSink
        get() = currentBufferedSink ?: logFile.sink().buffer().apply {
            currentBufferedSink = this
            LogFileManager.compressBakLogFile(logFile)
        }

    private val logFile: File
        get() = currentLogFile ?: LogFileManager.getNewLogFile().apply {
            currentLogFile = this
        }

    /**
     * ComposorDispatch实现方法
     */
    override fun invoke(tag: String, logLevel: LogLevel, msg: String) {
        if (logLevel >= logFileLevel) {
            try {
                measureNanoTime {
                    bufferSink.writeUtf8Line(msg)
                    writeByteCount += msg.length
                    if (ComposorUtil.isCrashHappening) {
                        bufferSink.flush()
                    }
                    if (isOverFileLimit()) {
                        resetBufferedSink()
                    }
                }.also { if (debug) Log.d(TAG, "invoke time used:${it}ns, writeByteCount:$writeByteCount")}
            } catch (t: Throwable) {
                Log.e(TAG, "invoke error", t)
            }
        }
    }

    private fun isOverFileLimit() = writeByteCount > fileMaxSize

    private fun resetBufferedSink() {
        bufferSink.close()
        writeByteCount = 0
        currentBufferedSink = null
        currentLogFile = null
    }

    init {
        try {
            if (!logDirectory.exists()) logDirectory.mkdirs()
            if (!logDirectory.isDirectory) throw IllegalArgumentException("logDirectory not a directory")
            if (autoCleanBeforeDaysLogs > 0) {
                LogFileManager.cleanBeforeDaysLogFiles(autoCleanBeforeDaysLogs)
            }
            //Flush to disk every 10 seconds
            ComposorUtil.appScope.launch {
                produceInterval(period = TimeUnit.SECONDS.toMillis(10)).consumeEach {
                    ComposorUtil.handler.post {
                        bufferSink.flush()
                        if (debug) Log.d(TAG, "auto flush in interval")
                    }
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "init error", t)
        }

    }

    companion object {
        private const val TAG = "OkLogFileDispatcher"
        private val lineFeedCode = "\n".toByteArray()
        var debug = false

        fun BufferedSink.writeUtf8Line(string: String) {
            writeUtf8(string)
            write(lineFeedCode)
        }
    }
}

/**
 * Send data at regular intervals, and return ReceiveChannel
 */
internal fun CoroutineScope.produceInterval(context: CoroutineContext = EmptyCoroutineContext,
                                   capacity: Int = 0,
                                   initialDelay: Long = 0L,
                                   period: Long) = produce(context, capacity) {
    delay(initialDelay)
    while (true) {
        send(System.currentTimeMillis())
        delay(period)
    }
}