package com.itj.sandersonwidget.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.itj.sandersonwidget.domain.Article
import com.itj.sandersonwidget.domain.ProgressItem

// TODO unit tests
class SharedPreferencesStorage(context: Context) : Storage {

    companion object {
        internal const val PREFS_NAME = "com.itj.sandersonwidget.ProgressBars"
        internal const val PROJECT_ITEMS = "progress_items"
        internal const val ARTICLES = "articles"
        internal const val DELIMITER = "DELIMITER"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    // todo take in widget id so multiple widgets have their own data
    override fun storeProgressItemData(items: List<ProgressItem>) {
        // Sets are not ordered. Need to add OG position to encoding and sort by this in decode
        val encodedItems = items.mapIndexed { index, progressItem ->
            "$index$DELIMITER${progressItem.label}$DELIMITER${progressItem.progressPercentage}"
        }
        with(sharedPreferences.edit()) {
            putStringSet(PROJECT_ITEMS, encodedItems.toMutableSet())
            apply()
        }
    }

    override fun retrieveProgressItemData(): List<ProgressItem> {
        val encodedItems = sharedPreferences.getStringSet(PROJECT_ITEMS, emptySet()) ?: emptySet()
        return encodedItems
            .map { encodedItem -> encodedItem.split(DELIMITER) }
            .sortedBy { it[0].toInt() }
            .map { ProgressItem(it[1], it[2]) }
    }

    override fun storeArticleData(items: List<Article>) {
        // Sets are not ordered. Need to add OG position to encoding and sort by this in decode
        val encodedArticles = items.mapIndexed { index, article ->
            "$index$DELIMITER${article.title}$DELIMITER${article.articleUrl}$DELIMITER${article.thumbnailUrl}"
        }
        with(sharedPreferences.edit()) {
            putStringSet(ARTICLES, encodedArticles.toMutableSet())
            apply()
        }
    }

    override fun retrieveArticleData(): List<Article> {
        val encodedArticles = sharedPreferences.getStringSet(ARTICLES, emptySet()) ?: emptySet()
        return encodedArticles
            .map { encodedArticle -> encodedArticle.split(DELIMITER) }
            .sortedBy { it[0].toInt() }
            .map { Article(it[1], it[2], it[3]) }
    }
}
