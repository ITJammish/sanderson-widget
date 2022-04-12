package com.itj.sandersonwidget.network

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

/**
 * A [Worker] that uses [Volley] to fetch the HTML content of Brandon Sanderson's websites
 * homepage and delegates response processing to [WebScraperResponseHandler].
 *
 * https://medium.com/swlh/periodic-tasks-with-android-workmanager-c901dd9ba7bc
 * https://developer.android.com/training/volley/simple
 */
class WebScraperWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        private const val TARGET_PAGE_URL = "https://www.brandonsanderson.com/"
    }

    override fun doWork(): Result {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        StringRequest(
            Request.Method.GET, TARGET_PAGE_URL,
            { response -> WebScraperResponseHandler(context).handleWebScrapedResponse(response) },
            {
                // TODO add real logging/error handling
                Log.d("JamesDebug:", "ERROR: $it")
            },
        ).also {
            // Add the request to the RequestQueue.
            queue.add(it)
        }

        return Result.success()
    }
}
