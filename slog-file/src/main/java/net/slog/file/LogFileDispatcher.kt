package net.slog.file

import android.util.Log
import net.slog.composor.ComposorDispatch
import net.slog.composor.LogLevel
import java.io.File
import java.io.RandomAccessFile
import java.lang.IllegalArgumentException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Created by zhongyongsheng on 2018/12/13.
 */
class LogFileDispatcher(val logDirectory: File): ComposorDispatch {

    private var currentMappedByteBuffer: MappedByteBuffer? = null

    protected val mappedByteBuffer: MappedByteBuffer
        get() {
            if (currentMappedByteBuffer == null) {
                val memoryMappedFile = RandomAccessFile(logFile, "rw")
                val channel = memoryMappedFile.channel
                currentMappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, fileMaxSize)
                for (i in 0..(fileMaxSize - 1)) {
                    currentMappedByteBuffer?.put(blank)
                }
                currentMappedByteBuffer?.position(0)
            }
            return currentMappedByteBuffer!!
        }

    private var currentLogFile: File? = null

    protected val logFile: File
        get() {
            if (currentLogFile == null)
                currentLogFile = File(logDirectory, "log.txt")
            return currentLogFile!!
        }

    protected val lineFeedCode = "\n".toByteArray()

    override fun invoke(tag: String, logLevel: LogLevel, msg: String) {
        if (logLevel > LogLevel.Debug) {
            for (byte in msg.toByteArray()) {
                mappedByteBuffer.put(byte)
            }
            for (byte in lineFeedCode) {
                mappedByteBuffer.put(byte)
            }
        }
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
        val fileMaxSize = 1024 * 1024L
        val blank = ' '.toByte()
    }
}