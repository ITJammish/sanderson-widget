package com.itj.sandersonwidget.domain.model

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProgressItemTest {

    companion object {
        private const val LABEL = "label"
        private const val PROGRESS_PERCENTAGE = "progress_percentage"
        private const val ALT_VALUE = "alt_value"
    }

    private val mockProgressItem = mockk<ProgressItem>()

    private lateinit var subject: ProgressItem

    @Before
    fun setUp() {
        subject = ProgressItem(
            label = LABEL,
            progressPercentage = PROGRESS_PERCENTAGE,
        )
    }

    @Test
    fun testEquals_isTrue() {
        mockProgressItem.also {
            every { it.label } returns LABEL
            every { it.progressPercentage } returns PROGRESS_PERCENTAGE
        }

        val result = subject == mockProgressItem

        assertTrue(result)
    }

    @Test
    fun testEquals_withDifferentLabel_isFalse() {
        mockProgressItem.also {
            every { it.label } returns ALT_VALUE
            every { it.progressPercentage } returns PROGRESS_PERCENTAGE
        }

        val result = subject == mockProgressItem

        assertFalse(result)
    }

    @Test
    fun testEquals_withDifferentProgressPercentage_isFalse() {
        mockProgressItem.also {
            every { it.label } returns LABEL
            every { it.progressPercentage } returns ALT_VALUE
        }

        val result = subject == mockProgressItem

        assertFalse(result)
    }
}
