package com.itj.sandersonwidget.domain.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem

class SharedPreferencesStorage(context: Context) : Storage {

    companion object {
        internal const val PREFS_NAME = "com.itj.sandersonwidget.ProgressBars"
        internal const val PROJECT_ITEMS = "progress_items"
        internal const val ARTICLES = "articles"
        internal const val PREF_ARTICLES_ENABLED = "pref_articles_enabled"
        internal const val PREF_THEME_ID = "PREF_THEME_ID"

        internal const val DELIMITER = "DELIMITER"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    override fun clearAll() {
        sharedPreferences.edit()
            .remove(PROJECT_ITEMS)
            .remove(ARTICLES)
            .apply()
    }

    override fun clearForAppWidgetId(appWidgetId: Int) {
        sharedPreferences.edit()
            .remove(PREF_ARTICLES_ENABLED + appWidgetId)
            .remove(PREF_THEME_ID + appWidgetId)
            .apply()
    }

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

    // Widget Preferences
    override fun storeArticlesEnabled(appWidgetId: Int, articlesEnabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(PREF_ARTICLES_ENABLED + appWidgetId, articlesEnabled)
            apply()
        }
    }

    override fun retrieveArticlesEnabled(appWidgetId: Int): Boolean {
        return sharedPreferences.getBoolean(PREF_ARTICLES_ENABLED + appWidgetId, true)
    }

    override fun storeTheme(appWidgetId: Int, themeId: Int) {
        with(sharedPreferences.edit()) {
            putInt(PREF_THEME_ID + appWidgetId, themeId)
            apply()
        }
    }

    override fun retrieveTheme(appWidgetId: Int): Int {
        return sharedPreferences.getInt(
            PREF_THEME_ID + appWidgetId,
            R.style.Theme_SandersonWidget_AppWidgetContainer_WayOfKings
        )
    }
}
