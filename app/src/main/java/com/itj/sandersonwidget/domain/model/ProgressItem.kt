package com.itj.sandersonwidget.domain.model

class ProgressItem(
    val label: String,
    val progressPercentage: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProgressItem

        if (label != other.label) return false
        if (progressPercentage != other.progressPercentage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = label.hashCode()
        result = 31 * result + progressPercentage.hashCode()
        return result
    }
}
