package com.itj.sandersonwidget.ui.service

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.itj.sandersonwidget.ProgressBarsWidgetProvider.Companion.EXTRA_ARTICLE_POSITION
import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage

/**
 * A service that acts as a list adapter for the article stack items.
 */
class ArticleListWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ArticleListWidgetFactory(applicationContext)
    }

    internal class ArticleListWidgetFactory(
        private val context: Context,
    ) : RemoteViewsFactory {

        companion object {
            private const val WEBSITE_ARTICLE_IMAGE_WIDTH = 768
            private const val WEBSITE_ARTICLE_IMAGE_HEIGHT = 512
        }

        private lateinit var data: List<Article>

        override fun onCreate() {
            // NI
        }

        override fun onDataSetChanged() {
            data = SharedPreferencesStorage(context).retrieveArticleData()
        }

        override fun onDestroy() {
            // NI
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            var articleImage: Bitmap? = null
            try {
                articleImage = Glide.with(context)
                    .asBitmap()
                    .load(data[position].thumbnailUrl)
                    .submit(WEBSITE_ARTICLE_IMAGE_WIDTH, WEBSITE_ARTICLE_IMAGE_HEIGHT)
                    .get()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Set image to View if image could be loaded, otherwise display the article title.
            val item = RemoteViews(context.packageName, R.layout.view_article_preview).apply {
                articleImage?.let { setImageViewBitmap(R.id.article_image, it) }
                    ?: setTextViewText(R.id.article_title, data[position].title)
            }

            // Apply a fill intent to broadcast the item's position when clicked
            Intent().apply { putExtra(EXTRA_ARTICLE_POSITION, position) }.also {
                item.setOnClickFillInIntent(R.id.article_root_view, it)
            }

            return item
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }
    }
}
