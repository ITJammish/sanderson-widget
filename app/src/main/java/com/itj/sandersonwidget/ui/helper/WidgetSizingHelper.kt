package com.itj.sandersonwidget.ui.helper

/**
 * https://developer.android.com/guide/topics/appwidgets/layouts
 */
private const val KEY_SMALL = "DimensionSize_Small"
private const val KEY_MEDIUM = "DimensionSize_Medium"
private const val KEY_LARGE = "DimensionSize_Large"

// width: 2 is small, 3-4 is medium, 5+ is large
// height: 1 is small, 2 is medium, 3+ is large
internal sealed class DimensionSize(val key: String) {
    object Small : DimensionSize(key = KEY_SMALL)
    object Medium : DimensionSize(key = KEY_MEDIUM)
    object Large : DimensionSize(key = KEY_LARGE)
}

private fun getDimensionSizeForKey(key: String): DimensionSize {
    return when (key) {
        KEY_SMALL -> DimensionSize.Small
        KEY_MEDIUM -> DimensionSize.Medium
        KEY_LARGE -> DimensionSize.Large
        else -> DimensionSize.Small
    }
}

internal data class GridSize(
    val width: DimensionSize,
    val height: DimensionSize,
)

internal fun getGridSizeForKeyPair(pair: Pair<String, String>): GridSize {
    return GridSize(getDimensionSizeForKey(pair.first), getDimensionSizeForKey(pair.second))
}

// n x m	(73n - 16) x (118m - 16)
internal fun getGridSizePortrait(width: Int, minHeight: Int, maxHeight: Int): GridSize {
    val widthCells = (width + 16) / 73
//        val heightCells = ((height.toDouble() + 16) / 118)
    return GridSize(
        getPortraitWidthCellsForSize(widthCells),
        getPortraitHeightCellsForMedianWidth(minHeight, maxHeight),
    )
}

// todo when I care about landscape
// n x m (142n - 15) x (66m - 15)
internal fun getGridSizeLandscape(width: Int, height: Int): GridSize {
    val widthCells = (width + 15) / 142
    val heightCells = (height + 15) / 66
    return GridSize(DimensionSize.Small, DimensionSize.Small)
}

private fun getPortraitWidthCellsForSize(width: Int): DimensionSize {
    return when (width) {
        in 0..2 -> DimensionSize.Small
        in 3..4 -> DimensionSize.Medium
        else -> DimensionSize.Large
    }
}

// Todo revisit now we know maxWidth is landscape and minWidth is portrait
private fun getPortraitHeightCellsForMedianWidth(minHeight: Int, maxHeight: Int): DimensionSize {
    return when (((maxHeight - minHeight) / 2) + minHeight) {
        in 0..133 -> DimensionSize.Small // unsupported -_-
        in 134..200 -> DimensionSize.Small
        in 201..347 -> DimensionSize.Medium
        else -> DimensionSize.Large
    }
}
