package com.itj.sandersonwidget.ui.service

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage
import com.itj.sandersonwidget.ui.helper.ThemeColors
import com.itj.sandersonwidget.ui.helper.fetchThemeColors
import com.itj.sandersonwidget.ui.helper.fetchThemeResId
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * https://www.youtube.com/watch?v=MMiuy9jK6X8&list=PLrnPJCHvNZuDCoET8jL2VK4YVRNhVEy0K&index=4
 */
// Number of items: uses items from the back of the list in cases where preceding items are shown with a full progress
// widget
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
            /**
             * 2022-05-03 14:32:40.184 8952-8964/com.itj.sandersonwidget E/AndroidRuntime: FATAL EXCEPTION: Binder:8952_1
            Process: com.itj.sandersonwidget, PID: 8952
            kotlin.UninitializedPropertyAccessException: lateinit property data has not been initialized
            at com.itj.sandersonwidget.ui.service.ProgressItemWidgetService$ProgressItemWidgetFactory.getCount(ProgressItemWidgetService.kt:43)
            at android.widget.RemoteViewsService$RemoteViewsFactoryAdapter.getCount(RemoteViewsService.java:154)
            at com.android.internal.widget.IRemoteViewsFactory$Stub.onTransact(IRemoteViewsFactory.java:75)
            at android.os.Binder.execTransact(Binder.java:565)
            2022-05-03 14:32:40.212 1346-1369/? E/SurfaceFlinger: ro.sf.lcd_density must be defined as a build property
             */
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
                setProgressBarColor(this)

                setTextViewText(R.id.item_percentage, "${data[imposedPosition].progressPercentage}%")
                setTextColor(R.id.item_percentage, themeColors.textColor)
            }
        }

        private fun setProgressBarColor(
            remoteViews: RemoteViews,
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                remoteViews.setColorStateList(
                    R.id.item_progress_bar,
                    "setProgressTintList",
                    ColorStateList.valueOf(themeColors.progressColor),
                )
            } else {
                // Use manual reflection
                var setTintMethod: Method? = null
                try {
                    setTintMethod =
                        RemoteViews::class.java.getMethod(
                            "setProgressTintList",
                            Int::class.javaPrimitiveType,
                            ColorStateList::class.java,
                        )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                }
                if (setTintMethod != null) {
                    try {
                        setTintMethod.invoke(
                            remoteViews,
                            R.id.item_progress_bar,
                            ColorStateList.valueOf(themeColors.progressColor),
                        )
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                    }
                }
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
