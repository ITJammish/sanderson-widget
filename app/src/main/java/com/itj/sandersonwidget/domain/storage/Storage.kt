package com.itj.sandersonwidget.domain.storage

import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem

// TODO accept and return Domain object and handle conversion for prefs storage in impl
interface Storage {

    fun clearAll()

    fun clearForAppWidgetId(appWidgetId: Int)

    fun storeProgressItemData(items: List<ProgressItem>)

    fun retrieveProgressItemData(): List<ProgressItem>

    fun storeArticleData(items: List<Article>)

    fun retrieveArticleData(): List<Article>

    fun storeArticlesEnabled(appWidgetId: Int, articlesEnabled: Boolean)

    fun retrieveArticlesEnabled(appWidgetId: Int): Boolean

    fun storeTheme(appWidgetId: Int, themeResId: Int)

    fun retrieveTheme(appWidgetId: Int): Int
}
