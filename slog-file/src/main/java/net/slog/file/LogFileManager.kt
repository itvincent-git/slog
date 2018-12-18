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
class LogFileManager(/*日志目录*/val logDirectory: File,
        /*日志文件前缀*/val logFilePrefix: String = "logs",
        /*日志文件后缀*/val logFileSurfix: String = ".txt") {

    /**
     * 除当前的日志文件外，压缩logDirectory目录下.txt文件为.zip
     * @param currentLogFile 排除这个文件不压缩
     */
    fun compressBakLogFile(currentLogFile: File) = GlobalScope.async(Dispatchers.IO) {
        try {
            if (!logDirectory.exists()) return@async
            logDirectory.listFiles(FilenameFilter { dir, name ->
                return@FilenameFilter name.startsWith(logFilePrefix) && name.endsWith(logFileSurfix) && currentLogFile.name != name
            }).forEach {
                it.toZipFile(logDirectory)
                it.delete()
            }
        } catch (t: Throwable) {
            log.error("compressBakLogFile error", t)
        }
    }

    companion object {
        val TAG = "LogFileManager"
        val log = SLoggerFactory.getLogger(TAG)
    }



}