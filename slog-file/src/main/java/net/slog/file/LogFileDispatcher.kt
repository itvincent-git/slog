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
class LogFileDispatcher(val logDirectory: File) {

    private var currentMappedByteBuffer: MappedByteBuffer? = null

    val mappedByteBuffer: MappedByteBuffer
        get() {
            if (currentMappedByteBuffer == null) {
                val memoryMappedFile = RandomAccessFile(logFile, "rw")
                val channel = memoryMappedFile.channel
                currentMappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, fileMaxSize)
            }
            return currentMappedByteBuffer!!
        }

    private var currentLogFile: File? = null

    val logFile: File
        get() {
            if (currentLogFile == null)
                currentLogFile = File(logDirectory, "log.txt")
            return currentLogFile!!
        }

    //var fileUsedSize = 0

    val dispatcher :ComposorDispatch = { logLevel, msg ->
        for (byte in msg.toByteArray())
            mappedByteBuffer.put(byte)
        //println("call dispatcher $mappedByteBuffer")
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
    }
}