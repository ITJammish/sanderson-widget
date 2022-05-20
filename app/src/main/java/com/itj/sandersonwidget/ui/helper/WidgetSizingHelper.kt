package com.itj.sandersonwidget.ui.helper

/**
 * https://developer.android.com/guide/topics/appwidgets/layouts
 */
// width: 2 is small, 3-4 is medium, 5+ is large
// height: 1 is small, 2 is medium, 3+ is large
internal sealed class DimensionSize {
    object Small : DimensionSize()
    object Medium : DimensionSize()
    object Large : DimensionSize()
}

internal data class GridSize(
    val width: DimensionSize,
    val height: DimensionSize,
)

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

private fun getPortraitHeightCellsForMedianWidth(minHeight: Int, maxHeight: Int): DimensionSize {
    return when (((maxHeight - minHeight) / 2) + minHeight) {
        in 0..133 -> DimensionSize.Small // unsupported -_-
        in 134..200 -> DimensionSize.Small
        in 201..347 -> DimensionSize.Medium
        else -> DimensionSize.Large
    }
}
