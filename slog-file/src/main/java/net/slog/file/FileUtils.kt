package net.slog.file

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 文件操作工具
 * Created by zhongyongsheng on 2018/12/17.
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

val blankCharByte = ' '.toByte()

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