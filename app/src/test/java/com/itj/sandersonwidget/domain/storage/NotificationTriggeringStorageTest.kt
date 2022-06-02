package com.itj.sandersonwidget.domain.storage

import android.content.Context
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.domain.model.WidgetLayoutConfig
import com.itj.sandersonwidget.ui.notifications.NotificationManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.Before
import org.junit.Test

class NotificationTriggeringStorageTest {

    private companion object {
        const val STUB_WIDGET_ID = 99
        const val STUB_THEME_IT = 45

        val progressItemOne = ProgressItem(
            label = "label_one",
            progressPercentage = "progressPercentage_one",
        )
        val progressItemTwo = ProgressItem(
            label = "label_two",
            progressPercentage = "progressPercentage_two",
        )
        val progressItemThree = ProgressItem(
            label = "label_three",
            progressPercentage = "progressPercentage_three",
        )
        val progressItemFour = ProgressItem(
            label = "label_four",
            progressPercentage = "progressPercentage_four",
        )
        val progressItemTwoUpdated = ProgressItem(
            label = "label_two",
            progressPercentage = "progressPercentage_two_updated_progress",
        )
        val progressItemThreeUpdated = ProgressItem(
            label = "label_three",
            progressPercentage = "progressPercentage_three_updated_progress",
        )

        val articleOne = Article(
            title = "article_title_one",
            articleUrl = "article_articleUrl_one",
            thumbnailUrl = "",
        )
        val articleTwo = Article(
            title = "article_title_two",
            articleUrl = "article_articleUrl_two",
            thumbnailUrl = "",
        )
        val articleThree = Article(
            title = "article_title_three",
            articleUrl = "article_articleUrl_three",
            thumbnailUrl = "",
        )
    }

    private val mockContext = mockk<Context>(relaxed = true)
    private val mockInnerStorage = mockk<Storage>(relaxed = true)
    private val mockNotificationManager = mockk<NotificationManager>()

    private lateinit var subject: NotificationTriggeringStorage

    @Before
    fun setUp() {
        subject = NotificationTriggeringStorage(
            mockContext,
            mockInnerStorage,
            mockNotificationManager,
        )
    }

    @Test
    fun storeProgressItemData_progressNotificationsEnabled_existingProgressItemsIsEmpty() {
        givenProgressNotificationsEnabled()
        givenExistingProgressItemsIsEmpty()
        val mockItems = mockk<List<ProgressItem>>()

        subject.storeProgressItemData(mockItems)

        thenProgressItemNotificationsNotProcessedOrSent(mockItems)
    }

    @Test
    fun storeProgressItemData_progressNotificationsEnabled_hasExistingProgressItems_noNewOrUpdatedItems() {
        givenProgressNotificationsEnabled()
        val stubItems = listOf(progressItemOne, progressItemTwo, progressItemThree)
        every { mockInnerStorage.retrieveProgressItemData() } returns stubItems

        subject.storeProgressItemData(stubItems)

        thenProgressItemNotificationsNotProcessedOrSent(stubItems)
    }

    @Test
    fun storeProgressItemData_progressNotificationsEnabled_hasExistingProgressItems_withNewItemOnly() {
        givenProgressNotificationsEnabled()
        val stubItemsExisting = listOf(progressItemTwo, progressItemThree)
        val stubItemsNew = listOf(progressItemOne, progressItemTwo, progressItemThree)
        every { mockInnerStorage.retrieveProgressItemData() } returns stubItemsExisting
        every {
            mockNotificationManager.pushNewProgressItemNotification(
                mockContext,
                progressItemOne,
                1,
                0
            )
        } returns Unit

        subject.storeProgressItemData(stubItemsNew)

        verifySequence {
            mockInnerStorage.retrieveProgressUpdateNotificationsEnabled()
            mockInnerStorage.retrieveProgressItemData()
            mockNotificationManager.pushNewProgressItemNotification(mockContext, progressItemOne, 1, 0)

            mockInnerStorage.storeProgressItemData(stubItemsNew)
        }
        verify(exactly = 0) {
            mockNotificationManager.pushProgressItemUpdatedNotification(mockContext, any(), any())
        }
    }

