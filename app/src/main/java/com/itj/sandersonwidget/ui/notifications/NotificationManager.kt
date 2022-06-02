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

class NotificationManager {

    companion object {
        private const val CHANNEL_ID = "com.itj.sandersonwidget.NOTIFICATION_CHANNEL_ID"
        private const val NOTIFICATION_ID_PROGRESS_ITEM = 1
        private const val NOTIFICATION_ID_ARTICLE = 2
        private const val NOTIFICATION_ICON_RES_ID = R.drawable.ic_launcher_foreground
    }

    private val notificationContentGenerator = NotificationContentGenerator()

    // Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is "new" and not in the
    // support library.
    fun createNotificationChannel(context: Context) {
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

    fun pushNewProgressItemNotification(
        context: Context,
        progressItem: ProgressItem,
        newItemCount: Int,
        updatedItemCount: Int,
    ) {
        // Generate notification content
        val otherNewItemCount = newItemCount - 1
        val contentTitle = notificationContentGenerator.getNewProgressItemTitle(context, progressItem)
        val contentText: String? =
            notificationContentGenerator.getNewProgressItemContentText(context, otherNewItemCount, updatedItemCount)

        // Build and send notification
        val builder = getBaseNotificationBuilder(context)
            .setContentTitle(contentTitle).also {
                if (contentText != null) {
                    it.setContentText(contentText)
                }
            }
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PROGRESS_ITEM, builder.build())
    }

    fun pushProgressItemUpdatedNotification(
        context: Context,
        progressItem: ProgressItem,
        updatedItemCount: Int,
    ) {
        // Generate notification content
        val otherUpdatedItemCount = updatedItemCount - 1
        val contentTitle = notificationContentGenerator.getProgressItemUpdatedTitle(context, progressItem)
        val contentText: String? =
            notificationContentGenerator.getProgressItemUpdatedContentText(context, otherUpdatedItemCount)

        // Build and send notification
        val builder = getBaseNotificationBuilder(context)
            .setContentTitle(contentTitle).also {
                if (contentText != null) {
                    it.setContentText(contentText)
                }
            }
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PROGRESS_ITEM, builder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun pushNewArticleNotification(context: Context, article: Article, articleCount: Int) {
        // Build click pending intent
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

        // Generate notification content
        val otherArticlesCount = articleCount - 1
        val contentTitle = notificationContentGenerator.getNewArticleNotificationTitle(context, article)
        val contentText = notificationContentGenerator.getNewArticleNotificationContentText(context, otherArticlesCount)

        // Build and send notification
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
}
