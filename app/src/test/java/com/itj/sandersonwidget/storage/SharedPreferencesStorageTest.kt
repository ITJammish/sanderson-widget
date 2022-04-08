package com.itj.sandersonwidget.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.itj.sandersonwidget.domain.ProgressItem
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

    // TODO Monday: continue writing UnitTests for any class that can be tested
    //  then go back to tutorials and pull in article views -> make it look nice
    //  See other todos, config, notifications etc
    @Test
    fun storeArticleData() {
    }

    @Test
    fun retrieveArticleData() {
    }
}