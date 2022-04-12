package com.itj.sandersonwidget.utils

import android.content.ComponentName
import android.content.Context

interface ComponentNameFetcher {

    fun fetchProgressBarsWidgetProviderComponentName(context: Context): ComponentName
}
