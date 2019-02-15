package net.slog.file

import android.util.Log
import net.slog.composor.ComposorDispatch
import net.slog.composor.ComposorUtil
import net.slog.composor.LogLevel
import java.io.File
import java.nio.BufferOverflowException
import java.nio.MappedByteBuffer
import java.text.SimpleDateFormat

/**
 * 输出日志到文件
 * Created by zhongyongsheng on 2018/12/13.
 *
 * @param logDirectory 日志目录
 * @param logFilePrefix 日志文件前缀
 * @param logFileSurfix 日志文件后缀
 * @param fileMaxSize 单个日志文件大小
 * @param logFileLevel 指定达到这个级别及以上的日志才输出到文件，先判定LogComposor的logLevel，再判定该字段
 * @param autoCleanBeforeDaysLogs 自动清理N天之前的日志，默认为7天，小于等于0则不清理日志
 */
class LogFileDispatcher @JvmOverloads constructor(val logDirectory: File,
                        val logFilePrefix: String = "logs_",
                        val logFileSurfix: String = ".txt",
                        val fileMaxSize: Long = 1024 * 1024L,
                        val logFileLevel: LogLevel = LogLevel.Debug,
                        val autoCleanBeforeDaysLogs:Int = 7): ComposorDispatch {
    val format = "yyyy_MM_dd_HH_mm_ss"
    val dateFormat = SimpleDateFormat(format, ComposorUtil.locale)

    init {
        LogFileManager.initialize(logDirectory, logFilePrefix, logFileSurfix)
    }

    private var currentMappedByteBuffer: MappedByteBuffer? = null

    protected val mappedByteBuffer: MappedByteBuffer
        get() =
            currentMappedByteBuffer ?: logFile.toMappedByteBuffer(fileMaxSize).apply {
                currentMappedByteBuffer = this
                LogFileManager.compressBakLogFile(logFile)
            }


    private var currentLogFile: File? = null

    private val logFile: File
        get() = currentLogFile ?: LogFileManager.getNewLogFile().apply {
            currentLogFile = this
        }

    /**
     * ComposorDispatch实现方法
     */
    override fun dispatchMessage(tag: String, logLevel: LogLevel, msg: String) {
        if (logLevel >= logFileLevel) {
            try {
                for (byte in msg.toByteArray()) {
                    writeToMappedByteBuffer(byte)
                }
                for (byte in lineFeedCode) {
                    writeToMappedByteBuffer(byte)
                }
            } catch (t: Throwable) {
                Log.e(TAG, "invoke error", t)
            }
        }
    }

    override fun flushMessage() {
        //nothing to do
    }

    private inline fun writeToMappedByteBuffer(byte: Byte) {
        try {
            mappedByteBuffer.put(byte)
        } catch (boe: BufferOverflowException) {
            //超时了文件上限大小，创建一个新文件
            createNewMappedByteBuffer()
            mappedByteBuffer.put(byte)
        }
    }

    private fun createNewMappedByteBuffer() {
        currentLogFile = null
        currentMappedByteBuffer = null
    }

    init {
        try {
            if (!logDirectory.exists()) logDirectory.mkdirs()
            if (!logDirectory.isDirectory) throw IllegalArgumentException("logDirectory not a directory")
            if (autoCleanBeforeDaysLogs > 0) {
                LogFileManager.cleanBeforeDaysLogFiles(autoCleanBeforeDaysLogs)
            }
        } catch (t: Throwable) {
            Log.e(TAG, "init error", t)
        }

    }

    companion object {
        val TAG = "LogFileDispatcher"
        protected val lineFeedCode = "\n".toByteArray()

    }
}