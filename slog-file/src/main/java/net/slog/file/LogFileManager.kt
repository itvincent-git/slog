package net.slog.file

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.slog.SLoggerFactory
import java.io.File
import java.io.FilenameFilter

/**
 * 日志文件管理
 * Created by zhongyongsheng on 2018/12/17.
 */
object LogFileManager {
    const val TAG = "LogFileManager"
    val log = SLoggerFactory.getLogger(TAG)

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
            log.error("compressBakLogFile error", t)
        }
    }

    /**
     * 返回全部的日志文件，包括txt/zip，按时间顺序排序
     */
    fun getLogFileList(): List<File> {
        try {
            if (!logDirectory.exists()) return listOf(currentLogFile)
            return logDirectory.listFiles(FilenameFilter { dir, name ->
                return@FilenameFilter name.startsWith(logFilePrefix) && (name.endsWith(logFileSurfix) || name.endsWith(".zip"))
            }).sortedWith(Comparator<File>() { lhs: File, rhs: File ->
                    when {
                        lhs.lastModified() < rhs.lastModified() -> -1
                        lhs.lastModified() > rhs.lastModified() -> 1
                        else -> 0
                    }
            })
        } catch (t: Throwable) {
            log.error("getLogFileList error", t)
            return listOf(currentLogFile)
        }
    }

}