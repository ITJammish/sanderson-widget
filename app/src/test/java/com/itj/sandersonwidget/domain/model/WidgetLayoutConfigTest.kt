package com.itj.sandersonwidget.domain.model

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WidgetLayoutConfigTest {

    companion object {
        private val GRID_SIZE = Pair("first", "second")
        private val ALT_GRID_SIZE = Pair("second", "first")
        private const val WIDTH = 10
        private const val HEIGHT = 15
        private const val ALT_DIMENSION = 20
    }

    private val mockWidgetLayoutConfig = mockk<WidgetLayoutConfig>()

    private lateinit var subject: WidgetLayoutConfig

    @Before
    fun setUp() {
        subject = WidgetLayoutConfig(
            gridSize = GRID_SIZE,
            width = WIDTH,
            height = HEIGHT,
        )
    }

    @Test
    fun testEquals_isTrue() {
        mockWidgetLayoutConfig.also {
            every { it.gridSize } returns GRID_SIZE
            every { it.width } returns WIDTH
            every { it.height } returns HEIGHT
        }

        val result = subject == mockWidgetLayoutConfig

        assertTrue(result)
    }

    @Test
    fun testEquals_withDifferentGridSize_isFalse() {
        mockWidgetLayoutConfig.also {
            every { it.gridSize } returns ALT_GRID_SIZE
            every { it.width } returns WIDTH
            every { it.height } returns HEIGHT
        }

        val result = subject == mockWidgetLayoutConfig

        assertFalse(result)
    }

    @Test
    fun testEquals_withDifferentWidth_isFalse() {
        mockWidgetLayoutConfig.also {
            every { it.gridSize } returns GRID_SIZE
            every { it.width } returns ALT_DIMENSION
            every { it.height } returns HEIGHT
        }

        val result = subject == mockWidgetLayoutConfig

        assertFalse(result)
    }

    @Test
    fun testEquals_withDifferentHeight_isFalse() {
        mockWidgetLayoutConfig.also {
            every { it.gridSize } returns GRID_SIZE
            every { it.width } returns WIDTH
            every { it.height } returns ALT_DIMENSION
        }

        val result = subject == mockWidgetLayoutConfig

        assertFalse(result)
    }
}
