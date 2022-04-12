package com.itj.sandersonwidget.ui

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage

/**
 * https://www.youtube.com/watch?v=MMiuy9jK6X8&list=PLrnPJCHvNZuDCoET8jL2VK4YVRNhVEy0K&index=4
 */
class ProgressItemWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ProgressItemWidgetFactory(applicationContext, intent)
    }

    internal class ProgressItemWidgetFactory(
        private val context: Context,
        intent: Intent?
    ) : RemoteViewsFactory {
        private val appWidgetId = intent?.getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID) ?: INVALID_APPWIDGET_ID
        private lateinit var data: List<ProgressItem>

        override fun onCreate() {
            // connect to data source - fetch data from storage (MAIN THREAD) (only grab cache, don't make network)
            data = SharedPreferencesStorage(context).retrieveProgressItemData()
        }

        override fun onDataSetChanged() {
            // NI
        }

        override fun onDestroy() {
            // close any datasource connections
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            val item = RemoteViews(context.packageName, R.layout.view_progress_item).also {
                with(it) {
                    setTextViewText(R.id.item_title, data[position].label)
                    setProgressBar(R.id.item_progress_bar, 100, data[position].progressPercentage.toInt(), false)
                    setTextViewText(R.id.item_percentage, "${data[position].progressPercentage}%")
                }
            }
            return item
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }
    }
}
