package com.itj.sandersonwidget.domain.storage

import android.content.Context
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.ui.notifications.pushNewArticleNotification
import com.itj.sandersonwidget.ui.notifications.pushNewProgressItemNotification
import com.itj.sandersonwidget.ui.notifications.pushProgressItemUpdatedNotification

// TODO ut
class NotificationTriggeringStorage(
    private val context: Context,
    private val innerStorage: Storage,
) : Storage by innerStorage {

    override fun storeProgressItemData(items: List<ProgressItem>) {
        val progressItemNotificationsEnabled = innerStorage.retrieveProgressUpdateNotificationsEnabled()
        val existingProgressItems = innerStorage.retrieveProgressItemData()

        // Don't push a notification if it's the first fetch (that would just be annoying)
        if (progressItemNotificationsEnabled && existingProgressItems.isNotEmpty()) {
            val existingProgressItemLabels = existingProgressItems.map { item -> item.label }
            val newItems = mutableListOf<ProgressItem>()
            val updatedItems = mutableListOf<ProgressItem>()

            // Find new/changed items and sort
            items.minus(existingProgressItems.toSet()).forEach {
                if (existingProgressItemLabels.contains(it.label)) {
                    updatedItems.add(it)
                } else {
                    newItems.add(it)
                }
            }

            // Determine which notification to show
            if (newItems.isNotEmpty()) {
                pushNewProgressItemNotification(context, newItems.first(), newItems.count(), updatedItems.count())
            } else if (updatedItems.isNotEmpty()) {
                pushProgressItemUpdatedNotification(context, updatedItems.first(), updatedItems.count())
            }
        }

        innerStorage.storeProgressItemData(items)
    }

    override fun storeArticleData(articles: List<Article>) {
        val articleNotificationsEnabled = innerStorage.retrieveArticleUpdateNotificationsEnabled()
        val existingArticles = innerStorage.retrieveArticleData()

        // Don't push a notification if it's the first fetch (that would just be annoying)
        if (articleNotificationsEnabled && existingArticles.isNotEmpty()) {
            val newArticles = articles.minus(existingArticles.toSet())

            if (newArticles.isNotEmpty()) {
                pushNewArticleNotification(context, newArticles.first(), newArticles.count())
            }
        }

        innerStorage.storeArticleData(articles)
    }
}
