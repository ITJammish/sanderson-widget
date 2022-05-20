package com.itj.sandersonwidget.ui.service

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage
import com.itj.sandersonwidget.ui.helper.ThemeColors
import com.itj.sandersonwidget.ui.helper.fetchThemeColors
import com.itj.sandersonwidget.ui.helper.fetchThemeResId
import com.itj.sandersonwidget.ui.helper.setProgressBarColorCompat
import com.itj.sandersonwidget.ui.service.ProgressItemWidgetService.Companion.NUMBER_OF_ITEMS

/**
 * A service that acts as a list adapter for the progress list items.
 *
 * - [NUMBER_OF_ITEMS] extra indicates how many of the four progress items should be displayed in this list,
 *  i.e. when we show one or more progress items as a wheel; how many are left to show in this list.
 */
class ProgressItemWidgetService : RemoteViewsService() {

    companion object {
        const val NUMBER_OF_ITEMS = "number_of_items"
        private const val USE_ALL_ITEMS = 0
    }

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ProgressItemWidgetFactory(applicationContext, intent)
    }

    @SuppressLint("ResourceType")
    internal class ProgressItemWidgetFactory(
        private val context: Context,
        intent: Intent?,
    ) : RemoteViewsFactory {

        private val appWidgetId = intent?.getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID) ?: INVALID_APPWIDGET_ID
        private val numberOfItems = intent?.getIntExtra(NUMBER_OF_ITEMS, USE_ALL_ITEMS) ?: USE_ALL_ITEMS
        private val themeColors: ThemeColors

        private lateinit var data: List<ProgressItem>

        init {
            val themeResId = SharedPreferencesStorage(context).retrieveTheme(appWidgetId).fetchThemeResId()
            themeColors = fetchThemeColors(context, themeResId)
        }

        override fun onCreate() {
            // connect to data source
        }

        override fun onDataSetChanged() {
            data = SharedPreferencesStorage(context).retrieveProgressItemData()
        }

        override fun onDestroy() {
            // close any datasource connections
        }

        override fun getCount(): Int {
            return if (numberOfItems == USE_ALL_ITEMS || data.size < numberOfItems) {
                data.size
            } else {
                numberOfItems
            }
        }

        override fun getViewAt(position: Int): RemoteViews {
            val imposedPosition = if (data.size < numberOfItems) {
                position
            } else {
                position + (data.size - numberOfItems)
            }

            return RemoteViews(context.packageName, R.layout.item_view_progress).apply {
                setTextViewText(R.id.item_title, data[imposedPosition].label)
                setTextColor(R.id.item_title, themeColors.textColor)

                setProgressBar(R.id.item_progress_bar, 100, data[imposedPosition].progressPercentage.toInt(), false)
                setProgressBarColorCompat(this, themeColors.progressColor)

                setTextViewText(R.id.item_percentage, "${data[imposedPosition].progressPercentage}%")
                setTextColor(R.id.item_percentage, themeColors.textColor)
            }
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
