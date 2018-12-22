package net.slog.file

import android.util.Log
import net.slog.composor.ComposorDispatch
import net.slog.composor.LogLevel
import java.io.File
import java.nio.BufferOverflowException
import java.nio.MappedByteBuffer
import java.text.SimpleDateFormat
import java.util.*

/**
 * 输出日志到文件
 * Created by zhongyongsheng on 2018/12/13.
 *
 * @param logDirectory 日志目录
 * @param logFilePrefix 日志文件前缀
 * @param logFileSurfix 日志文件后缀
 * @param fileMaxSize 单个日志文件大小
 * @param logFileLevel 指定达到这个级别及以上的日志才输出到文件，先判定LogComposor的logLevel，再判定该字段
 */
class LogFileDispatcher @JvmOverloads constructor(val logDirectory: File,
                        val logFilePrefix: String = "logs_",
                        val logFileSurfix: String = ".txt",
                        val fileMaxSize: Long = 1024 * 1024L,
                        val logFileLevel: LogLevel = LogLevel.Debug): ComposorDispatch {
    val mFormat = "yyyy_MM_dd_HH_mm_ss"
    val dateFormat = SimpleDateFormat(mFormat)

    init {
        LogFileManager.initialize(this)
    }

    private var currentMappedByteBuffer: MappedByteBuffer? = null

    protected val mappedByteBuffer: MappedByteBuffer
        get() {
            if (currentMappedByteBuffer == null) {
                currentMappedByteBuffer = logFile.toMappedByteBuffer(fileMaxSize)
                LogFileManager.compressBakLogFile(logFile)
            }
            return currentMappedByteBuffer!!
        }

    private var currentLogFile: File? = null

    val logFile: File
        get() {
            if (currentLogFile == null)
                currentLogFile = getNewLogFile()
            return currentLogFile!!
        }

    protected fun getNewLogFile(): File {
        return File(logDirectory, "${logFilePrefix}${dateFormat.format(Date())}$logFileSurfix")
    }

    /**
     * ComposorDispatch实现方法
     */
    override fun invoke(tag: String, logLevel: LogLevel, msg: String) {
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

    private inline fun writeToMappedByteBuffer(byte: Byte) {
        try {
            mappedByteBuffer.put(byte)
        } catch (boe: BufferOverflowException) {
            //超时了文件上限大小，创建一个新文件
            createNewMappedByteBuffer()
            mappedByteBuffer.put(byte)
        }
    }

    fun createNewMappedByteBuffer() {
        currentLogFile = getNewLogFile()
        currentMappedByteBuffer = logFile.toMappedByteBuffer(fileMaxSize)
        LogFileManager.compressBakLogFile(logFile)
    }

    init {
        try {
            if (!logDirectory.exists()) logDirectory.mkdirs()
            if (!logDirectory.isDirectory) throw IllegalArgumentException("logDirectory not a directory")
        } catch (t: Throwable) {
            Log.e(TAG, "init error", t)
        }

    }

    companion object {
        val TAG = "LogFileDispatcher"
        protected val lineFeedCode = "\n".toByteArray()

    }
}