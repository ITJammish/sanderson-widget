package com.itj.sandersonwidget.domain

import android.appwidget.AppWidgetManager
import android.content.Context
import com.itj.sandersonwidget.domain.storage.NotificationTriggeringStorage
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage
import com.itj.sandersonwidget.domain.storage.Storage
import com.itj.sandersonwidget.utils.ComponentNameFetcher
import com.itj.sandersonwidget.utils.ComponentNameFetcherImpl
import com.itj.sandersonwidget.utils.IntentProvider
import com.itj.sandersonwidget.utils.IntentProviderImpl
import org.jsoup.Jsoup

/**
 * Coordinates/delegates post-network processes:
 *  - delegates parsing of response body
 *  - delegates storage of parsed data
 *  - broadcasts intent to prompt widget update
 */
class WebScraperResponseHandler(
    private val context: Context,
    private val store: Storage = NotificationTriggeringStorage(context, SharedPreferencesStorage(context)),
    private val htmlParser: HTMLParser = HTMLParser(),
    private val intentProvider: IntentProvider = IntentProviderImpl(),
    private val componentNameFetcher: ComponentNameFetcher = ComponentNameFetcherImpl(),
) {

    companion object {
        internal const val ARTICLE_COUNT = 20
    }

    fun handleWebScrapedResponse(response: String) {
        with(Jsoup.parseBodyFragment(response)) {
            // ParseHtml
            htmlParser.also {
                // Process and store progress bar data
                it.parseProjectProgress(this).also { progressItems ->
                    store.storeProgressItemData(progressItems)
                }
                // Process and store article data
                it.parseArticles(this, ARTICLE_COUNT).also { articles ->
                    store.storeArticleData(articles)
                }
            }

            // Push Intent to prompt widget update
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                componentNameFetcher.fetchProgressBarsWidgetProviderComponentName(context)
            )
            // Intent.putExtra returns an intent, but the initial val reference only holds the initial state
            // so we need to reassign the return putExtra return value to a new val
            val updateWidgetIntent = intentProvider.fetchUpdateWidgetIntent(context)
            val intentWithExtras = updateWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intentWithExtras)
        }
    }
}