    @Test
    fun storeProgressItemData_progressNotificationsEnabled_hasExistingProgressItems_withUpdatedItemOnly() {
        givenProgressNotificationsEnabled()
        val stubItemsExisting = listOf(progressItemTwo, progressItemThree)
        val stubItemsNew = listOf(progressItemTwoUpdated, progressItemThree)
        every { mockInnerStorage.retrieveProgressItemData() } returns stubItemsExisting
        every {
            mockNotificationManager.pushProgressItemUpdatedNotification(
                mockContext,
                progressItemTwoUpdated,
                1
            )
        } returns Unit

        subject.storeProgressItemData(stubItemsNew)

        verifySequence {
            mockInnerStorage.retrieveProgressUpdateNotificationsEnabled()
            mockInnerStorage.retrieveProgressItemData()
            mockNotificationManager.pushProgressItemUpdatedNotification(mockContext, progressItemTwoUpdated, 1)

            mockInnerStorage.storeProgressItemData(stubItemsNew)
        }
        verify(exactly = 0) {
            mockNotificationManager.pushNewProgressItemNotification(mockContext, any(), any(), any())
        }
    }

    @Test
    fun storeProgressItemData_progressNotificationsEnabled_hasExistingProgressItems_withNewItemAndUpdatedItem() {
        givenProgressNotificationsEnabled()
        val stubItemsExisting = listOf(progressItemTwo, progressItemThree)
        val stubItemsNew = listOf(progressItemOne, progressItemTwoUpdated, progressItemThree)
        every { mockInnerStorage.retrieveProgressItemData() } returns stubItemsExisting
        every {
            mockNotificationManager.pushNewProgressItemNotification(
                mockContext,
                progressItemOne,
                1,
                1
            )
        } returns Unit

        subject.storeProgressItemData(stubItemsNew)

        verifySequence {
            mockInnerStorage.retrieveProgressUpdateNotificationsEnabled()
            mockInnerStorage.retrieveProgressItemData()
            mockNotificationManager.pushNewProgressItemNotification(mockContext, progressItemOne, 1, 1)

            mockInnerStorage.storeProgressItemData(stubItemsNew)
        }
        verify(exactly = 0) {
            mockNotificationManager.pushProgressItemUpdatedNotification(mockContext, any(), any())
        }
    }

    @Test
    fun storeProgressItemData_progressNotificationsEnabled_hasExistingProgressItems_withNewItemAndUpdatedItems() {
        givenProgressNotificationsEnabled()
        val stubItemsExisting = listOf(progressItemTwo, progressItemThree)
        val stubItemsNew = listOf(progressItemOne, progressItemTwoUpdated, progressItemThreeUpdated)
        every { mockInnerStorage.retrieveProgressItemData() } returns stubItemsExisting
        every {
            mockNotificationManager.pushNewProgressItemNotification(
                mockContext,
                progressItemOne,
                1,
                2
            )
        } returns Unit

        subject.storeProgressItemData(stubItemsNew)

        verifySequence {
            mockInnerStorage.retrieveProgressUpdateNotificationsEnabled()
            mockInnerStorage.retrieveProgressItemData()
            mockNotificationManager.pushNewProgressItemNotification(mockContext, progressItemOne, 1, 2)

            mockInnerStorage.storeProgressItemData(stubItemsNew)
        }
        verify(exactly = 0) {
            mockNotificationManager.pushProgressItemUpdatedNotification(mockContext, any(), any())
        }
    }

    @Test
    fun storeProgressItemData_progressNotificationsEnabled_hasExistingProgressItems_withNewItemsAndUpdatedItem() {
        givenProgressNotificationsEnabled()
        val stubItemsExisting = listOf(progressItemTwo, progressItemThree)
        val stubItemsNew = listOf(progressItemOne, progressItemTwoUpdated, progressItemThree, progressItemFour)
        every { mockInnerStorage.retrieveProgressItemData() } returns stubItemsExisting
        every {
            mockNotificationManager.pushNewProgressItemNotification(
                mockContext,
                progressItemOne,
                2,
                1
            )
        } returns Unit

        subject.storeProgressItemData(stubItemsNew)

        verifySequence {
            mockInnerStorage.retrieveProgressUpdateNotificationsEnabled()
            mockInnerStorage.retrieveProgressItemData()
            mockNotificationManager.pushNewProgressItemNotification(mockContext, progressItemOne, 2, 1)

            mockInnerStorage.storeProgressItemData(stubItemsNew)
        }
        verify(exactly = 0) {
            mockNotificationManager.pushProgressItemUpdatedNotification(mockContext, any(), any())
        }
    }

