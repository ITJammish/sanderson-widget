package com.itj.sandersonwidget.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.toolbox.Volley
import com.itj.sandersonwidget.domain.WebScraperResponseHandler

/**
 * A [Worker] that uses [Volley] to fetch the HTML content of Brandon Sanderson's websites
 * homepage and delegates response processing to [WebScraperResponseHandler].
 *
 * https://medium.com/swlh/periodic-tasks-with-android-workmanager-c901dd9ba7bc
 * https://developer.android.com/training/volley/simple
 */
class WebScraperWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        getStringRequest(context).also {
            // Add the request to the RequestQueue.
            queue.add(it)
        }

        return Result.success()
    }
}
