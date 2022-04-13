package com.itj.sandersonwidget.ui.layouts

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import com.itj.sandersonwidget.ProgressBarsWidgetProvider
import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage
import com.itj.sandersonwidget.ui.ArticleListWidgetService
import com.itj.sandersonwidget.ui.DimensionSize.*
import com.itj.sandersonwidget.ui.GridSize
import com.itj.sandersonwidget.ui.ProgressItemWidgetService
import com.itj.sandersonwidget.ui.ProgressItemWidgetService.Companion.NUMBER_OF_ITEMS
import com.itj.sandersonwidget.ui.getCustomProgressBarBitMap

// todo for api>=31
//    val smallView = RemoteViews(context.packageName, R.layout.view_small)
//    val mediumView = RemoteViews(context.packageName, R.layout.view_medium)
//    val largeView = RemoteViews(context.packageName, R.layout.view_large)
//
//    val viewMapping: Map<SizeF, RemoteViews> = mapOf(
//        SizeF(150f, 100f) to smallView,
//        SizeF(150f, 200f) to mediumView,
//        SizeF(215f, 100f) to largeView,
//    )
//
//    val remoteViews = RemoteViews(viewMapping)
//    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
/**
 * https://developer.android.com/guide/topics/appwidgets/layouts
 */
class LayoutProvider {

    internal fun fetchLayout(
        context: Context,
        appWidgetId: Int,
        gridSize: GridSize,
    ): RemoteViews {
        val articlesEnabled = SharedPreferencesStorage(context).retrieveArticlesEnabled()
        val progressItemData = SharedPreferencesStorage(context).retrieveProgressItemData()
        if (progressItemData.isEmpty()) {
            return RemoteViews(context.packageName, R.layout.view_loading)
        }

        return when (gridSize.width) {
            is Small -> when (gridSize.height) {
                is Small -> fetchSmallSmallView(context, progressItemData)
                is Medium -> fetchSmallMediumView(context, progressItemData, appWidgetId)
                is Large -> fetchSmallLargeView(context, progressItemData, appWidgetId)
            }
            is Medium -> when (gridSize.height) {
                is Small -> fetchMediumSmallView(context, progressItemData, appWidgetId)
                is Medium -> fetchMediumMediumView(context, progressItemData, appWidgetId, articlesEnabled)
                is Large -> fetchMediumLargeView(context, progressItemData, appWidgetId, articlesEnabled)
            }
            is Large -> when (gridSize.height) {
                is Small -> fetchLargeSmallView(context, progressItemData, appWidgetId)
                is Medium -> fetchLargeMediumView(context, progressItemData, appWidgetId, articlesEnabled)
                is Large -> fetchLargeLargeView(context, progressItemData, appWidgetId, articlesEnabled)
            }
        }
//        return oldViews(context, appWidgetId)
    }

