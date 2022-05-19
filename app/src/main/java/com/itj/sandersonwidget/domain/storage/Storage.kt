package com.itj.sandersonwidget.domain.storage

import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem

// TODO accept and return Domain object and handle conversion for prefs storage in impl
interface Storage {

    fun clearAll()

    fun storeProgressItemData(items: List<ProgressItem>)

    fun retrieveProgressItemData(): List<ProgressItem>

    fun storeArticleData(items: List<Article>)

    fun retrieveArticleData(): List<Article>

    fun storeArticlesEnabled(articlesEnabled: Boolean)

    fun retrieveArticlesEnabled(): Boolean

    fun storeTheme(themeResId: Int)

    fun retrieveTheme(): Int
}
