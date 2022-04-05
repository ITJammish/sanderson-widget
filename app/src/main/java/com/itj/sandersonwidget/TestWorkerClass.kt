package com.itj.sandersonwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.itj.sandersonwidget.storage.SharedPreferencesStorage
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode

/**
 * https://medium.com/swlh/periodic-tasks-with-android-workmanager-c901dd9ba7bc
 */
class TestWorkerClass(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        private const val VC_LABEL = "vc_label"
        private const val VC_BAR = "vc_bar"
    }

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
                with(Jsoup.parseBodyFragment(response)) {
                    val projectTitles = getElementsByClass(VC_LABEL).map {
                        (it.childNode(0) as TextNode).text()
                    }
                    val projectProgress = getElementsByClass(VC_BAR).map {
                        it.attr("data-percentage-value")
                    }

                    // todo delegate mapping?
                    // zip and store data
                    projectTitles.zip(projectProgress)
                        .map { pair -> "$pair.first:$pair.second" }
                        .also { SharedPreferencesStorage(context).store(it) }
                    Log.d("JamesDebug:", "Saved stuff")

                    val intent = Intent(context.applicationContext, ProgressBars::class.java).also {
                        it.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    }
                    var ids: IntArray
                    val widgetManager = AppWidgetManager.getInstance(context).also {
                        ids = it.getAppWidgetIds(ComponentName(context, ProgressBars::class.java))
//                        it.notifyAppWidgetViewDataChanged(ids, android.R.id.list)
                    }

                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    context.sendBroadcast(intent)
                }
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