    @Test
    fun storeProgressItemData_progressNotificationsEnabled_hasExistingProgressItems_withNewItemsAndUpdatedItems() {
        givenProgressNotificationsEnabled()
        val stubItemsExisting = listOf(progressItemTwo, progressItemThree)
        val stubItemsNew = listOf(progressItemOne, progressItemTwoUpdated, progressItemThreeUpdated, progressItemFour)
        every { mockInnerStorage.retrieveProgressItemData() } returns stubItemsExisting
        every {
            mockNotificationManager.pushNewProgressItemNotification(
                mockContext,
                progressItemOne,
                2,
                2
            )
        } returns Unit

        subject.storeProgressItemData(stubItemsNew)

        verifySequence {
            mockInnerStorage.retrieveProgressUpdateNotificationsEnabled()
            mockInnerStorage.retrieveProgressItemData()
            mockNotificationManager.pushNewProgressItemNotification(mockContext, progressItemOne, 2, 2)

            mockInnerStorage.storeProgressItemData(stubItemsNew)
        }
        verify(exactly = 0) {
            mockNotificationManager.pushProgressItemUpdatedNotification(mockContext, any(), any())
        }
    }

    @Test
    fun storeProgressItemData_progressNotificationsDisabled() {
        givenProgressNotificationsDisabled()
        val mockItems = mockk<List<ProgressItem>>()

        subject.storeProgressItemData(mockItems)

        thenProgressItemNotificationsNotProcessedOrSent(mockItems)
    }

    @Test
    fun storeArticleData_articleNotificationsEnabled_noExistingArticles() {
        givenArticleNotificationsEnabled()
        every { mockInnerStorage.retrieveArticleData() } returns emptyList()
        val mockArticles = mockk<List<Article>>()

        subject.storeArticleData(mockArticles)

        thenArticleNotificationsNotProcessedOrSent(mockArticles)
    }

    @Test
    fun storeArticleData_articleNotificationsEnabled_noNewArticles() {
        givenArticleNotificationsEnabled()
        val stubArticles = listOf(
            articleOne,
            articleTwo,
        )
        every { mockInnerStorage.retrieveArticleData() } returns stubArticles

        subject.storeArticleData(stubArticles)

        thenArticleNotificationsNotProcessedOrSent(stubArticles)
    }

    @Test
    fun storeArticleData_articleNotificationsEnabled_newArticle() {
        givenArticleNotificationsEnabled()
        val stubExistingArticles = listOf(
            articleOne,
            articleTwo,
        )
        val stubNewArticles = listOf(
            articleOne,
            articleTwo,
            articleThree,
        )
        every { mockInnerStorage.retrieveArticleData() } returns stubExistingArticles
        every { mockNotificationManager.pushNewArticleNotification(mockContext, articleThree, 1) } returns Unit

        subject.storeArticleData(stubNewArticles)

        verifySequence {
            mockInnerStorage.retrieveArticleUpdateNotificationsEnabled()
            mockInnerStorage.retrieveArticleData()
            mockNotificationManager.pushNewArticleNotification(mockContext, articleThree, 1)
            mockInnerStorage.storeArticleData(stubNewArticles)
        }
    }

    @Test
    fun storeArticleData_articleNotificationsEnabled_newArticles() {
        givenArticleNotificationsEnabled()
        val stubExistingArticles = listOf(
            articleOne,
        )
        val stubNewArticles = listOf(
            articleOne,
            articleTwo,
            articleThree,
        )
        every { mockInnerStorage.retrieveArticleData() } returns stubExistingArticles
        every { mockNotificationManager.pushNewArticleNotification(mockContext, articleTwo, 2) } returns Unit

        subject.storeArticleData(stubNewArticles)

        verifySequence {
            mockInnerStorage.retrieveArticleUpdateNotificationsEnabled()
            mockInnerStorage.retrieveArticleData()
            mockNotificationManager.pushNewArticleNotification(mockContext, articleTwo, 2)
            mockInnerStorage.storeArticleData(stubNewArticles)
        }
    }

    @Test
    fun storeArticleData_articleNotificationsDisabled() {
        givenArticleNotificationsDisabled()
        val mockArticles = mockk<List<Article>>()

        subject.storeArticleData(mockArticles)

        thenArticleNotificationsNotProcessedOrSent(mockArticles)
    }

    @Test
    fun clearAll() {
        subject.clearAll()
        verify { mockInnerStorage.clearAll() }
    }

    @Test
    fun clearForAppWidgetId() {
        subject.clearForAppWidgetId(STUB_WIDGET_ID)
        verify { mockInnerStorage.clearForAppWidgetId(STUB_WIDGET_ID) }
    }

    @Test
    fun retrieveArticleData() {
        subject.retrieveArticleData()
        verify { mockInnerStorage.retrieveArticleData() }
    }

    @Test
    fun retrieveArticleUpdateNotificationsEnabled() {
        subject.retrieveArticleUpdateNotificationsEnabled()
        verify { mockInnerStorage.retrieveArticleUpdateNotificationsEnabled() }
    }

