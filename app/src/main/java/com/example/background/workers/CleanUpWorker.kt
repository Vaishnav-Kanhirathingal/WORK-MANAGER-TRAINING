package com.example.background.workers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import java.io.File

private const val TAG = "CleanUpWorker"

class CleanUpWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        makeStatusNotification("cleaning up files", applicationContext)
        sleep()
        return try {
            val outputDirectory = File(Environment.getExternalStorageDirectory(), OUTPUT_PATH)
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs() // should succeed
            }
            for (i in outputDirectory.listFiles()) {
                val d = i.delete()
                Log.d(TAG, "file ${i.name} deleted = $d")
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}