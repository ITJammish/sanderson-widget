package com.itj.sandersonwidget.storage

import com.itj.sandersonwidget.domain.Article
import com.itj.sandersonwidget.domain.ProgressItem

// TODO accept and return Domain object and handle conversion for prefs storage in impl
interface Storage {

    fun storeProgressItemData(items: List<ProgressItem>)

    fun retrieveProgressItemData(): List<ProgressItem>

    fun storeArticleData(items: List<Article>)

    fun retrieveArticleData(): List<Article>
}
