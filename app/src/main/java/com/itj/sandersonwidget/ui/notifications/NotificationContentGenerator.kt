package com.itj.sandersonwidget.ui.notifications

import android.content.Context
import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem

class NotificationContentGenerator {

    fun getNewProgressItemTitle(context: Context, progressItem: ProgressItem): String {
        return String.format(
            context.getString(R.string.notification_new_progress_item_title),
            progressItem.label,
            progressItem.progressPercentage,
        )
    }

    fun getNewProgressItemContentText(
        context: Context,
        otherNewItemCount: Int,
        updatedItemCount: Int,
    ): String? {
        return with(context) {
            if (otherNewItemCount > 0 && updatedItemCount > 0) {
                val newItemContent =
                    resources.getQuantityString(R.plurals.other_new_items, otherNewItemCount, otherNewItemCount)
                val updatedItemContent =
                    resources.getQuantityString(R.plurals.updated_items, updatedItemCount, updatedItemCount)
                String.format(
                    getString(R.string.notification_new_progress_item_content_new_and_existing_items),
                    newItemContent,
                    updatedItemContent,
                )
            } else if (otherNewItemCount > 0) {
                val newItemContent =
                    resources.getQuantityString(R.plurals.other_new_items, otherNewItemCount, otherNewItemCount)
                String.format(
                    getString(R.string.notification_new_progress_item_content_new_items_only),
                    newItemContent,
                )
            } else if (updatedItemCount > 0) {
                val updatedItemContent =
                    resources.getQuantityString(R.plurals.other_updated_items, updatedItemCount, updatedItemCount)
                String.format(
                    getString(R.string.notification_new_progress_item_content_new_items_only),
                    updatedItemContent,
                )
            } else {
                null
            }
        }
    }

    fun getProgressItemUpdatedTitle(context: Context, progressItem: ProgressItem): String {
        return String.format(
            context.getString(R.string.notification_progress_item_updated_title),
            progressItem.label,
            progressItem.progressPercentage,
        )
    }

    fun getProgressItemUpdatedContentText(context: Context, otherUpdatedItemCount: Int): String? {
        return with(context) {
            if (otherUpdatedItemCount > 0) {
                val otherUpdatedItemContent =
                    resources.getQuantityString(
                        R.plurals.other_updated_items,
                        otherUpdatedItemCount,
                        otherUpdatedItemCount
                    )
                String.format(
                    getString(R.string.notification_progress_item_updated_content_existing_items_only),
                    otherUpdatedItemContent,
                )
            } else {
                null
            }
        }
    }

    fun getNewArticleNotificationTitle(context: Context, article: Article): String {
        return String.format(context.getString(R.string.notification_new_article_title), article.title)
    }

    fun getNewArticleNotificationContentText(context: Context, otherArticlesCount: Int): String {
        return with(context) {
            if (otherArticlesCount > 0) {
                val otherNewArticlesContent =
                    resources.getQuantityString(R.plurals.other_new_articles, otherArticlesCount, otherArticlesCount)
                String.format(
                    getString(R.string.notification_multiple_new_article_content),
                    otherNewArticlesContent,
                )
            } else {
                getString(R.string.notification_new_article_content)
            }
        }
    }
}
