package com.itj.sandersonwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.*
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.work.*
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage
import com.itj.sandersonwidget.network.WebScraperWorker
import com.itj.sandersonwidget.ui.DimensionSize.Small
import com.itj.sandersonwidget.ui.GridSize
import com.itj.sandersonwidget.ui.getGridSizePortrait
import com.itj.sandersonwidget.ui.layouts.LayoutProvider
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
//        for (appWidgetId in appWidgetIds) {
        // todo this is broken and removing data before it can be used
//            SharedPreferencesStorage(context).clearAll()
//        }
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

    // Link with suggestions for catching widget resize events (Default doesn't work on Samsung apparently -_-)
    // https://stackoverflow.com/questions/17396045/how-to-catch-widget-size-changes-on-devices-where-onappwidgetoptionschanged-not
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        // todo only for api <31
        newOptions?.let {
            val newMinWidth = it.get(OPTION_APPWIDGET_MIN_WIDTH) as Int? ?: -1
            val newMinHeight = it.get(OPTION_APPWIDGET_MIN_HEIGHT) as Int? ?: -1
            val newMaxHeight = it.get(OPTION_APPWIDGET_MAX_HEIGHT) as Int? ?: -1
            val gridSize = getGridSizePortrait(newMinWidth, newMinHeight, newMaxHeight)
            if (newMinWidth != -1 && newMinWidth != -1) {
                // todo could need maxWidth/minHeight for portrait mode
                updateAppWidget(context, appWidgetManager, appWidgetId, gridSize, newMinWidth, newMaxHeight)
            }
        }
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    gridSize: GridSize = GridSize(Small, Small),
    width: Int = 50,
    height: Int = 50,
) {
    val views = LayoutProvider().fetchLayout(context, appWidgetId, gridSize, width, height)

    // Instruct the widget manager to update the widget
    with(appWidgetManager) {
        updateAppWidget(appWidgetId, views)
        // TODO not sure if notify methods are required if we're calling a full refresh: https://developer.android.com/guide/topics/appwidgets/advanced
//        notifyAppWidgetViewDataChanged(appWidgetId, R.id.progress_item_list)
//        notifyAppWidgetViewDataChanged(appWidgetId, R.id.article_stack)
    }
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
