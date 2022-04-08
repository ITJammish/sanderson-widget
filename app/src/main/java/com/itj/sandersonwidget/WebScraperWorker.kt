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
import com.itj.sandersonwidget.domain.Article
import com.itj.sandersonwidget.domain.ProgressItem
import com.itj.sandersonwidget.storage.SharedPreferencesStorage
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode

/**
 * https://medium.com/swlh/periodic-tasks-with-android-workmanager-c901dd9ba7bc
 */
// TODO UnitTest
class WebScraperWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        private const val ARTICLE_COUNT = 5

        private const val TARGET_PAGE_URL = "https://www.brandonsanderson.com/"
        private const val VC_LABEL = "vc_label"
        private const val VC_BAR = "vc_bar"
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

                    // todo delegate mapping TODO AFTER LUNCH
                    // Process progress bar data
                    val projectTitles = getElementsByClass(VC_LABEL).map {
                        (it.childNode(0) as TextNode).text().trim()
                    }
                    val projectProgress = getElementsByClass(VC_BAR).map {
                        it.attr("data-percentage-value").trim()
                    }

                    // zip and store
                    projectTitles.zip(projectProgress)
                        .map { pair -> ProgressItem(label = pair.first, progressPercentage = pair.second) }
                        .also { SharedPreferencesStorage(context).storeProgressItemData(it) }


                    // Process article data
                    val postNames = getElementsByClass("entry-title").select("a")
                        .take(ARTICLE_COUNT)
                        .map {
                            Pair(it.text(), it.attr("href"))
                        }
                    val thumbnailUrls = getElementsByClass("blog-thumb-lazy-load preload-me lazy-load")
                        .take(ARTICLE_COUNT)
                        .map {
                            it.attr("data-srcset").split(",")[0].split(" ")[0]
                        }

                    // zip and store
                    postNames.zip(thumbnailUrls)
                        .map { pair -> Article(pair.first.first, pair.first.second, pair.second) }
                        .also { SharedPreferencesStorage(context).storeArticleData(it) }


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
