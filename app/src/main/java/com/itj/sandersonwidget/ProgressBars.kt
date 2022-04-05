package com.itj.sandersonwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 *  TODO NEXT:
 *  - store data in prefs -> TestWorkerClass (rename this)
 *  - when stored trigger AppWidgetManager.ACTION_APPWIDGET_UPDATE broadcast to kick this.onUpdate
 *  - pull data from prefs here in updateAppWidget so it pulls data for every update (and we don't have to throw data
 *  around as extras)
 *  - Clean everything up! Layers, single responsibility classes etc!
 */

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [ProgressBarsConfigureActivity]
 */
class ProgressBars : AppWidgetProvider() {

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
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val data = loadProgressData(context)
    Log.d("JamesDebug:", "updateAppWidget: $data")

    // todo get data
    val widgetText = loadTitlePref(context, appWidgetId)
    // Construct the RemoteViews object
    // todo views for progress bars
    val views = RemoteViews(context.packageName, R.layout.progress_bars)
    views.setTextViewText(R.id.appwidget_text, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun startWorkRequest(context: Context) {
    val workRequestTag = "WORK_REQUEST"

    val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    val periodicWorkRequest =
        PeriodicWorkRequestBuilder<TestWorkerClass>(15, TimeUnit.MINUTES)
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
