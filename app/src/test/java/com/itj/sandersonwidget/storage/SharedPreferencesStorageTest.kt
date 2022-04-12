package com.itj.sandersonwidget.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.itj.sandersonwidget.domain.Article
import com.itj.sandersonwidget.domain.ProgressItem
import com.itj.sandersonwidget.storage.SharedPreferencesStorage.Companion.ARTICLES
import com.itj.sandersonwidget.storage.SharedPreferencesStorage.Companion.DELIMITER
import com.itj.sandersonwidget.storage.SharedPreferencesStorage.Companion.PREFS_NAME
import com.itj.sandersonwidget.storage.SharedPreferencesStorage.Companion.PROJECT_ITEMS
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SharedPreferencesStorageTest {

    private companion object {
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
}
