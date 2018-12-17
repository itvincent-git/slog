package net.slog.file

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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