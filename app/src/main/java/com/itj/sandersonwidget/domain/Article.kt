package com.itj.sandersonwidget.domain

data class Article(
    val title: String,
    val articleUrl: String,
    val thumbnailUrl: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (title != other.title) return false
        if (articleUrl != other.articleUrl) return false
        if (thumbnailUrl != other.thumbnailUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + articleUrl.hashCode()
        result = 31 * result + thumbnailUrl.hashCode()
        return result
    }
}
