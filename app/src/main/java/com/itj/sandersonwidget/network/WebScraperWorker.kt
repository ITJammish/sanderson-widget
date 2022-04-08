package com.itj.sandersonwidget.network

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
import com.itj.sandersonwidget.ProgressBarsWidgetProvider
import com.itj.sandersonwidget.storage.SharedPreferencesStorage
import org.jsoup.Jsoup

/**
 * https://medium.com/swlh/periodic-tasks-with-android-workmanager-c901dd9ba7bc
 */
// TODO UnitTest
class WebScraperWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        private const val TARGET_PAGE_URL = "https://www.brandonsanderson.com/"
        private const val ARTICLE_COUNT = 5
    }

    override fun doWork(): Result {
        Log.d("JamesDebug:", "doWork() - Start")

        // from: https://developer.android.com/training/volley/simple
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, TARGET_PAGE_URL,
            { response ->
                with(Jsoup.parseBodyFragment(response)) {

                    JsonParser().also {
                        // Process and store progress bar data
                        it.parseProjectProgress(this).also { progressItems ->
                            SharedPreferencesStorage(context).storeProgressItemData(progressItems)
                        }
                        // Process and store article data
                        it.parseArticles(this, ARTICLE_COUNT).also { articles ->
                            SharedPreferencesStorage(context).storeArticleData(articles) }
                    }


                    // Prompt widget update
                    val intent = Intent(context.applicationContext, ProgressBarsWidgetProvider::class.java).also {
                        it.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    }
                    var ids: IntArray
                    AppWidgetManager.getInstance(context).also {
                        ids = it.getAppWidgetIds(ComponentName(context, ProgressBarsWidgetProvider::class.java))
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
