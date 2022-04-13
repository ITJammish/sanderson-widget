package com.itj.sandersonwidget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.work.*
import com.itj.sandersonwidget.ProgressBarsWidgetProvider.Companion.ACTION_ARTICLE_CLICK
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage
import com.itj.sandersonwidget.network.WebScraperWorker
import com.itj.sandersonwidget.ui.ArticleListWidgetService
import com.itj.sandersonwidget.ui.ProgressItemWidgetService
import java.util.concurrent.TimeUnit

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [ProgressBarsConfigureActivity]
 */
class ProgressBarsWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_ARTICLE_CLICK = "com.itj.sandersonwidget.actionArticleClick"
        const val EXTRA_ARTICLE_POSITION = "extra_article_position"
        const val INVALID_ARTICLE_POSITION = -1
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("JamesDebug:", "onUpdate with ${appWidgetIds[0]}")
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // TODO if multiple widgets placed we'll have duplicate Work... any way to make this nicer?
        //  Probably should save data against sharedPref key+widgetId, and clean this up when onDisabled
        //  That way, while we have duplicate datasets, we aren't spinning up multiple Work and overwriting
        //  the same data.
        // Enter relevant functionality for when the first widget is created
        startWorkRequest(context)
        Log.d("JamesDebug:", "onEnabled")
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        cancelWorkRequest(context)
        // todo clear shared prefs data?
        Log.d("JamesDebug:", "onDisabled")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (ACTION_ARTICLE_CLICK == intent?.action) {
            val clickedPosition = intent.getIntExtra(EXTRA_ARTICLE_POSITION, INVALID_ARTICLE_POSITION)
            if (clickedPosition != INVALID_ARTICLE_POSITION) {
                context?.let {
                    val article = SharedPreferencesStorage(it).retrieveArticleData()[clickedPosition]
                    val browserLaunchIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(article.articleUrl)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    it.startActivity(browserLaunchIntent)
                }
            }
        }
        super.onReceive(context, intent)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // TODO read config and inflate different widget layouts (sans articles etc)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.widget_progress_bars)

    // Create and set progress list item adapter intent
    val progressItemServiceIntent = Intent(context, ProgressItemWidgetService::class.java).also {
        it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        it.data = Uri.parse(it.toUri(Intent.URI_INTENT_SCHEME))
    }
    views.apply {
        setRemoteAdapter(R.id.progress_item_list, progressItemServiceIntent)
        setEmptyView(R.id.progress_item_list, R.id.progress_list_empty_view)
    }

    // Create and set article stack adapter intent
    val articleStackServiceIntent = Intent(context, ArticleListWidgetService::class.java).also {
        it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        it.data = Uri.parse(it.toUri(Intent.URI_INTENT_SCHEME))
    }
    val articleClickIntent = Intent(context, ProgressBarsWidgetProvider::class.java).apply {
        action = ACTION_ARTICLE_CLICK
    }
    // Todo extract method to handle SDK checks more cleanly
    val clickArticlePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getBroadcast(context, 0, articleClickIntent, FLAG_MUTABLE)
    } else {
        // Todo test with older Android versions
        PendingIntent.getBroadcast(context, 0, articleClickIntent, 0)
    }
    views.apply {
        setRemoteAdapter(R.id.article_stack, articleStackServiceIntent)
        setEmptyView(R.id.article_stack, R.id.article_stack_empty_view)
        setPendingIntentTemplate(R.id.article_stack, clickArticlePendingIntent)
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun startWorkRequest(context: Context) {
    val workRequestTag = "WORK_REQUEST"
    val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    val periodicWorkRequest =
        PeriodicWorkRequestBuilder<WebScraperWorker>(15, TimeUnit.MINUTES)
            .addTag(workRequestTag)
            .setConstraints(constraints)
            .build()

    WorkManager
        .getInstance(context)
        .enqueueUniquePeriodicWork(workRequestTag, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest)
}

internal fun cancelWorkRequest(context: Context) {
    WorkManager.getInstance(context).cancelAllWork()
}