    // Todo scrollable list, not just first item
    private fun fetchSmallSmallView(context: Context, progressItemData: List<ProgressItem>): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_small_small).apply {
            bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator, R.id.item_title)
        }
    }

    private fun fetchSmallMediumView(
        context: Context,
        progressItemData: List<ProgressItem>,
        appWidgetId: Int,
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_small_medium).apply {
            bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator_1, R.id.item_title_1)
            bindProgressItemList(context, appWidgetId, 3)
        }
    }

    private fun fetchSmallLargeView(
        context: Context,
        progressItemData: List<ProgressItem>,
        appWidgetId: Int,
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_small_large).apply {
            bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator_1, R.id.item_title_1)
            bindProgressItem(context, progressItemData[1], R.id.item_progress_indicator_2, R.id.item_title_2)
            bindProgressItemList(context, appWidgetId, 2)
        }
    }

    private fun fetchMediumSmallView(
        context: Context,
        progressItemData: List<ProgressItem>,
        appWidgetId: Int,
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_medium_small).apply {
            bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator_1, R.id.item_title_1)
            bindProgressItemList(context, appWidgetId, 3)
        }
    }

    private fun fetchMediumMediumView(
        context: Context,
        progressItemData: List<ProgressItem>,
        appWidgetId: Int,
        articlesEnabled: Boolean
    ): RemoteViews {
        return if (articlesEnabled) {
            RemoteViews(context.packageName, R.layout.view_medium_medium_with_articles).apply {
                bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator_1, R.id.item_title_1)
                bindProgressItemList(context, appWidgetId, 3)
                bindArticleStack(context, appWidgetId)
            }
        } else {
            fetchFourByFourView(context, progressItemData)
        }
    }

    private fun fetchMediumLargeView(
        context: Context,
        progressItemData: List<ProgressItem>,
        appWidgetId: Int,
        articlesEnabled: Boolean
    ): RemoteViews {
        return if (articlesEnabled) {
            RemoteViews(context.packageName, R.layout.view_medium_medium_with_articles).apply {
                bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator_1, R.id.item_title_1)
                bindProgressItemList(context, appWidgetId, 3)
                bindArticleStack(context, appWidgetId)
            }
        } else {
            fetchFourByFourView(context, progressItemData)
        }
    }

    private fun fetchLargeSmallView(
        context: Context,
        progressItemData: List<ProgressItem>,
        appWidgetId: Int,
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_large_small).apply {
            bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator_1, R.id.item_title_1)
            bindProgressItem(context, progressItemData[1], R.id.item_progress_indicator_2, R.id.item_title_2)
            bindProgressItemList(context, appWidgetId, 2)
        }
    }

    private fun fetchLargeMediumView(
        context: Context,
        progressItemData: List<ProgressItem>,
        appWidgetId: Int,
        articlesEnabled: Boolean
    ): RemoteViews {
        return if (articlesEnabled) {
            RemoteViews(context.packageName, R.layout.view_large_medium_with_articles).apply {
                bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator_1, R.id.item_title_1)
                bindProgressItem(context, progressItemData[1], R.id.item_progress_indicator_2, R.id.item_title_2)
                bindProgressItemList(context, appWidgetId, 2)
                bindArticleStack(context, appWidgetId)
            }
        } else {
            fetchFourByFourView(context, progressItemData)
        }
    }

    private fun fetchLargeLargeView(
        context: Context,
        progressItemData: List<ProgressItem>,
        appWidgetId: Int,
        articlesEnabled: Boolean
    ): RemoteViews {
        return if (articlesEnabled) {
            RemoteViews(context.packageName, R.layout.view_large_medium_with_articles).apply {
                bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator_1, R.id.item_title_1)
                bindProgressItem(context, progressItemData[1], R.id.item_progress_indicator_2, R.id.item_title_2)
                bindProgressItemList(context, appWidgetId, 2)
                bindArticleStack(context, appWidgetId)
            }
        } else {
            fetchFourByFourView(context, progressItemData)
        }
    }

    // -- Fetch common layout methods --
    private fun fetchFourByFourView(context: Context, progressItemData: List<ProgressItem>): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_medium_medium).apply {
            bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator_1, R.id.item_title_1)
            bindProgressItem(context, progressItemData[1], R.id.item_progress_indicator_2, R.id.item_title_2)
            bindProgressItem(context, progressItemData[2], R.id.item_progress_indicator_3, R.id.item_title_3)
            bindProgressItem(context, progressItemData[3], R.id.item_progress_indicator_4, R.id.item_title_4)
        }
    }

    // -- Binding methods --
    private fun RemoteViews.bindProgressItem(
        context: Context,
        progressItemData: ProgressItem,
        imageViewResId: Int,
        titleResId: Int
    ) {
        setImageViewBitmap(
            imageViewResId, getCustomProgressBarBitMap(
                context = context,
                frameSize = 200,
                outlineWidth = 6,
                progressBarWidth = 30,
                progress = progressItemData.progressPercentage.toInt()
            )
        )
        setTextViewText(titleResId, progressItemData.label)
    }

    private fun RemoteViews.bindProgressItemList(
        context: Context,
        appWidgetId: Int,
        numberOfItems: Int,
    ) {
        // Create and set progress list item adapter intent
        val progressItemServiceIntent = Intent(context, ProgressItemWidgetService::class.java).also {
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            it.putExtra(NUMBER_OF_ITEMS, numberOfItems)
            it.data = Uri.parse(it.toUri(Intent.URI_INTENT_SCHEME))
        }

        setRemoteAdapter(R.id.progress_item_list, progressItemServiceIntent)
        setEmptyView(R.id.progress_item_list, R.id.progress_list_empty_view)
    }

    private fun RemoteViews.bindArticleStack(
        context: Context,
        appWidgetId: Int,
    ) {
        // Create and set article stack adapter intent
        val articleStackServiceIntent = Intent(context, ArticleListWidgetService::class.java).also {
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            it.data = Uri.parse(it.toUri(Intent.URI_INTENT_SCHEME))
        }
        val articleClickIntent = Intent(context, ProgressBarsWidgetProvider::class.java).apply {
            action = ProgressBarsWidgetProvider.ACTION_ARTICLE_CLICK
        }
        // Todo extract method to handle SDK checks more cleanly
        val clickArticlePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(context, 0, articleClickIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            // Todo test with older Android versions
            PendingIntent.getBroadcast(context, 0, articleClickIntent, 0)
        }

        setRemoteAdapter(R.id.article_stack, articleStackServiceIntent)
        setEmptyView(R.id.article_stack, R.id.article_stack_empty_view)
        setPendingIntentTemplate(R.id.article_stack, clickArticlePendingIntent)

    }

    @Deprecated(message = "Moving away from singular scalable layout.")
    private fun oldViews(context: Context, appWidgetId: Int): RemoteViews {
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
            action = ProgressBarsWidgetProvider.ACTION_ARTICLE_CLICK
        }
        // Todo extract method to handle SDK checks more cleanly
        val clickArticlePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(context, 0, articleClickIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            // Todo test with older Android versions
            PendingIntent.getBroadcast(context, 0, articleClickIntent, 0)
        }
        views.apply {
            setRemoteAdapter(R.id.article_stack, articleStackServiceIntent)
            setEmptyView(R.id.article_stack, R.id.article_stack_empty_view)
            setPendingIntentTemplate(R.id.article_stack, clickArticlePendingIntent)
        }

        // Spiked custom progress view
        views.setImageViewBitmap(
            R.id.test_image, getCustomProgressBarBitMap(
                context = context,
                frameSize = 400,
                outlineWidth = 6,
                progressBarWidth = 30,
                progress = 29
            )
        )

        return views
    }
}
