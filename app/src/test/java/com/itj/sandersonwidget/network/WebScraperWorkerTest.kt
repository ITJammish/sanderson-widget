package com.itj.sandersonwidget.network

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class WebScraperWorkerTest {

    private val mockStringRequest = mockk<StringRequest>()
    private val mockQueue = mockk<RequestQueue>().also {
        every { it.add(mockStringRequest) } returns mockStringRequest
    }

    private val mockContext = mockk<Context>()
    private val mockWorkerParams = mockk<WorkerParameters>()

    private lateinit var subject: WebScraperWorker
    private lateinit var result: ListenableWorker.Result

    @Before
    fun setUp() {
        mockkStatic(Volley::class).also {
            every { Volley.newRequestQueue(mockContext) } returns mockQueue
        }
        mockkStatic(::getStringRequest)
        every { getStringRequest(mockContext) } returns mockStringRequest

        subject = WebScraperWorker(
            context = mockContext,
            workerParams = mockWorkerParams,
        )
    }

    @Test
    fun doWork() {
        result = subject.doWork()

        verify {
            Volley.newRequestQueue(mockContext)
            getStringRequest(mockContext)
            mockQueue.add(mockStringRequest)
        }
        Assert.assertEquals(ListenableWorker.Result.Success(), result)
    }
}
