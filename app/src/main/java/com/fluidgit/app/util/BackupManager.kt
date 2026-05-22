package com.fluidgit.app.util

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor() {

    fun createBackup(context: Context, destZip: File) {
        val databasesDir = File(context.applicationInfo.dataDir, "databases")
        val prefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
        
        ZipOutputStream(FileOutputStream(destZip)).use { zipOut ->
            if (databasesDir.exists()) {
                zipFiles(databasesDir, "databases", zipOut)
            }
            if (prefsDir.exists()) {
                zipFiles(prefsDir, "shared_prefs", zipOut)
            }
        }
    }

    private fun zipFiles(directory: File, baseName: String, zipOut: ZipOutputStream) {
        val files = directory.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                zipFiles(file, "$baseName/${file.name}", zipOut)
            } else {
                FileInputStream(file).use { fis ->
                    val zipEntry = ZipEntry("$baseName/${file.name}")
                    zipOut.putNextEntry(zipEntry)
                    val bytes = ByteArray(1024)
                    var length: Int
                    while (fis.read(bytes).also { length = it } >= 0) {
                        zipOut.write(bytes, 0, length)
                    }
                    zipOut.closeEntry()
                }
            }
        }
    }
}
