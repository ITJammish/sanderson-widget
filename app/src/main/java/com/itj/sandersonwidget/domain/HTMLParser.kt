package com.itj.sandersonwidget.domain

import com.itj.sandersonwidget.domain.model.Article
import com.itj.sandersonwidget.domain.model.ProgressItem
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode

/**
 * Parses data based on html implementation of https://www.brandonsanderson.com/ on 25/03/2023
 */
class HTMLParser {

    companion object {
        private const val PROGRESS_BAR_LABEL = "vc_label"
        private const val PROGRESS_BAR_CLASS = "vc_bar"
        private const val PROGRESS_BAR_ATTRIBUTE = "data-percentage-value"

        private const val POST_NAME_CLASS = "entry-title"
        private const val POST_NAME_ATTRIBUTE_TAG = "a"
        private const val POST_NAME_ATTRIBUTE_HREF = "href"
        private const val POST_URL_CLASS = "blog-thumb-lazy-load preload-me lazy-load aspect"
        private const val POST_URL_CLASS_IMG_ATTRIBUTE = "data-srcset"
    }

    internal fun parseProjectProgress(document: Document): List<ProgressItem> = with(document) {
        val projectTitles = getElementsByClass(PROGRESS_BAR_LABEL).map {
            (it.childNode(0) as TextNode).text().trim()
        }
        val projectProgress = getElementsByClass(PROGRESS_BAR_CLASS).map {
            it.attr(PROGRESS_BAR_ATTRIBUTE).trim()
        }

        return@with projectTitles.zip(projectProgress)
            .map { pair -> ProgressItem(label = pair.first, progressPercentage = pair.second) }
    }

    internal fun parseArticles(document: Document, articleCount: Int): List<Article> = with(document) {
        val postNames = getElementsByClass(POST_NAME_CLASS).select(POST_NAME_ATTRIBUTE_TAG)
            .take(articleCount)
            .map {
                Pair(it.text(), it.attr(POST_NAME_ATTRIBUTE_HREF))
            }
        val thumbnailUrls = getElementsByClass(POST_URL_CLASS)
            .take(articleCount)
            .map {
                it.attr(POST_URL_CLASS_IMG_ATTRIBUTE).split(",")[0].split(" ")[0]
            }

        return@with postNames.zip(thumbnailUrls)
            .map { pair -> Article(pair.first.first, pair.first.second, pair.second) }
    }
}
