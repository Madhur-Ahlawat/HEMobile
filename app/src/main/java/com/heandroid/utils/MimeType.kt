package com.heandroid.utils

import java.io.File
import java.lang.Exception
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object MimeType {

    fun selectMimeType(file : File) : String {
        var mimeType = ""
        if(file.name.contains("jpg", true) || file.name.contains("png")
            || file.name.contains("jpeg") || file.name.contains("bmp") || file.name.contains("tiff")){
            mimeType = "image/" + file.name.split(".")[1]
        } else if (file.name.contains("csv", true)) {
            mimeType = "text/csv"

        }  else if (file.name.contains("doc", true)) {
            mimeType = "application/msword"

        }  else if (file.name.contains("docx", true)) {
            mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

        } else if (file.name.contains("xls", true)) {
            mimeType = "application/vnd.ms-excel"

        } else if (file.name.contains("xlsx", true)) {
            mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        }
        return mimeType
    }

}