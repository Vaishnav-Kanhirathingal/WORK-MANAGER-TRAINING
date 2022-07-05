package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI

class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        makeStatusNotification("starting the blur process", applicationContext)
        sleep()
        return try {
//            val picture = BitmapFactory.decodeResource(
//                applicationContext.resources,
//                R.drawable.android_cupcake
//            )
            val picture = BitmapFactory.decodeStream(
                applicationContext
                    .contentResolver
                    .openInputStream(Uri.parse(resourceUri))
            )
            val out = blurBitmap(picture, applicationContext)
            val uri = writeBitmapToFile(applicationContext, out)
            makeStatusNotification("uri = $uri", applicationContext)
            val outputData = workDataOf(KEY_IMAGE_URI to uri.toString())
            Result.success(outputData)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}