    @Test
    fun retrieveArticlesEnabled() {
        subject.retrieveArticlesEnabled(STUB_WIDGET_ID)
        verify { mockInnerStorage.retrieveArticlesEnabled(STUB_WIDGET_ID) }
    }

    @Test
    fun retrieveLayoutConfig() {
        subject.retrieveLayoutConfig(STUB_WIDGET_ID)
        verify { mockInnerStorage.retrieveLayoutConfig(STUB_WIDGET_ID) }
    }

    @Test
    fun retrieveProgressItemData() {
        subject.retrieveProgressItemData()
        verify { mockInnerStorage.retrieveProgressItemData() }
    }

    @Test
    fun retrieveProgressUpdateNotificationsEnabled() {
        subject.retrieveProgressUpdateNotificationsEnabled()
        verify { mockInnerStorage.retrieveProgressUpdateNotificationsEnabled() }
    }

    @Test
    fun retrieveTheme() {
        subject.retrieveTheme(STUB_WIDGET_ID)
        verify { mockInnerStorage.retrieveTheme(STUB_WIDGET_ID) }
    }

    @Test
    fun storeArticleUpdateNotificationsEnabled_true() {
        subject.storeArticleUpdateNotificationsEnabled(true)
        verify { mockInnerStorage.storeArticleUpdateNotificationsEnabled(true) }
    }

    @Test
    fun storeArticleUpdateNotificationsEnabled_false() {
        subject.storeArticleUpdateNotificationsEnabled(false)
        verify { mockInnerStorage.storeArticleUpdateNotificationsEnabled(false) }
    }

    @Test
    fun storeArticlesEnabled_true() {
        subject.storeArticlesEnabled(STUB_WIDGET_ID, true)
        verify { mockInnerStorage.storeArticlesEnabled(STUB_WIDGET_ID, true) }
    }

    @Test
    fun storeLayoutConfig() {
        val mockLayoutConfig = mockk<WidgetLayoutConfig>()

        subject.storeLayoutConfig(STUB_WIDGET_ID, mockLayoutConfig)

        verify { mockInnerStorage.storeLayoutConfig(STUB_WIDGET_ID, mockLayoutConfig) }
    }

    @Test
    fun storeProgressUpdateNotificationsEnabled_true() {
        subject.storeProgressUpdateNotificationsEnabled(true)
        verify { mockInnerStorage.storeProgressUpdateNotificationsEnabled(true) }
    }

    @Test
    fun storeProgressUpdateNotificationsEnabled_false() {
        subject.storeProgressUpdateNotificationsEnabled(false)
        verify { mockInnerStorage.storeProgressUpdateNotificationsEnabled(false) }
    }

    @Test
    fun storeTheme() {
        subject.storeTheme(STUB_WIDGET_ID, STUB_THEME_IT)
        verify { mockInnerStorage.storeTheme(STUB_WIDGET_ID, STUB_THEME_IT) }
    }

    // GIVEN
    private fun givenProgressNotificationsEnabled() {
        every { mockInnerStorage.retrieveProgressUpdateNotificationsEnabled() } returns true
    }

    private fun givenProgressNotificationsDisabled() {
        every { mockInnerStorage.retrieveProgressUpdateNotificationsEnabled() } returns false
    }

    private fun givenExistingProgressItemsIsEmpty() {
        every { mockInnerStorage.retrieveProgressItemData() } returns emptyList()
    }

    private fun givenArticleNotificationsEnabled() {
        every { mockInnerStorage.retrieveArticleUpdateNotificationsEnabled() } returns true
    }

    private fun givenArticleNotificationsDisabled() {
        every { mockInnerStorage.retrieveArticleUpdateNotificationsEnabled() } returns false
    }

    // THEN
    private fun thenProgressItemNotificationsNotProcessedOrSent(testItems: List<ProgressItem>) {
        verifySequence {
            mockInnerStorage.retrieveProgressUpdateNotificationsEnabled()
            mockInnerStorage.retrieveProgressItemData()
            mockInnerStorage.storeProgressItemData(testItems)
        }
        verify(exactly = 0) {
            mockNotificationManager.pushNewProgressItemNotification(mockContext, any(), any(), any())
            mockNotificationManager.pushProgressItemUpdatedNotification(mockContext, any(), any())
        }
    }

    private fun thenArticleNotificationsNotProcessedOrSent(testArticles: List<Article>) {
        verifySequence {
            mockInnerStorage.retrieveArticleUpdateNotificationsEnabled()
            mockInnerStorage.retrieveArticleData()
            mockInnerStorage.storeArticleData(testArticles)
        }
        verify(exactly = 0) {
            mockNotificationManager.pushNewArticleNotification(mockContext, any(), any())
        }
    }
}
