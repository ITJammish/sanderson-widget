package com.itj.sandersonwidget.domain.storage

import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.domain.model.WidgetLayoutConfig

interface Storage {

    companion object {
        internal const val INVALID_INT = -1
        internal const val DEFAULT_ARTICLES_ENABLED = true
        internal const val DEFAULT_PROGRESS_ITEM_NOTIFICATIONS_ENABLED = true
        internal const val DEFAULT_ARTICLE_NOTIFICATIONS_ENABLED = true
        internal const val DEFAULT_THEME_RES_ID = R.style.Theme_SandersonWidget_AppWidgetContainer_Blank
    }

    fun clearAll()

    fun clearForAppWidgetId(appWidgetId: Int)

    fun storeProgressItemData(items: List<ProgressItem>)

    fun retrieveProgressItemData(): List<ProgressItem>

    fun storeArticleData(articles: List<Article>)

    fun retrieveArticleData(): List<Article>

    fun storeArticlesEnabled(appWidgetId: Int, articlesEnabled: Boolean)

    fun retrieveArticlesEnabled(appWidgetId: Int): Boolean

    fun storeTheme(appWidgetId: Int, themeId: Int)

    fun retrieveTheme(appWidgetId: Int): Int

    fun storeProgressUpdateNotificationsEnabled(boolean: Boolean)

    fun retrieveProgressUpdateNotificationsEnabled(): Boolean

    fun storeArticleUpdateNotificationsEnabled(boolean: Boolean)

    fun retrieveArticleUpdateNotificationsEnabled(): Boolean

    fun storeLayoutConfig(appWidgetId: Int, config: WidgetLayoutConfig)

    fun retrieveLayoutConfig(appWidgetId: Int): WidgetLayoutConfig
}
