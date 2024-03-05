package com.example.sampleproject.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import org.apache.commons.io.FileUtils
import java.io.File

object UriUtil {
    fun isFileCheck(context: Context, imageUri: Uri?): Boolean {
        return getMimeType(context, imageUri!!).equals("png")||
               getMimeType(context, imageUri!!).equals("gif")||
               getMimeType(context, imageUri!!).equals("jpg")
        return false
    }

    fun getImageFromUri(context: Context, imageUri: Uri?) : File? {
        imageUri?.let { uri ->
            val mimeType = getMimeType(context, uri)
            mimeType?.let {
                val file = createTmpFileFromUri(context, imageUri, "temp_image", ".$it")
                file?.let { Log.d("image Url = ", file.absolutePath) }
                return file
            }
        }
        return null
    }

    private fun getMimeType(context: Context, uri: Uri): String? {
        val extension: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

    private fun createTmpFileFromUri(
        context: Context,
        uri: Uri,
        fileName: String,
        mimeType: String,
    ): File? {
        return try {
            val stream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile(fileName, mimeType,context.cacheDir)
            FileUtils.copyInputStreamToFile(stream,file)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteTempFiles(file: File): Boolean {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    if (f.isDirectory) {
                        deleteTempFiles(f)
                    } else {
                        f.delete()
                    }
                }
            }
        }
        return file.delete()
    }
}