package com.itj.sandersonwidget.ui.helper

import android.app.WallpaperManager
import android.app.WallpaperManager.FLAG_SYSTEM
import android.content.Context
import android.os.Build
import com.itj.sandersonwidget.R

/**
 * Themes are disabled as of release 1.0
 */

private const val BlankId = 1
private const val WayOfKingsId = 2
private const val WordsOfRadianceId = 3
private const val OathbringerId = 4
private const val RosharId = 5

internal data class ThemeColors(
    val textColor: Int,
    val progressColor: Int,
)

internal sealed class Theme(val id: Int) {
    object Blank : Theme(BlankId)
    object WayOfKings : Theme(WayOfKingsId)
    object WordsOfRadiance : Theme(WordsOfRadianceId)
    object Oathbringer : Theme(OathbringerId)
    object Roshar : Theme(RosharId)
}

internal fun fetchThemeColors(context: Context, themeResId: Int): ThemeColors {
    val attrs = intArrayOf(R.attr.appWidgetTextColor)
    val styledAttr = context.obtainStyledAttributes(themeResId, attrs)
    val textColor = styledAttr.getColor(0, getDefaultTextColor(context))
    styledAttr.recycle()

    val attrs1 = intArrayOf(R.attr.appWidgetProgressBarColor)
    val styledAttr1 =
        context.obtainStyledAttributes(themeResId, attrs1)
    val progressColor = styledAttr1.getColor(0, getDefaultProgressColor(context))
    styledAttr1.recycle()

    return ThemeColors(textColor = textColor, progressColor = progressColor)
}

private fun getDefaultTextColor(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.getColor(android.R.color.white)
    } else {
        context.resources.getColor(android.R.color.white)
    }
}

private fun getDefaultProgressColor(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        WallpaperManager.getInstance(context).getWallpaperColors(FLAG_SYSTEM)?.primaryColor?.toArgb()
            ?: context.getColor(android.R.color.holo_blue_dark)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.getColor(android.R.color.holo_blue_dark)
    } else {
        context.resources.getColor(android.R.color.holo_blue_dark)
    }
}

internal fun Int.fetchThemeResId(): Int {
    return when (this) {
        BlankId -> R.style.Theme_SandersonWidget_AppWidgetContainer_Blank
//        WayOfKingsId -> R.style.Theme_SandersonWidget_AppWidgetContainer_WayOfKings
//        WordsOfRadianceId -> R.style.Theme_SandersonWidget_AppWidgetContainer_WordsOfRadiance
//        OathbringerId -> R.style.Theme_SandersonWidget_AppWidgetContainer_Oathbringer
//        RosharId -> R.style.Theme_SandersonWidget_AppWidgetContainer_Roshar
        else -> R.style.Theme_SandersonWidget_AppWidgetContainer_Blank
    }
}
