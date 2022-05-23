package com.itj.sandersonwidget.domain.storage

import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.domain.model.WidgetLayoutConfig

interface Storage {

    companion object {
        const val INVALID_INT = -1
    }

    fun clearAll()

    fun clearForAppWidgetId(appWidgetId: Int)

    fun storeProgressItemData(items: List<ProgressItem>)

    fun retrieveProgressItemData(): List<ProgressItem>

    fun storeArticleData(items: List<Article>)

    fun retrieveArticleData(): List<Article>

    fun storeArticlesEnabled(appWidgetId: Int, articlesEnabled: Boolean)

    fun retrieveArticlesEnabled(appWidgetId: Int): Boolean

    fun storeTheme(appWidgetId: Int, themeId: Int)

    fun retrieveTheme(appWidgetId: Int): Int

    fun storeLayoutConfig(appWidgetId: Int, config: WidgetLayoutConfig)

    fun retrieveLayoutConfig(appWidgetId: Int): WidgetLayoutConfig
}
