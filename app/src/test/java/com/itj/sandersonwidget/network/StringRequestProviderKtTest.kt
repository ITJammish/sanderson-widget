package com.itj.sandersonwidget.network

import android.content.Context
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class StringRequestProviderKtTest {

    private val mockContext = mockk<Context>()

    @Test
    fun getStringRequest() {
        val result = getStringRequest(mockContext)

        assertEquals("https://www.brandonsanderson.com/", result.url)
    }
}
