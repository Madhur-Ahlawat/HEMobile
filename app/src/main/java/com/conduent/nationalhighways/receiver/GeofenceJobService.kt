package com.conduent.nationalhighways.receiver

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.app.JobIntentService
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.Date
import java.util.logging.Handler

class GeofenceJobService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
    }

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(directory, "dartlogs_20_1.txt")
            if (file.exists()) {
                val fileWriter = FileWriter(file, true)
                val bufferedWriter = BufferedWriter(fileWriter)

                bufferedWriter.write("Geofence service called " + Date().toString() + "\n")
                bufferedWriter.newLine()
                bufferedWriter.close()
            }else{

            }
        }
    }
}