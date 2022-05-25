package com.itj.sandersonwidget.domain.model

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ArticleTest {

    companion object {
        private const val TITLE = "title"
        private const val ARTICLE_URL = "article_url"
        private const val THUMBNAIL_URL = "thumbnail_url"
        private const val ALT_VALUE = "alt_value"
    }

    private val mockArticle = mockk<Article>()

    private lateinit var subject: Article

    @Before
    fun setUp() {
        subject = Article(
            title = TITLE,
            articleUrl = ARTICLE_URL,
            thumbnailUrl = THUMBNAIL_URL,
        )
    }

    @Test
    fun testEquals_isTrue() {
        mockArticle.also {
            every { it.title } returns TITLE
            every { it.articleUrl } returns ARTICLE_URL
            every { it.thumbnailUrl } returns THUMBNAIL_URL
        }

        val result = subject == mockArticle

        assertTrue(result)
    }

    @Test
    fun testEquals_withDifferentTitle_isFalse() {
        mockArticle.also {
            every { it.title } returns ALT_VALUE
            every { it.articleUrl } returns ARTICLE_URL
            every { it.thumbnailUrl } returns THUMBNAIL_URL
        }

        val result = subject == mockArticle

        assertFalse(result)
    }

    @Test
    fun testEquals_withDifferentArticleUrl_isFalse() {
        mockArticle.also {
            every { it.title } returns TITLE
            every { it.articleUrl } returns ALT_VALUE
            every { it.thumbnailUrl } returns THUMBNAIL_URL
        }

        val result = subject == mockArticle

        assertFalse(result)
    }

    @Test
    fun testEquals_withDifferentThumbnail_isFalse() {
        mockArticle.also {
            every { it.title } returns TITLE
            every { it.articleUrl } returns ARTICLE_URL
            every { it.thumbnailUrl } returns ALT_VALUE
        }

        val result = subject == mockArticle

        assertFalse(result)
    }
}
