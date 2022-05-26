package com.itj.sandersonwidget.ui.helper

import android.content.Context
import android.os.Build
import com.itj.sandersonwidget.R

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
    val defaultColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.getColor(android.R.color.white)
    } else {
        context.resources.getColor(android.R.color.white)
    }

    val attrs = intArrayOf(R.attr.appWidgetTextColor)
    val styledAttr = context.obtainStyledAttributes(themeResId, attrs)
    val textColor = styledAttr.getColor(0, defaultColor)
    styledAttr.recycle()

    val attrs1 = intArrayOf(R.attr.appWidgetProgressBarColor)
    val styledAttr1 =
        context.obtainStyledAttributes(themeResId, attrs1)
    val progressColor = styledAttr1.getColor(0, defaultColor)
    styledAttr1.recycle()

    return ThemeColors(textColor = textColor, progressColor = progressColor)
}

internal fun Int.fetchThemeResId(): Int {
    return when (this) {
        BlankId -> R.style.Theme_SandersonWidget_AppWidgetContainer_Blank
        WayOfKingsId -> R.style.Theme_SandersonWidget_AppWidgetContainer_WayOfKings
        WordsOfRadianceId -> R.style.Theme_SandersonWidget_AppWidgetContainer_WordsOfRadiance
        OathbringerId -> R.style.Theme_SandersonWidget_AppWidgetContainer_Oathbringer
        RosharId -> R.style.Theme_SandersonWidget_AppWidgetContainer_Roshar
        else -> R.style.Theme_SandersonWidget_AppWidgetContainer_WayOfKings
    }
}
