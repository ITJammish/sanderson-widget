package com.itj.sandersonwidget

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class TestWorkerClass(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("JamesDebug:", "doWork() - Start")

        // from: https://developer.android.com/training/volley/simple
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url = "https://www.brandonsanderson.com/"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.d("JamesDebug:", response)
            },
            {
                Log.d("JamesDebug:", "ERROR: $it")
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
        Log.d("JamesDebug:", "doWork() - End")

        return Result.success()
    }
}
