package com.itj.sandersonwidget.domain.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.itj.sandersonwidget.R
import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem
import com.itj.sandersonwidget.domain.model.WidgetLayoutConfig
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage.Companion.ARTICLES
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage.Companion.DELIMITER
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage.Companion.PREFS_NAME
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage.Companion.PROJECT_ITEMS
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SharedPreferencesStorageTest {

    private companion object {
        private const val APP_WIDGET_ID = 34
        private const val THEME_ID = 99
        private const val GRID_WIDTH = "small"
        private const val GRID_HEIGHT = "height"
        private const val WIDTH = 45
        private const val HEIGHT = 83
        private const val COMPRESSED_CONFIG = "$GRID_WIDTH$DELIMITER$GRID_HEIGHT$DELIMITER$WIDTH$DELIMITER$HEIGHT"

        val progressItems = listOf(
            ProgressItem("label1", "percentage1"),
            ProgressItem("label2", "percentage2"),
            ProgressItem("label3", "percentage3"),
        )
        val expectedEncodedItems = listOf(
            "0${DELIMITER}label1${DELIMITER}percentage1",
            "1${DELIMITER}label2${DELIMITER}percentage2",
            "2${DELIMITER}label3${DELIMITER}percentage3",
        ).toMutableSet()
        val expectedEncodedItemsUnsorted = listOf(
            "2${DELIMITER}label3${DELIMITER}percentage3",
            "0${DELIMITER}label1${DELIMITER}percentage1",
            "1${DELIMITER}label2${DELIMITER}percentage2",
        ).toMutableSet()
        val articles = listOf(
            Article("title1", "articleUrl1", "thumbnailUrl1"),
            Article("title2", "articleUrl2", "thumbnailUrl2"),
            Article("title3", "articleUrl3", "thumbnailUrl3"),
        )
        val expectedEncodedArticles = listOf(
            "0${DELIMITER}title1${DELIMITER}articleUrl1${DELIMITER}thumbnailUrl1",
            "1${DELIMITER}title2${DELIMITER}articleUrl2${DELIMITER}thumbnailUrl2",
            "2${DELIMITER}title3${DELIMITER}articleUrl3${DELIMITER}thumbnailUrl3",
        ).toMutableSet()
        val expectedEncodedArticlesUnsorted = listOf(
            "1${DELIMITER}title2${DELIMITER}articleUrl2${DELIMITER}thumbnailUrl2",
            "2${DELIMITER}title3${DELIMITER}articleUrl3${DELIMITER}thumbnailUrl3",
            "0${DELIMITER}title1${DELIMITER}articleUrl1${DELIMITER}thumbnailUrl1",
        ).toMutableSet()
    }

    private val mockSharedPreferencesEditor = mockk<SharedPreferences.Editor>(relaxed = true)
    private val mockSharedPreferences = mockk<SharedPreferences>().also {
        every { it.edit() } returns mockSharedPreferencesEditor
    }
    private val mockWidgetLayoutConfig = mockk<WidgetLayoutConfig>().also {
        every { it.gridSize.first } returns GRID_WIDTH
        every { it.gridSize.second } returns GRID_HEIGHT
        every { it.width } returns WIDTH
        every { it.height } returns HEIGHT
    }

    private val mockContext = mockk<Context>().also {
        every { it.getSharedPreferences(PREFS_NAME, MODE_PRIVATE) } returns mockSharedPreferences
    }

    private lateinit var subject: SharedPreferencesStorage

    @Before
    fun setUp() {
        subject = SharedPreferencesStorage(mockContext)
    }

    @Test
    fun storeProgressItemData() {
        subject.storeProgressItemData(progressItems)

        verify {
            mockSharedPreferences.edit()
            mockSharedPreferencesEditor.putStringSet(PROJECT_ITEMS, expectedEncodedItems)
            mockSharedPreferencesEditor.apply()
        }
    }

    @Test
    fun retrieveProgressItemData_returns_data() {
        every { mockSharedPreferences.getStringSet(PROJECT_ITEMS, emptySet()) } returns expectedEncodedItemsUnsorted

        val progressItemsResult = subject.retrieveProgressItemData()

        assertEquals(progressItems, progressItemsResult)
    }

    @Test
    fun retrieveProgressItemData_returns_empty() {
        every { mockSharedPreferences.getStringSet(PROJECT_ITEMS, emptySet()) } returns emptySet()

        val progressItemsResult = subject.retrieveProgressItemData()

        assert(progressItemsResult.isEmpty())
    }

    @Test
    fun storeArticleData() {
        subject.storeArticleData(articles)

        verify {
            mockSharedPreferences.edit()
            mockSharedPreferencesEditor.putStringSet(ARTICLES, expectedEncodedArticles)
            mockSharedPreferencesEditor.apply()
        }
    }

    @Test
    fun retrieveArticleData_returns_data() {
        every { mockSharedPreferences.getStringSet(ARTICLES, emptySet()) } returns expectedEncodedArticlesUnsorted

        val articlesResult = subject.retrieveArticleData()

        assertEquals(articles, articlesResult)
    }

    @Test
    fun retrieveArticleData_returns_empty() {
        every { mockSharedPreferences.getStringSet(ARTICLES, emptySet()) } returns emptySet()

        val articlesResult = subject.retrieveArticleData()

        assert(articlesResult.isEmpty())
    }

    @Test
    fun storeArticlesEnabled_true() {
        subject.storeArticlesEnabled(APP_WIDGET_ID, true)

        verify {
            mockSharedPreferences.edit()
            mockSharedPreferencesEditor.putBoolean(SharedPreferencesStorage.PREF_ARTICLES_ENABLED + APP_WIDGET_ID, true)
            mockSharedPreferencesEditor.apply()
        }
    }

    @Test
    fun storeArticlesEnabled_false() {
        subject.storeArticlesEnabled(APP_WIDGET_ID, false)

        verify {
            mockSharedPreferences.edit()
            mockSharedPreferencesEditor.putBoolean(
                SharedPreferencesStorage.PREF_ARTICLES_ENABLED + APP_WIDGET_ID,
                false
            )
            mockSharedPreferencesEditor.apply()
        }
    }

    @Test
    fun retrieveArticlesEnabled_true() {
        every {
            mockSharedPreferences.getBoolean(
                SharedPreferencesStorage.PREF_ARTICLES_ENABLED + APP_WIDGET_ID,
                true
            )
        } returns true

        val articlesEnabledResult = subject.retrieveArticlesEnabled(APP_WIDGET_ID)

        assertTrue(articlesEnabledResult)
    }

    @Test
    fun retrieveArticlesEnabled_false() {
        every {
            mockSharedPreferences.getBoolean(
                SharedPreferencesStorage.PREF_ARTICLES_ENABLED + APP_WIDGET_ID,
                true
            )
        } returns false

        val articlesEnabledResult = subject.retrieveArticlesEnabled(APP_WIDGET_ID)

        assertFalse(articlesEnabledResult)
    }

    @Test
    fun storeTheme() {
        subject.storeTheme(APP_WIDGET_ID, THEME_ID)

        verify {
            mockSharedPreferences.edit()
            mockSharedPreferencesEditor.putInt(SharedPreferencesStorage.PREF_THEME_ID + APP_WIDGET_ID, THEME_ID)
            mockSharedPreferencesEditor.apply()
        }
    }

    @Test
    fun retrieveTheme() {
        every {
            mockSharedPreferences.getInt(
                SharedPreferencesStorage.PREF_THEME_ID + APP_WIDGET_ID,
                R.style.Theme_SandersonWidget_AppWidgetContainer_Blank
            )
        } returns THEME_ID

        val themeResult = subject.retrieveTheme(APP_WIDGET_ID)

        assertEquals(THEME_ID, themeResult)
    }

    @Test
    fun storeProgressUpdateNotificationsEnabled_true() {
        subject.storeProgressUpdateNotificationsEnabled(true)

        verify {
            mockSharedPreferences.edit()
            mockSharedPreferencesEditor.putBoolean(
                SharedPreferencesStorage.PREF_PROGRESS_ITEM_NOTIFICATIONS_ENABLED,
                true
            )
            mockSharedPreferencesEditor.apply()
        }
    }

    @Test
    fun storeProgressUpdateNotificationsEnabled_false() {
        subject.storeProgressUpdateNotificationsEnabled(false)

        verify {
            mockSharedPreferences.edit()
            mockSharedPreferencesEditor.putBoolean(
                SharedPreferencesStorage.PREF_PROGRESS_ITEM_NOTIFICATIONS_ENABLED,
                false
            )
            mockSharedPreferencesEditor.apply()
        }
    }

    @Test
    fun retrieveProgressUpdateNotificationsEnabled_true() {
        every {
            mockSharedPreferences.getBoolean(
                SharedPreferencesStorage.PREF_PROGRESS_ITEM_NOTIFICATIONS_ENABLED,
                true
            )
        } returns true

        val progressItemNotificationsEnabledResult = subject.retrieveProgressUpdateNotificationsEnabled()

        assertTrue(progressItemNotificationsEnabledResult)
    }

    @Test
    fun retrieveProgressUpdateNotificationsEnabled_false() {
        every {
            mockSharedPreferences.getBoolean(
                SharedPreferencesStorage.PREF_PROGRESS_ITEM_NOTIFICATIONS_ENABLED,
                true
            )
        } returns false

        val progressItemNotificationsEnabledResult = subject.retrieveProgressUpdateNotificationsEnabled()

        assertFalse(progressItemNotificationsEnabledResult)
    }

    @Test
    fun storeArticleUpdateNotificationsEnabled_true() {
        subject.storeArticleUpdateNotificationsEnabled(true)

        verify {
            mockSharedPreferences.edit()
            mockSharedPreferencesEditor.putBoolean(SharedPreferencesStorage.PREF_ARTICLE_NOTIFICATIONS_ENABLED, true)
            mockSharedPreferencesEditor.apply()
        }
    }

    @Test
    fun storeArticleUpdateNotificationsEnabled_false() {
        subject.storeArticleUpdateNotificationsEnabled(false)

        verify {
            mockSharedPreferences.edit()
            mockSharedPreferencesEditor.putBoolean(SharedPreferencesStorage.PREF_ARTICLE_NOTIFICATIONS_ENABLED, false)
            mockSharedPreferencesEditor.apply()
        }
    }

    @Test
    fun retrieveArticleUpdateNotificationsEnabled_true() {
        every {
            mockSharedPreferences.getBoolean(
                SharedPreferencesStorage.PREF_ARTICLE_NOTIFICATIONS_ENABLED,
                true
            )
        } returns true

        val articleNotificationsEnabledResult = subject.retrieveArticleUpdateNotificationsEnabled()

        assertTrue(articleNotificationsEnabledResult)
    }

    @Test
    fun retrieveArticleUpdateNotificationsEnabled_false() {
        every {
            mockSharedPreferences.getBoolean(
                SharedPreferencesStorage.PREF_ARTICLE_NOTIFICATIONS_ENABLED,
                true
            )
        } returns false

        val articleNotificationsEnabledResult = subject.retrieveArticleUpdateNotificationsEnabled()

        assertFalse(articleNotificationsEnabledResult)
    }

    @Test
    fun storeLayoutConfig() {
        subject.storeLayoutConfig(APP_WIDGET_ID, mockWidgetLayoutConfig)

        verify {
            mockSharedPreferences.edit()
            mockSharedPreferencesEditor.putString(
                SharedPreferencesStorage.PREF_LAYOUT_CONFIG + APP_WIDGET_ID,
                COMPRESSED_CONFIG,
            )
            mockSharedPreferencesEditor.apply()
        }
    }

    @Test
    fun retrieveLayoutConfig() {
        every {
            mockSharedPreferences.getString(
                SharedPreferencesStorage.PREF_LAYOUT_CONFIG + APP_WIDGET_ID,
                ""
            )
        } returns COMPRESSED_CONFIG
        val expectedLayoutConfig = WidgetLayoutConfig(Pair(GRID_WIDTH, GRID_HEIGHT), WIDTH, HEIGHT)

        val layoutConfigResult = subject.retrieveLayoutConfig(APP_WIDGET_ID)

        assertEquals(expectedLayoutConfig, layoutConfigResult)
    }
}
