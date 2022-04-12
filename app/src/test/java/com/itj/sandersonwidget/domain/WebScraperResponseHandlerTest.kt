package com.itj.sandersonwidget.domain

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.domain.WebScraperResponseHandler.Companion.ARTICLE_COUNT
import com.itj.sandersonwidget.domain.storage.Storage
import com.itj.sandersonwidget.utils.ComponentNameFetcher
import com.itj.sandersonwidget.utils.IntentProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Before
import org.junit.Test

class WebScraperResponseHandlerTest {

    private companion object {
        const val stubResponse = "a stub response"
        val widgetIds: IntArray = IntArray(2).also {
            it[0] = 1
            it[1] = 2
        }
    }

    private val mockProgressItems = mockk<List<ProgressItem>>()
    private val mockArticles = mockk<List<Article>>()
    private val mockDocument = mockk<Document>()
    private val mockComponentName = mockk<ComponentName>()
    private val mockAppWidgetManager = mockk<AppWidgetManager>(relaxed = true).also {
        every { it.getAppWidgetIds(mockComponentName) } returns widgetIds
    }
    private val mockApplicationContext = mockk<Context>()
    private val mockIntentWithExtras = mockk<Intent>()
    private val mockIntent = mockk<Intent>(relaxed = true).also {
        every { it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds) } returns mockIntentWithExtras
    }

    private val mockContext = mockk<Context>().also {
        every { it.applicationContext } returns mockApplicationContext
        every { it.sendBroadcast(any()) } returns Unit
    }
    private val mockStore = mockk<Storage>().also {
        every { it.storeProgressItemData(mockProgressItems) } returns Unit
        every { it.storeArticleData(mockArticles) } returns Unit
    }
    private val mockHTMLParser = mockk<HTMLParser>().also {
        every { it.parseProjectProgress(mockDocument) } returns mockProgressItems
        every { it.parseArticles(mockDocument, ARTICLE_COUNT) } returns mockArticles
    }
    private val mockIntentProvider = mockk<IntentProvider>().also {
        every { it.fetchUpdateWidgetIntent(mockContext) } returns mockIntent
    }
    private val mockComponentNameFetcher = mockk<ComponentNameFetcher>().also {
        every { it.fetchProgressBarsWidgetProviderComponentName(mockContext) } returns mockComponentName
    }

    private lateinit var subject: WebScraperResponseHandler

    @Before
    fun setUp() {
        mockkStatic(Jsoup::class).also {
            every { Jsoup.parseBodyFragment(stubResponse) } returns mockDocument
        }
        mockkStatic(AppWidgetManager::class).also {
            every { AppWidgetManager.getInstance(mockContext) } returns mockAppWidgetManager
        }
        subject = WebScraperResponseHandler(
            mockContext,
            mockStore,
            mockHTMLParser,
            mockIntentProvider,
            mockComponentNameFetcher,
        )
    }

    @Test
    fun handleWebScrapedResponse() {
        subject.handleWebScrapedResponse(stubResponse)

        verify {
            mockHTMLParser.parseProjectProgress(mockDocument)
            mockStore.storeProgressItemData(mockProgressItems)
            mockHTMLParser.parseArticles(mockDocument, ARTICLE_COUNT)
            mockStore.storeArticleData(mockArticles)

            mockComponentNameFetcher.fetchProgressBarsWidgetProviderComponentName(mockContext)
            mockAppWidgetManager.getAppWidgetIds(mockComponentName)
            mockIntentProvider.fetchUpdateWidgetIntent(mockContext)
            mockIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
            mockContext.sendBroadcast(mockIntentWithExtras)
        }
    }
}
