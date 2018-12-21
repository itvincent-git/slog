package net.slog.file

import android.support.annotation.WorkerThread
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FilenameFilter
import java.io.IOException

/**
 * 日志文件管理
 * Created by zhongyongsheng on 2018/12/17.
 */
object LogFileManager {
    const val TAG = "LogFileManager"

    private lateinit var logDirectory: File
    private lateinit var logFilePrefix: String
    private lateinit var logFileSurfix: String
    private lateinit var currentLogFile: File

    internal fun initialize(fileDispatcher: LogFileDispatcher) {
        logDirectory = fileDispatcher.logDirectory
        logFilePrefix = fileDispatcher.logFilePrefix
        logFileSurfix = fileDispatcher.logFileSurfix
        currentLogFile = fileDispatcher.logFile
    }
    /**
     * 除当前的日志文件外，压缩logDirectory目录下.txt文件为.zip
     * @param currentLogFile 排除这个文件不压缩
     */
    fun compressBakLogFile(excludeFile: File) = GlobalScope.async(Dispatchers.IO) {
        try {
            if (!logDirectory.exists()) return@async
            logDirectory.listFiles(FilenameFilter { dir, name ->
                return@FilenameFilter name.startsWith(logFilePrefix) && name.endsWith(logFileSurfix) && excludeFile.name != name
            }).forEach {
                it.toZipFile(logDirectory)
                it.delete()
            }
        } catch (t: Throwable) {
            Log.e(TAG, "compressBakLogFile error", t)
        }
    }

    /**
     * 返回全部的日志文件，包括txt/zip，按时间倒序排序
     */
    @WorkerThread
    fun getLogFileList(): List<File> {
        try {
            if (!logDirectory.exists()) return listOf(currentLogFile)
            return logDirectory.listFiles(FilenameFilter { dir, name ->
                return@FilenameFilter name.startsWith(logFilePrefix) && (name.endsWith(logFileSurfix) || name.endsWith(".zip"))
            }).sortByLastModified(true)
        } catch (t: Throwable) {
            return listOf(currentLogFile)
        }
    }

    /**
     * 压缩日志文件
     * @param externalFiles 额外压缩的文件
     * @param maxLogSize 最大压缩包大小
     * @param timeRange 如果设置了开始和结束时间，则按时间范围来找文件，否则按照最接近这个timeRange.startTime时间点的日志来收集
     * @param targetZipFile zip文件路径
     */
    @WorkerThread
    @Throws(IOException::class)
    suspend fun compressLogFile(externalFiles: List<File>, maxLogSize: Long, timeRange: TimeRange, targetZipFile: File): List<File> {
        return withContext(Dispatchers.IO) {
            if (!logDirectory.exists()) return@withContext listOf<File>()

            return@withContext logDirectory.listFiles(FilenameFilter { dir, name ->
                return@FilenameFilter name.startsWith(logFilePrefix) && (name.endsWith(logFileSurfix) || name.endsWith(".zip"))
            })//取日志文件
            .run {
                if (timeRange.endTime == 0L) {
                    sortByLastModifiedTimePoint(timeRange.startTime)//按靠近的时间点排序
                } else {
                    sortByLastModified(true)
                    .filterByLastModifiedRange(timeRange)//把这段时间范围内的日志过滤出来
                }
            }
            .takeByFileSize(maxLogSize, { file, currentSize -> //按文件上限取
                    file.predictCompressedSize() + currentSize < maxLogSize
                }, {
                    it.predictCompressedSize()
                })
            .toMutableList().apply {
                addAll(externalFiles)
                        toZipFile(targetZipFile)
            }
        }
    }

}