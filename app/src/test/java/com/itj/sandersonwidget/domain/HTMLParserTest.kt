package com.itj.sandersonwidget.domain

import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HTMLParserTest {

    private companion object {
        val expectedProgressBars = listOf(
            ProgressItem(label = "Stormlight 5 Draft 1.0", progressPercentage = "5"),
            ProgressItem(label = "Wax & Wayne Book 4 (Mistborn 7) Draft 4.0", progressPercentage = "100"),
            ProgressItem(label = "Skyward Four Draft 1.0", progressPercentage = "100"),
            ProgressItem(label = "Secret Project One Draft 3.0", progressPercentage = "66"),
        )
        val expectedArticles = listOf(
            Article(
                title = "Prologue to Stormlight 5",
                articleUrl = "https://www.brandonsanderson.com/prologue-to-stormlight-5/",
                thumbnailUrl = "https://www.brandonsanderson.com/wp-content/uploads/2022/03/prologue-768x512.png"
            ),
            Article(
                title = "First Look at Secret Project Four (Hint: it’s Stormlight Adjacent)",
                articleUrl = "https://www.brandonsanderson.com/first-look-at-secret-project-four-hint-its-stormlight-adjacent/",
                thumbnailUrl = "https://www.brandonsanderson.com/wp-content/uploads/2022/03/SP4-blog-768x512.png"
            ),
            Article(
                title = "First Look at Secret Project #3",
                articleUrl = "https://www.brandonsanderson.com/first-look-at-secret-project-3/",
                thumbnailUrl = "https://www.brandonsanderson.com/wp-content/uploads/2022/03/sp3-blog-768x512.png"
            ),
            Article(
                title = "Some FAQs You Might Enjoy",
                articleUrl = "https://www.brandonsanderson.com/some-faqs-you-might-enjoy/",
                thumbnailUrl = "https://www.brandonsanderson.com/wp-content/uploads/2022/03/Artboard-1-copy-3-768x512.png"
            ),
            Article(
                title = "First Look at Secret Project #2",
                articleUrl = "https://www.brandonsanderson.com/first-look-at-secret-project-2/",
                thumbnailUrl = "https://www.brandonsanderson.com/wp-content/uploads/2022/03/SP2-blog-768x512.jpg"
            ),
        )

        val classLoader = javaClass.classLoader!!
        val progressBarsHtml = classLoader.getResource("progress_bars.html").readText()
        val articlesHtml = classLoader.getResource("articles.html").readText()
    }

    private lateinit var subject: HTMLParser

    @Before
    fun setUp() {
        subject = HTMLParser()
    }

    @Test
    fun parseProjectProgress() {
        val mockResponse = Jsoup.parseBodyFragment(progressBarsHtml)

        val result = subject.parseProjectProgress(mockResponse)

        assertEquals(expectedProgressBars, result)
    }

    @Test
    fun parseArticles() {
        val mockResponse = Jsoup.parseBodyFragment(articlesHtml)

        val result = subject.parseArticles(mockResponse, 5)

        assertEquals(expectedArticles, result)
    }
}
