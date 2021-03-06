package net.slog.file

import android.support.annotation.WorkerThread
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import net.slog.composor.ComposorUtil
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 日志文件管理
 * Created by zhongyongsheng on 2018/12/17.
 */
object LogFileManager {
    private const val TAG = "LogFileManager"
    private const val format = "yyyy_MM_dd_HH_mm_ss"

    private lateinit var logDirectory: File
    private lateinit var logFilePrefix: String
    private lateinit var logFileSurfix: String

    internal fun initialize(_logDirectory: File, _logFilePrefix: String, _logFileSurfix: String) {
        logDirectory = _logDirectory
        logFilePrefix = _logFilePrefix
        logFileSurfix = _logFileSurfix
    }

    /**
     * 除excludeFile文件外，压缩logDirectory目录下的所有.txt文件为.zip
     * @param currentLogFile 排除这个文件不压缩
     */
    fun compressBakLogFile(excludeFile: File) = ComposorUtil.appScope.async(Dispatchers.IO) {
        try {
            if (!logDirectory.exists()) return@async
            logDirectory.listFiles(FilenameFilter { dir, name ->
                return@FilenameFilter name.startsWith(logFilePrefix) && name.endsWith(logFileSurfix) && excludeFile.name != name
            })?.forEach {
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
            if (!logDirectory.exists()) return emptyList()
            return logDirectory.listFiles(FilenameFilter { dir, name ->
                return@FilenameFilter name.startsWith(logFilePrefix) && (name.endsWith(logFileSurfix) || name.endsWith(".zip"))
            })?.sortByFileNameDate(logFilePrefix, SimpleDateFormat(format, ComposorUtil.locale), true) ?: emptyList()
        } catch (t: Throwable) {
            return emptyList()
        }
    }

    /**
     * 返回一个TimeRange条件内的日志
     * @param timeRange 如果设置了开始和结束时间，则按时间范围来找文件，否则按照最接近这个timeRange.startTime时间点的日志来收集
     */
    @WorkerThread
    fun getLogFileListByTimeRange(timeRange: TimeRange): List<File> {
        try {
            if (!logDirectory.exists()) return emptyList()
            val fileList = logDirectory.listFiles(FilenameFilter { dir, name ->
                return@FilenameFilter name.startsWith(logFilePrefix) && (name.endsWith(logFileSurfix) || name.endsWith(".zip"))
                }) ?: return emptyList()
            return fileList.run {
                    if (timeRange.endTime == 0L) {
                        sortByFileNameDateTimePoint(timeRange.startTime, logFilePrefix, SimpleDateFormat(format, ComposorUtil.locale))//按靠近的时间点排序
                    } else {
                        val dateFormat = SimpleDateFormat(format, ComposorUtil.locale)
                        sortByFileNameDate(logFilePrefix, dateFormat, true)
                                .filterByFileNameDateRange(timeRange, logFilePrefix, dateFormat)//把这段时间范围内的日志过滤出来
                    }
                }
        } catch (t: Throwable) {
            return emptyList()
        }
    }

    /**
     * 清理beforeDays天前日志
     */
    fun cleanBeforeDaysLogFiles(beforeDays: Int) = ComposorUtil.appScope.async(Dispatchers.IO) {
        val files = getLogFileListByTimeRange(TimeRange(0, System.currentTimeMillis() - TimeUnit.DAYS.toMillis(beforeDays.toLong())))
        Log.i(TAG, "cleanBeforeDaysLogFiles $files")
        for (file in files) {
            file.deleteWithoutException()
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

            val fileList = logDirectory.listFiles(FilenameFilter { dir, name -> //取日志文件
                return@FilenameFilter name.startsWith(logFilePrefix) && (name.endsWith(logFileSurfix) || name.endsWith(".zip"))
            }) ?: return@withContext emptyList<File>()

            return@withContext fileList.run {
                if (timeRange.endTime == 0L) {
                    sortByFileNameDateTimePoint(timeRange.startTime, logFilePrefix, SimpleDateFormat(format, ComposorUtil.locale))//按靠近的时间点排序
                } else {
                    val dateFormat = SimpleDateFormat(format, ComposorUtil.locale)
                    sortByFileNameDate(logFilePrefix, dateFormat, true)
                    .filterByFileNameDateRange(timeRange, logFilePrefix, dateFormat)//把这段时间范围内的日志过滤出来
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

    /**
     * create new log file
     */
    internal fun getNewLogFile(): File {
        val dateFormat = SimpleDateFormat(format, ComposorUtil.locale)
        val createNewFile: (Int) -> File = { appendMs ->
            File(logDirectory, "$logFilePrefix${dateFormat.format(getLogFileDate(appendMs))}$logFileSurfix")
        }
        var newFile = createNewFile(0)
        while (newFile.exists()) {// if exists, filename add 1s
            newFile = createNewFile(1)
        }
        Log.i(TAG, "createNewFile $newFile")
        return newFile
    }

    private fun getLogFileDate(appendMs: Int): Date {
        if (appendMs <= 0) return Date()
        val c = Calendar.getInstance()
        c.add(Calendar.SECOND, appendMs)
        return c.time
    }
}