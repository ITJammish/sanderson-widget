package com.itj.sandersonwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.*
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_CONFIGURATION_CHANGED
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.work.*
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage
import com.itj.sandersonwidget.network.WebScraperWorker
import com.itj.sandersonwidget.ui.helper.DimensionSize.Small
import com.itj.sandersonwidget.ui.helper.GridSize
import com.itj.sandersonwidget.ui.helper.getGridSizePortrait
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
        private const val INVALID_WIDGET_DIMENSION = -1
    }

//    private val widgetViewMap: MutableMap<Int, Pair<RemoteViews, RemoteViews>> = mutableMapOf()
    private var appWidgetManager: AppWidgetManager? = null

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("JamesDebug:", "onUpdate with ${appWidgetIds[0]}")
        this.appWidgetManager = appWidgetManager
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
//        for (appWidgetId in appWidgetIds) {
//            widgetViewMap.remove(appWidgetId)
//        }
//        if (widgetViewMap.isEmpty()) {
//            appWidgetManager = null
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
        when (intent?.action) {
            ACTION_ARTICLE_CLICK -> handleArticleClick(context, intent)
            /**
             * We can't monitor this intent action from AppWidgetProvider, and introducing a regular Broadcast Receiver
             * is not advised. It's suggested that Launcher applications should be responsible for triggering
             * configuration changes in widget:
             * https://stackoverflow.com/questions/2435548/how-to-detect-orientation-change-in-home-screen-widget#:~:text=Since%20Android%20API%2016%2C%20there,min%2Fmax%20width%2Fheight.&text=When%20this%20is%20called%2C%20one,have%20to%20get%20orientation%20information.
             */
//            ACTION_CONFIGURATION_CHANGED -> handleConfigurationChange(context)
        }

        super.onReceive(context, intent)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        newOptions?.let {
            val minWidth = it.get(OPTION_APPWIDGET_MIN_WIDTH) as Int? ?: INVALID_WIDGET_DIMENSION
            val maxWidth = it.get(OPTION_APPWIDGET_MAX_WIDTH) as Int? ?: INVALID_WIDGET_DIMENSION
            val minHeight = it.get(OPTION_APPWIDGET_MIN_HEIGHT) as Int? ?: INVALID_WIDGET_DIMENSION
            val maxHeight = it.get(OPTION_APPWIDGET_MAX_HEIGHT) as Int? ?: INVALID_WIDGET_DIMENSION

            val gridSize = getGridSizePortrait(minWidth, minHeight, maxHeight)

            if (minWidth != INVALID_WIDGET_DIMENSION) {
                val portraitLayout = LayoutProvider().fetchLayout(context, appWidgetId, gridSize, minWidth, maxHeight)
                val landscapeLayout = LayoutProvider().fetchLayout(context, appWidgetId, gridSize, maxWidth, minHeight)
//                widgetViewMap[appWidgetId] = Pair(portraitLayout, landscapeLayout)

                val orientation = context.resources.configuration.orientation
                if (orientation == ORIENTATION_PORTRAIT) {
                    updateAppWidget(appWidgetManager, appWidgetId, portraitLayout)
                } else {
                    updateAppWidget(appWidgetManager, appWidgetId, landscapeLayout)
                }
            }
        }
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    private fun handleArticleClick(context: Context?, intent: Intent) {
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

//    private fun handleConfigurationChange(context: Context?) {
//        context?.let {
//            val orientation = it.resources.configuration.orientation
//            appWidgetManager?.let { appWidgetManager ->
//                for (appWidgetId in widgetViewMap.keys) {
//                    if (orientation == ORIENTATION_PORTRAIT) {
//                        widgetViewMap[appWidgetId]?.first
//                    } else {
//                        widgetViewMap[appWidgetId]?.second
//                    }?.let { views ->
//                        updateAppWidget(appWidgetManager, appWidgetId, views)
//                    }
//                }
//            }
//        }
//    }
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
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun updateAppWidget(
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    views: RemoteViews,
) {
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
