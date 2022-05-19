package com.itj.sandersonwidget.ui

import android.content.Context
import android.os.Build
import com.itj.sandersonwidget.R

internal data class ThemeColors(
    val textColor: Int,
    val progressColor: Int,
)

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
