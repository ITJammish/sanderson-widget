package com.itj.sandersonwidget.ui.layouts

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.*
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

    // Todo duplicating default behaviour (also specified in SharedPrefs)
    private var articlesEnabled = true
    private var themeResId = R.style.Theme_SandersonWidget_AppWidgetContainer_WayOfKings

    internal fun fetchLayout(
        context: Context,
        appWidgetId: Int,
        gridSize: GridSize,
        width: Int,
        height: Int,
    ): RemoteViews {
        articlesEnabled = SharedPreferencesStorage(context).retrieveArticlesEnabled()
        themeResId = SharedPreferencesStorage(context).retrieveTheme()
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
                is Medium -> fetchMediumMediumView(context, progressItemData, appWidgetId)
                is Large -> fetchMediumLargeView(context, progressItemData, appWidgetId)
            }
            is Large -> when (gridSize.height) {
                is Small -> fetchLargeSmallView(context, progressItemData, appWidgetId, width, height)
                is Medium -> fetchLargeMediumView(context, progressItemData, appWidgetId)
                is Large -> fetchLargeLargeView(context, progressItemData, appWidgetId)
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
        width: Int,
        height: Int,
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.view_large_small).apply {
            bindProgressItem(context, progressItemData[0], R.id.item_progress_indicator_1, R.id.item_title_1)
            bindProgressItem(context, progressItemData[1], R.id.item_progress_indicator_2, R.id.item_title_2)
            bindProgressItemList(context, appWidgetId, 2)

            // todo after lunch:
            // Either set up themes and save their int to prefs on settings option change

            // also need to clean this up vv, for API level AND test on other widget sizes...

            //Not needed for later API -> todo @API:setStyledBackgroundImage()
            // or: getBackgroundDrawableId -> if(api>=31){}/else{}
            // Calculate widget dimensions
            val screenDensity = context.resources.displayMetrics.density
            val widgetWidth = (screenDensity * width).toInt()
            val widgetHeight = (screenDensity * height).toInt()

            // Decode styled scaled background image
            val backgroundAttrs = intArrayOf(R.attr.appWidgetBackgroundImage)
            // todo pull style id out of shared prefs/create a keymap for enum -> style that can be queried
            val styledBackgroundAttrs = context.obtainStyledAttributes(
                themeResId,
                backgroundAttrs
            )
            val backgroundResId = styledBackgroundAttrs.getResourceId(0, R.drawable.oathbringer)
            styledBackgroundAttrs.recycle()

            val options = BitmapFactory.Options().apply {
                inMutable = true
            }
            val result = Bitmap.createBitmap(widgetWidth, widgetHeight, Bitmap.Config.ARGB_8888)
            // todo get drawable id from user set style/theme attr
            val rawBitmap = BitmapFactory.decodeResource(context.resources, backgroundResId, options)
            val bitmap = Bitmap.createScaledBitmap(rawBitmap, widgetWidth, widgetHeight, true)
            val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            val paint = Paint().apply {
                isAntiAlias = true
                this.shader = shader
            }

            // Obtain corner radius from styles
            val attrs = intArrayOf(R.attr.appWidgetRadius)
            val styledAttr = context.obtainStyledAttributes(R.style.Theme_SandersonWidget_AppWidgetContainer, attrs)
            val radius = styledAttr.getDimension(0, 0f)
            styledAttr.recycle()

            // Cut background image corners and apply to image view
            val rect = RectF(0.0f, 0.0f, widgetWidth.toFloat(), widgetHeight.toFloat())
            Canvas(result).apply { drawRoundRect(rect, radius, radius, paint) }
            setImageViewBitmap(R.id.widget_background_view, result)

            // usable for higher APIs (31 etc)
            // todo get drawable id from user set style/theme attr
//            setImageViewResource(R.id.widget_backgroun_view, R.drawable.wayofkings)
        }
    }

    private fun fetchLargeMediumView(
        context: Context,
        progressItemData: List<ProgressItem>,
        appWidgetId: Int,
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
        val attrs = intArrayOf(R.attr.appWidgetProgressBarColor)
        val styledAttr = context.obtainStyledAttributes(
            themeResId,
            attrs,
        )
        val defaultColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(android.R.color.black)
        } else {
            context.resources.getColor(android.R.color.black)
        }
        val progressColor = styledAttr.getColor(0, defaultColor)
        styledAttr.recycle()

        setImageViewBitmap(
            imageViewResId, getCustomProgressBarBitMap(
                context = context,
                frameSize = 200,
                outlineWidth = 6,
                progressBarWidth = 30,
                progress = progressItemData.progressPercentage.toInt(),
                progressColor = progressColor,
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
        val clickArticlePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, 0, articleClickIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            // Todo test with older Android versions
            PendingIntent.getBroadcast(context, 0, articleClickIntent, 0)
        }

        setRemoteAdapter(R.id.article_stack, articleStackServiceIntent)
        setEmptyView(R.id.article_stack, R.id.article_stack_empty_view)
        setPendingIntentTemplate(R.id.article_stack, clickArticlePendingIntent)
    }
}
