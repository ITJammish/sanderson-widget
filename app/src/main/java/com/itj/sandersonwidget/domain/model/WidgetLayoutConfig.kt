package com.itj.sandersonwidget.domain.model

data class WidgetLayoutConfig(
    val gridSize: Pair<String, String>,
    val width: Int,
    val height: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetLayoutConfig

        if (gridSize != other.gridSize) return false
        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gridSize.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}
