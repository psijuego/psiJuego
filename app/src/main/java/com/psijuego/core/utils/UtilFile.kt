package com.psijuego.core.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.psijuego.core.Constants
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date


class UtilFile {


    private val context = CoreModule.getContext()!!

    companion object {
        fun getInstance(): UtilFile {
            return UtilFile()
        }
    }

    fun fileName(firstNamePart: String, lastNamePart: String): String {
        val format = SimpleDateFormat("yyMMdd-hhmmss-SSS")
        return "$firstNamePart-${format.format(Date())}$lastNamePart"
    }

    fun deleteFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            var deleted = false
            if (file.exists()) {
                deleted = file.delete()
            }
            deleted
        } catch (e: Exception) {
            false
        }
    }

    fun getExternalDirectory(subFolder: String): String {
        val directory = getDirectory()
        val subFolderFile = File("${directory}/$subFolder")
        if (!subFolderFile.exists()) subFolderFile.mkdirs()
        return subFolderFile.absolutePath
    }

    private fun getDirectory(): String {
        val appDir = CoreModule.getContext()?.filesDir
        if (appDir != null && !appDir.exists()) appDir.mkdirs()
        return appDir!!.absolutePath
    }


    fun getImageFullFilePath(fileName: String?): String? {
        return if (fileName == null) {
            null;
        } else {
            "${getExternalDirectory(Constants.IMAGE_DIRECTORY)}/$fileName"
        }
    }

    private fun getUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.applicationContext.packageName}.provider",
            file
        )
    }

    fun copyContentUriImageToDir(uri: Uri): Uri {
        val inputStream = context.contentResolver.openInputStream(uri)
        val localTargetFilePath = "${getExternalDirectory(Constants.IMAGE_DIRECTORY)}/${
            fileName(
                Constants.IMAGE_FIRST_NAME_PART,
                Constants.JPG_EXTENSION
            )
        }"
        val outPut = FileOutputStream(localTargetFilePath)
        writeOutputStream(inputStream!!, outPut)
        return getUri(File(localTargetFilePath))
    }

    @Throws(IOException::class)
    fun writeOutputStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(8 * 1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
        // Cerrar los flujos
        output.flush()
        output.close()
        input.close()
    }

    fun grantUriPermission(context: Context, intent: Intent, uri: Uri?) {
        val resInfoList =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    fun saveFileToDownloads(file: File) {
        val inputStream = FileInputStream(file)
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString()
        val outputFile = File(downloadsDir, file.name)
        copyFile(inputStream, FileOutputStream(outputFile))
    }

    @Throws(IOException::class)
    private fun copyFile(input: FileInputStream, output: FileOutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } > 0) {
            output.write(buffer, 0, bytesRead)
        }
        input.close()
        output.close()
    }

}