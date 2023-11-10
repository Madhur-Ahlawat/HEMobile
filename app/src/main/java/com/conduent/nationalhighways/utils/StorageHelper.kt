package com.conduent.nationalhighways.utils

import android.app.Activity
import android.os.Environment
import com.conduent.nationalhighways.utils.common.Constants
import okhttp3.ResponseBody
import java.io.*

object StorageHelper {



    /*fun checkStoragePermissions(context: Context): Boolean {
        var ret = true
        if (SDK_INT >= Build.VERSION_CODES.R) {
            ret = Environment.isExternalStorageManager()
        } else if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ret = false
        }
        return ret
    }

    fun requestStoragePermission(
        context: Context,
        onScopeResultLaucher: ActivityResultLauncher<Intent>,
        onPermissionlaucher: ActivityResultLauncher<Array<String>>
    ) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            var intent: Intent
            try {
                intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", context.applicationContext.packageName))

            } catch (e: Exception) {
                intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            }

            onScopeResultLaucher.launch(intent)
        } else {
            onPermissionlaucher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
*/
    fun writeResponseBodyToDisk(
        activity: Activity,
        selectionType: String,
        body: ResponseBody
    ): Boolean {
        val fileExtension =
            if (selectionType == Constants.PDF) Constants.PDF_EXTENSION else Constants.CSV_EXTENSION
        try {
//            val filePath ="${activity.getExternalFilesDir(null)}${File.separator}${
//                    System.currentTimeMillis()
//                }$fileExtension"
//            val currentFile = File(filePath)

            val filePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).path + "/" + System.currentTimeMillis().toString() + fileExtension
            val currentFile = File(
                filePath
            )
            if (!currentFile.exists())
                currentFile.parentFile?.mkdirs()

            return if (selectionType == "pdf") {
                savePdf(body, currentFile)
            } else {
                saveSpreadSheet(body, filePath)
            }
        } catch (e: Exception) {
            return false
        }

    }

    private fun savePdf(body: ResponseBody, futurePdfFile: File): Boolean {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val pdfReader = ByteArray(4096)
            val fileSize = body.contentLength()
            var fileSizeDownloaded: Long = 0
            inputStream = body.byteStream()
            outputStream = FileOutputStream(futurePdfFile)

            while (true) {
                val read = inputStream.read(pdfReader)
                if (read == -1) {
                    break
                }
                outputStream.write(pdfReader, 0, read)
                fileSizeDownloaded += read
            }
            outputStream.flush()
            return true
        } catch (e: IOException) {
            return false
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    private fun saveSpreadSheet(body: ResponseBody, pathToSaveFile: String): Boolean {
        val input = body.byteStream()

        try {
            val fos = FileOutputStream(pathToSaveFile)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also {
                        read = it
                    }
                    != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return true
        } catch (e: Exception) {
            return false
        } finally {
            input.close()
        }
    }
/*
    fun createFile(context: Context, format:String,) : File {
        var path=""
        val capture_dir: String = context.getExternalFilesDir("")?.absolutePath + "/"+ FOLDER_NAME  +"/"
        val file = File(capture_dir,System.currentTimeMillis() + format)
        if (!file.exists()) {
            file.mkdirs()}
        val fos =  FileOutputStream(file);//Get OutputStream for NewFile Location
        InputStream is = c.getInputStream();//Get InputStream for connection
        byte[] buffer = new byte[1024];//Set buffer type
        int len1 = 0;//init length
        while ((len1 = is.read(buffer)) != -1) {
            fos.write(buffer, 0, len1);//Write new file
        }
        fos.close();
        is.close



        return path
    }
*/
}