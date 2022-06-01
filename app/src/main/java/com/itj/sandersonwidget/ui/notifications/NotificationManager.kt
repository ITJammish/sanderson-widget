package com.itj.sandersonwidget.ui.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem

private const val CHANNEL_ID = "com.itj.sandersonwidget.NOTIFICATION_CHANNEL_ID"
private const val NOTIFICATION_ID_PROGRESS_ITEM = 1
private const val NOTIFICATION_ID_ARTICLE = 2
private const val NOTIFICATION_ICON_RES_ID = R.drawable.ic_launcher_foreground

/**
 * Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is "new" and not in the
 * support library.
 */
internal fun createNotificationChannel(context: Context) {
    with(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

internal fun pushNewProgressItemNotification(
    context: Context,
    progressItem: ProgressItem,
    newItemCount: Int,
    updatedItemCount: Int,
) {
    val contentTitle = String.format(
        context.getString(R.string.notification_new_progress_item_title),
        progressItem.label,
        progressItem.progressPercentage,
    )

    val otherNewItemCount = newItemCount - 1
    val contentText: String? = with(context) {
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

    val builder = getBaseNotificationBuilder(context)
        .setContentTitle(contentTitle).also {
            if (contentText != null) {
                it.setContentText(contentText)
            }
        }
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PROGRESS_ITEM, builder.build())
}

internal fun pushProgressItemUpdatedNotification(
    context: Context,
    progressItem: ProgressItem,
    updatedItemCount: Int,
) {
    val contentTitle = String.format(
        context.getString(R.string.notification_progress_item_updated_title),
        progressItem.label,
        progressItem.progressPercentage,
    )

    val otherUpdatedItemCount = updatedItemCount - 1
    val contentText: String? = with(context) {
        if (otherUpdatedItemCount > 0) {
            val otherUpdatedItemContent =
                resources.getQuantityString(R.plurals.other_updated_items, otherUpdatedItemCount, otherUpdatedItemCount)
            String.format(
                getString(R.string.notification_progress_item_updated_content_existing_items_only),
                otherUpdatedItemContent,
            )
        } else {
            null
        }
    }

    val builder = getBaseNotificationBuilder(context)
        .setContentTitle(contentTitle).also {
            if (contentText != null) {
                it.setContentText(contentText)
            }
        }
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PROGRESS_ITEM, builder.build())
}

@SuppressLint("UnspecifiedImmutableFlag")
internal fun pushNewArticleNotification(context: Context, article: Article, articleCount: Int) {
    val browserLaunchIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(article.articleUrl)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    val pendingIntent: PendingIntent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(context, 0, browserLaunchIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, browserLaunchIntent, 0)
        }

    val contentTitle = String.format(context.getString(R.string.notification_new_article_title), article.title)

    val otherArticlesCount = articleCount - 1
    val contentText = with(context) {
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

    val builder = getBaseNotificationBuilder(context)
        .setContentTitle(contentTitle)
        .setContentText(contentText)
        .setContentIntent(pendingIntent)
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ARTICLE, builder.build())
}

private fun getBaseNotificationBuilder(context: Context): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(NOTIFICATION_ICON_RES_ID)
        .setPriority(PRIORITY_LOW)
        .setVisibility(VISIBILITY_PUBLIC)
        .setAutoCancel(true)
}
