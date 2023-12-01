package com.conduent.nationalhighways.utils.common

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Objects

object PicUtils {

    fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? {
        var fileName: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndexOrThrow("_display_name"))
                if (!displayName.isNullOrEmpty()) {
                    fileName = displayName
                }
            }
        }
        return fileName
    }

    fun getPath(context: Context, uri: Uri): String? {
        Log.e("TAG", "getPath() called with: context = $context, uri = $uri")

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            Log.e("TAG", "getPath: 11")
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                return uri.path
            } else if (isGoogleDriveUri(uri)) {
                Log.e("TAG", "getPath:getDriveFilePath " + getDriveFilePath(uri, context))
                return getDriveFilePath(uri, context).toString()
            } else if (isDownloadsDocument(uri)) {
                Log.e("TAG", "getPath: 11--")

                if (DocumentsContract.getDocumentId(uri) != null && DocumentsContract.getDocumentId(
                        uri
                    ).startsWith("msf:")
                ) {
                    val file: File = File(
                        context.cacheDir,
                        getFileNameFromUri(uri,context.contentResolver) + "."+Objects.requireNonNull(
                            context.contentResolver.getType(
                                uri
                            )
                        )?.split("/")?.get(1)
                    )
                    Log.e("TAG", "getPath: file "+file )
                    Log.e("TAG", "getPath: file "+Objects.requireNonNull(
                        context.contentResolver.getType(
                            uri
                        )) )
                    try {
                        context.contentResolver.openInputStream(uri).use { inputStream ->
                            FileOutputStream(file).use { output ->
                                val buffer = ByteArray(4 * 1024) // or other buffer size
                                var read: Int
                                while (inputStream?.read(buffer).also { read = it!! } != -1) {
                                    output.write(buffer, 0, read)
                                }
                                output.flush()
                                return file.toString()
                            }
                        }
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                    return null
                }
            } else if (isMediaDocument(uri)) {
                Log.e("TAG", "getPath: 11---")
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            Log.e("TAG", "getPath: 33")
            return uri.path
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            Log.e("TAG", "getPath: 22")

            return getRealPathFromURI(context, uri)
//            return getDataColumn(context, uri, null, null)
        }
        return null
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    private fun getDriveFilePath(uri: Uri, context: Context): File? {
        val returnUri: Uri = uri
        val returnCursor: Cursor =
            context.contentResolver.query(returnUri, null, null, null, null) ?: return null
        /*
     * Get the column indexes of the data in the Cursor,
     *     * move to the first row in the Cursor, get the data,
     *     * and display it.
     * */
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex: Int = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        val size = returnCursor.getLong(sizeIndex).toString()
        val file = File(context.cacheDir, name)
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
        } finally {
            returnCursor.close()
        }
        return file
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        Log.e(
            "TAG",
            "getDataColumn() called with: context = $context, uri = $uri, selection = $selection, selectionArgs = $selectionArgs"
        )
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getRealPathFromURI(activity: Context, contentUri: Uri): String? {
        var column_index = 0
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Audio.Media.DATA)
            cursor = activity.contentResolver.query(contentUri, proj, null, null, null)
            column_index = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)!!
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } catch (e: java.lang.IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return cursor!!.getString(column_index)
    }


}