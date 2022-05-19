package com.itj.sandersonwidget.ui.helper

/**
 * https://developer.android.com/guide/topics/appwidgets/layouts
Number of cells (width x height)	Available size in portrait mode (dp)	Available size in landscape mode (dp)
1x1	57x102dp	127x51dp
2x1	130x102dp	269x51dp
3x1	203x102dp	412x51dp
4x1	276x102dp	554x51dp
5x1	349x102dp	697x51dp
5x2	349x220dp	697x117dp
5x3	349x337dp	697x184dp
5x4	349x455dp	697x250dp
...	...	...
n x m	(73n - 16) x (118m - 16)	(142n - 15) x (66m - 15)
 */
/**
 * 1080x1794 - 2.625
 * Grid for pixel2 starting at 2x2:
 *  Cells          2           3           4           5
 *  Width:      144-206     225-317     305-428     385-540
 *  Height:     134-176     209-273     284-369     359-465
 */
/**
 * 1080x2148 - 2.75
 * Grid for pixel4 starting at 2x2:
 *  Cells        2          3           4           5
 *  Width:      130-249     203-382     276-514     349-647
 *  Height:     134-209     193-322     263-434     X
 */
// 134-209  193-273     263-369

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
