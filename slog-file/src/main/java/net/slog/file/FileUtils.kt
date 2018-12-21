package net.slog.file

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.abs

/**
 * 文件操作工具
 * Created by zhongyongsheng on 2018/12/17.
 */

const val blankCharByte = ' '.toByte()
const val AVERAGE_LOG_ZIP_COMPRESSION_RATIO = 0.15f//ZIP方式在压log的平均压缩率，用于收集日志时，估算日志压缩后大小

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
 * 按时间旧到新排序
 */
fun Array<File>.sortByLastModified(descending: Boolean = false): List<File> {
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
 * 按最靠近timePoint的文件LastModified时间为先进行排序
 */
fun Array<File>.sortByLastModifiedTimePoint(timePoint: Long): List<File> {
    return sortedWith(Comparator { lhs: File, rhs: File ->
        when {
            abs(lhs.lastModified() - timePoint) < abs(rhs.lastModified() - timePoint) -> -1
            abs(lhs.lastModified() - timePoint) > abs(rhs.lastModified() - timePoint) -> 1
            else -> 0
        }
    })
}

/**
 * 按addSize大小计算，返回累计小于maxSize内的文件
 */
fun Collection<File>.takeByFileSize(maxSize: Long, predicate: (File, Long) -> Boolean, addSize: (File) -> Long): List<File> {
    val list = mutableListOf<File>()
    var counterSize = 0L
    for (file in this) {
        if (predicate(file, counterSize)) {
            list += file
            counterSize += addSize(file)
        }
    }
    return list
}

/**
 * 全部文件压缩成zip文件
 */
@Throws(IOException::class)
fun Collection<File>.toZipFile(targetFile: File) {
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