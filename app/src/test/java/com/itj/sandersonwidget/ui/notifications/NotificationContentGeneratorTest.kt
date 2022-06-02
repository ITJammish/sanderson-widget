package com.itj.sandersonwidget.ui.notifications

import android.content.Context
import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class NotificationContentGeneratorTest {

    private companion object {
        const val PROGRESS_ITEM_LABEL = "progress_item_label"
        const val PROGRESS_ITEM_PERCENTAGE = "progress_item_percentage"

        const val ARTICLE_TITLE = "article_title"
    }

    private val mockContext = mockk<Context>()

    private lateinit var subject: NotificationContentGenerator
    private var result: String? = null

    @Before
    fun setUp() {
        subject = NotificationContentGenerator()
    }

    @Test
    fun getNewProgressItemTitle() {
        val mockProgressItem = mockk<ProgressItem>().also {
            every { it.label } returns PROGRESS_ITEM_LABEL
            every { it.progressPercentage } returns PROGRESS_ITEM_PERCENTAGE
        }
        every { mockContext.getString(R.string.notification_new_progress_item_title) } returns "New project added: %1\$s at %2\$s%%"

        result = subject.getNewProgressItemTitle(
            mockContext,
            mockProgressItem,
        )

        assertEquals("New project added: $PROGRESS_ITEM_LABEL at $PROGRESS_ITEM_PERCENTAGE%", result)
    }

    @Test
    fun getNewProgressItemContentText_newItemAndUpdatedItem() {
        every { mockContext.resources.getQuantityString(R.plurals.other_new_items, 1, 1) } returns "is also 1 other new project"
        every { mockContext.resources.getQuantityString(R.plurals.updated_items, 1, 1) } returns "1 updated project"
        every { mockContext.getString(R.string.notification_new_progress_item_content_new_and_existing_items) } returns "There %1\$s, and %2\$s."

        result = subject.getNewProgressItemContentText(mockContext, 1, 1)

        assertEquals("There is also 1 other new project, and 1 updated project.", result)
    }

    @Test
    fun getNewProgressItemContentText_newItemsOnly() {
        every { mockContext.resources.getQuantityString(R.plurals.other_new_items, 1, 1) } returns "is also 1 other new project"
        every { mockContext.getString(R.string.notification_new_progress_item_content_new_items_only) } returns "There %s."

        result = subject.getNewProgressItemContentText(mockContext, 1, 0)

        assertEquals("There is also 1 other new project.", result)
    }

    @Test
    fun getNewProgressItemContentText_updatedItemsOnly() {
        every { mockContext.resources.getQuantityString(R.plurals.other_updated_items, 1, 1) } returns "is also 1 other updated project"
        every { mockContext.getString(R.string.notification_new_progress_item_content_new_items_only) } returns "There %s."

        result = subject.getNewProgressItemContentText(mockContext, 0, 1)

        assertEquals("There is also 1 other updated project.", result)
    }

    @Test
    fun getNewProgressItemContentText_noNewItems() {
        result = subject.getNewProgressItemContentText(mockContext, 0, 0)

        assertNull(result)
    }

    @Test
    fun getProgressItemUpdatedTitle() {
        val mockProgressItem = mockk<ProgressItem>().also {
            every { it.label } returns PROGRESS_ITEM_LABEL
            every { it.progressPercentage } returns PROGRESS_ITEM_PERCENTAGE
        }
        every { mockContext.getString(R.string.notification_progress_item_updated_title) } returns "Project updated: %1\$s now at %2\$s%%"

        result = subject.getProgressItemUpdatedTitle(mockContext, mockProgressItem)

        assertEquals("Project updated: $PROGRESS_ITEM_LABEL now at $PROGRESS_ITEM_PERCENTAGE%", result)
    }

    @Test
    fun getProgressItemUpdatedContentText() {
        every { mockContext.resources.getQuantityString(R.plurals.other_updated_items, 1, 1) } returns "is also 1 other updated project"
        every { mockContext.getString(R.string.notification_progress_item_updated_content_existing_items_only) } returns "There %s."

        result = subject.getProgressItemUpdatedContentText(mockContext, 1)

        assertEquals("There is also 1 other updated project.", result)
    }

    @Test
    fun getProgressItemUpdatedContentText_noOtherUpdatedItems() {
        result = subject.getProgressItemUpdatedContentText(mockContext, 0)
        assertNull(result)
    }

    @Test
    fun getNewArticleNotificationTitle() {
        val mockArticle = mockk<Article>().also {
            every { it.title } returns ARTICLE_TITLE
        }
        every { mockContext.getString(R.string.notification_new_article_title) } returns "New article: %s"

        result = subject.getNewArticleNotificationTitle(mockContext, mockArticle)

        assertEquals("New article: $ARTICLE_TITLE", result)
    }

    @Test
    fun getNewArticleNotificationContentText_hasOtherArticles() {
        every { mockContext.resources.getQuantityString(R.plurals.other_new_articles, 1, 1) } returns "There is also 1 other new article"
        every { mockContext.getString(R.string.notification_multiple_new_article_content) } returns "Click here to open. %s."

        result = subject.getNewArticleNotificationContentText(mockContext, 1)

        assertEquals("Click here to open. There is also 1 other new article.", result)
    }

    @Test
    fun getNewArticleNotificationContentText_noOtherArticles() {
        every { mockContext.getString(R.string.notification_new_article_content) } returns "Click here to open"

        result = subject.getNewArticleNotificationContentText(mockContext, 0)

        assertEquals("Click here to open", result)
    }
}
