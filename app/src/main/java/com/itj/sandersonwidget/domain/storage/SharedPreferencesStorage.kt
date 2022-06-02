package com.itj.sandersonwidget.domain.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.domain.model.WidgetLayoutConfig
import com.itj.sandersonwidget.domain.storage.Storage.Companion.DEFAULT_ARTICLES_ENABLED
import com.itj.sandersonwidget.domain.storage.Storage.Companion.DEFAULT_ARTICLE_NOTIFICATIONS_ENABLED
import com.itj.sandersonwidget.domain.storage.Storage.Companion.DEFAULT_PROGRESS_ITEM_NOTIFICATIONS_ENABLED
import com.itj.sandersonwidget.domain.storage.Storage.Companion.DEFAULT_THEME_RES_ID
import com.itj.sandersonwidget.domain.storage.Storage.Companion.INVALID_INT

/**
 * Uses SharedPreferences to store widget data using primitives.
 */
class SharedPreferencesStorage(context: Context) : Storage {

    companion object {
        internal const val PREFS_NAME = "com.itj.sandersonwidget.ProgressBars"
        internal const val PROJECT_ITEMS = "progress_items"
        internal const val ARTICLES = "articles"
        internal const val PREF_ARTICLES_ENABLED = "pref_articles_enabled"
        internal const val PREF_THEME_ID = "pref_theme_id"
        internal const val PREF_PROGRESS_ITEM_NOTIFICATIONS_ENABLED = "pref_progress_item_notifications_enabled"
        internal const val PREF_ARTICLE_NOTIFICATIONS_ENABLED = "pref_article_notifications_enabled"
        internal const val PREF_LAYOUT_CONFIG = "pref_layout_config"

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

    override fun storeArticleData(articles: List<Article>) {
        // Sets are not ordered. Need to add OG position to encoding and sort by this in decode
        val encodedArticles = articles.mapIndexed { index, article ->
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
        return sharedPreferences.getBoolean(PREF_ARTICLES_ENABLED + appWidgetId, DEFAULT_ARTICLES_ENABLED)
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
            DEFAULT_THEME_RES_ID
        )
    }

    override fun storeProgressUpdateNotificationsEnabled(boolean: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(PREF_PROGRESS_ITEM_NOTIFICATIONS_ENABLED, boolean)
            apply()
        }
    }

    override fun retrieveProgressUpdateNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(
            PREF_PROGRESS_ITEM_NOTIFICATIONS_ENABLED,
            DEFAULT_PROGRESS_ITEM_NOTIFICATIONS_ENABLED
        )
    }

    override fun storeArticleUpdateNotificationsEnabled(boolean: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(PREF_ARTICLE_NOTIFICATIONS_ENABLED, boolean)
            apply()
        }
    }

    override fun retrieveArticleUpdateNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_ARTICLE_NOTIFICATIONS_ENABLED, DEFAULT_ARTICLE_NOTIFICATIONS_ENABLED)
    }

    override fun storeLayoutConfig(appWidgetId: Int, config: WidgetLayoutConfig) {
        val compressedConfig =
            "${config.gridSize.first}$DELIMITER${config.gridSize.second}$DELIMITER${config.width}$DELIMITER${config.height}"

        with(sharedPreferences.edit()) {
            putString(PREF_LAYOUT_CONFIG + appWidgetId, compressedConfig)
            apply()
        }
    }

    override fun retrieveLayoutConfig(appWidgetId: Int): WidgetLayoutConfig {
        val storedValue = sharedPreferences.getString(PREF_LAYOUT_CONFIG + appWidgetId, "")
        return if (storedValue.isNullOrEmpty()) {
            WidgetLayoutConfig(gridSize = Pair("", ""), INVALID_INT, INVALID_INT)
        } else {
            val splitValues = storedValue.split(DELIMITER)
            WidgetLayoutConfig(
                gridSize = Pair(splitValues[0], splitValues[1]),
                width = splitValues[2].toInt(),
                height = splitValues[3].toInt(),
            )
        }
    }
}
