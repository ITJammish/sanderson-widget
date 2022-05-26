package com.itj.sandersonwidget.ui.helper

/**
 * Methods allowing physical widget dimensions to be converted into grid values on a device's home screen. Values used
 * are sourced from Google's documentation:
 * https://developer.android.com/guide/topics/appwidgets/layouts#anatomy_determining_size
 *
 * We're then further abstracting grid ranges into categories:
 *  2 squares = [DimensionSize.Small]
 *  3..4 squares = [DimensionSize.Medium]
 *  5+ squares = [DimensionSize.Large]
 */

private const val KEY_SMALL = "DimensionSize_Small"
private const val KEY_MEDIUM = "DimensionSize_Medium"
private const val KEY_LARGE = "DimensionSize_Large"

internal sealed class DimensionSize(val key: String) {
    object Small : DimensionSize(key = KEY_SMALL)
    object Medium : DimensionSize(key = KEY_MEDIUM)
    object Large : DimensionSize(key = KEY_LARGE)
}

internal data class GridSize(
    val width: DimensionSize,
    val height: DimensionSize,
)

internal fun getGridSizeForKeyPair(pair: Pair<String, String>): GridSize {
    return GridSize(getDimensionSizeForKey(pair.first), getDimensionSizeForKey(pair.second))
}

/**
 * When the device is portrait the width of the widget is parallel to the shorter side of the device:
 *
 *  ------
 *  \    \
 *  \    \ long side (height)
 *  \    \
 *  ------
 * short side (width)
 */
internal fun getGridSizePortrait(width: Int, height: Int): GridSize {
    val widthCells = (width + 16) / 73
    val heightCells = ((height + 16) / 118)
    return GridSize(
        getShortSideCellsForSize(widthCells),
        getLongSideCellsForSize(heightCells),
    )
}

/**
 * When the device is landscape the width of the widget is parallel to the longer side of the device:
 *
 *  \------------\
 *  \            \ short side (height)
 *  \------------\
 *    long side (width)
 */
internal fun getGridSizeLandscape(width: Int, height: Int): GridSize {
    val widthCells = (width + 15) / 142
    val heightCells = (height + 15) / 66
    return GridSize(
        getLongSideCellsForSize(widthCells),
        getShortSideCellsForSize(heightCells),
    )
}

private fun getDimensionSizeForKey(key: String): DimensionSize {
    return when (key) {
        KEY_SMALL -> DimensionSize.Small
        KEY_MEDIUM -> DimensionSize.Medium
        KEY_LARGE -> DimensionSize.Large
        else -> DimensionSize.Small
    }
}

private fun getShortSideCellsForSize(gridSquares: Int): DimensionSize {
    return when (gridSquares) {
        in 0..2 -> DimensionSize.Small
        in 3..4 -> DimensionSize.Medium
        else -> DimensionSize.Large
    }
}

private fun getLongSideCellsForSize(gridSquares: Int): DimensionSize {
    return when (gridSquares + 1) {
        in 0..2 -> DimensionSize.Small
        in 3..4 -> DimensionSize.Medium
        else -> DimensionSize.Large
    }
}
