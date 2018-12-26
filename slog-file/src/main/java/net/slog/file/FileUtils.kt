package net.slog.file

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.DateFormat
import java.text.ParseException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.abs

/**
 * 文件操作工具
 * Created by zhongyongsheng on 2018/12/17.
 */

const val blankCharByte = ' '.toByte()
const val AVERAGE_LOG_ZIP_COMPRESSION_RATIO = 0.15f//ZIP方式在压log的平均压缩率，用于收集日志时，估算日志压缩后大小
val dateOfFirstTime = Date(0)

/**
 * 时间范围，单位毫秒
 */
data class TimeRange(val startTime: Long, val endTime: Long = 0L)

/**
 * 压缩成zip文件
 */
@Throws(IOException::class)
fun File.toZipFile(targetDir: File,
                   targetFileName: String = this.nameWithoutExtension + ".zip") {
    val zipFile = File(targetDir, targetFileName)
            .apply { if (this.exists()) this.delete() }
    ZipOutputStream(FileOutputStream(zipFile)).use { stream ->
        val zipEntry = ZipEntry(this.name)
        stream.putNextEntry(zipEntry)
        this.forEachBlock { bytes: ByteArray, i: Int ->
            stream.write(bytes)
        }
        stream.closeEntry()
    }
}

/**
 * 转成MappedByteBuffer
 */
fun File.toMappedByteBuffer(fileMaxSize: Long): MappedByteBuffer? {
    val memoryMappedFile = RandomAccessFile(this, "rw")
    val channel = memoryMappedFile.channel

    //reset file to put blank byte, avoid the unknown char at the end of the file
    val mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, fileMaxSize)
    for (i in 0..(fileMaxSize - 1)) {
        mappedByteBuffer.put(blankCharByte)
    }

    //revert to the start position
    mappedByteBuffer.position(0)
    return mappedByteBuffer
}

/**
 * 预测压缩后的文件大小
 */
fun File.predictCompressedSize(): Long {
    if (name.endsWith(".zip")) return length()
    return (length() * AVERAGE_LOG_ZIP_COMPRESSION_RATIO).toLong()
}

/**
 * 返回文件名上的时间，不包含后缀名
 * @param prefix 排除这个前缀，不匹配时间格式
 */
fun File.fileNameToDate(prefix: String, dateFormat: DateFormat): Date {
    val dateString = nameWithoutExtension.substringAfter(prefix)
    return try {
        dateFormat.parse(dateString)
    } catch (e: ParseException) {
        dateOfFirstTime
    }
}

/**
 * 按时间旧到新排序
 */
fun Array<File>.sortByLastModified(descending: Boolean = false): List<File> {
    if (isEmpty()) return emptyList()
    return sortedWith(Comparator { lhs: File, rhs: File ->
        if (descending) {
            when {
                lhs.lastModified() < rhs.lastModified() -> 1
                lhs.lastModified() > rhs.lastModified() -> -1
                else -> 0
            }
        } else {
            when {
                lhs.lastModified() < rhs.lastModified() -> -1
                lhs.lastModified() > rhs.lastModified() -> 1
                else -> 0
            }
        }
    })
}

/**
 * 按文件名的日期来排期
 */
fun Array<File>.sortByFileNameDate(prefix: String, dateFormat: DateFormat, descending: Boolean = false): List<File> {
    if (isEmpty()) return emptyList()
    return sortedWith(Comparator { lhs: File, rhs: File ->
        if (descending) {
            when {
                lhs.fileNameToDate(prefix, dateFormat) < rhs.fileNameToDate(prefix, dateFormat) -> 1
                lhs.fileNameToDate(prefix, dateFormat) > rhs.fileNameToDate(prefix, dateFormat) -> -1
                else -> 0
            }
        } else {
            when {
                lhs.fileNameToDate(prefix, dateFormat) < rhs.fileNameToDate(prefix, dateFormat) -> -1
                lhs.fileNameToDate(prefix, dateFormat) > rhs.fileNameToDate(prefix, dateFormat) -> 1
                else -> 0
            }
        }
    })
}

/**
 * 按最靠近timePoint的文件LastModified时间为先进行排序
 */
fun Array<File>.sortByLastModifiedTimePoint(timePoint: Long): List<File> {
    if (isEmpty()) return emptyList()
    return sortedWith(Comparator { lhs: File, rhs: File ->
        when {
            abs(lhs.lastModified() - timePoint) < abs(rhs.lastModified() - timePoint) -> -1
            abs(lhs.lastModified() - timePoint) > abs(rhs.lastModified() - timePoint) -> 1
            else -> 0
        }
    })
}

/**
 * 按最靠近timePoint的文件FileNameDate时间为先进行排序
 */
fun Array<File>.sortByFileNameDateTimePoint(timePoint: Long, prefix: String, dateFormat: DateFormat): List<File> {
    if (isEmpty()) return emptyList()
    return sortedWith(Comparator { lhs: File, rhs: File ->
        when {
            abs(lhs.fileNameToDate(prefix, dateFormat).time - timePoint) < abs(rhs.fileNameToDate(prefix, dateFormat).time - timePoint) -> -1
            abs(lhs.fileNameToDate(prefix, dateFormat).time - timePoint) > abs(rhs.fileNameToDate(prefix, dateFormat).time - timePoint) -> 1
            else -> 0
        }
    })
}


/**
 * 按最近修改时间在timeRange范围内则被选中返回
 */
fun Collection<File>.filterByLastModifiedRange(timeRange: TimeRange): List<File> {
    if (isEmpty()) return emptyList()
    return filter {
        val l = it.lastModified()
        l >= timeRange.startTime && l <= timeRange.endTime
    }
}

/**
 * 按FileNameDate在timeRange范围内则被选中返回
 */
fun Collection<File>.filterByFileNameDateRange(timeRange: TimeRange, prefix: String, dateFormat: DateFormat): List<File> {
    if (isEmpty()) return emptyList()
    return filter {
        val l = it.fileNameToDate(prefix, dateFormat).time
        l >= timeRange.startTime && l <= timeRange.endTime
    }
}

/**
 * 按addSize大小计算，返回累计小于maxSize内的文件
 */
fun Collection<File>.takeByFileSize(maxSize: Long, predicate: (File, Long) -> Boolean, addSize: (File) -> Long): List<File> {
    if (isEmpty()) return emptyList()
    val list = mutableListOf<File>()
    var counterSize = 0L
    for (file in this) {
        if (predicate(file, counterSize)) {
            list += file
            counterSize += addSize(file)
        } else {
            break
        }
    }
    return list
}

/**
 * 全部文件压缩成zip文件
 */
@Throws(IOException::class)
fun Collection<File>.toZipFile(targetFile: File) {
    if (isEmpty()) return
    if (targetFile.exists()) {
        targetFile.delete()
    } else {
        targetFile.parentFile.mkdirs()
        targetFile.createNewFile()
    }
    FileOutputStream(targetFile).use {
        ZipOutputStream(it).use { stream ->

            for (file in this) {
                val zipEntry = ZipEntry(file.name)
                stream.putNextEntry(zipEntry)
                file.forEachBlock { bytes: ByteArray, i: Int ->
                    stream.write(bytes)
                }
            }
            stream.closeEntry()
        }
    }
}

/**
 * 转换成MB的大小
 */
fun Int.toMB(): Long {
    return this * 1024 * 1024L
}

/**
 * 删除文件，如果有异常返回false
 */
fun File.deleteWithoutException(): Boolean {
    try {
        return delete()
    } catch (t: Throwable) {
        return false
    }
}