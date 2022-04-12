package com.itj.sandersonwidget.utils

import android.content.ComponentName
import android.content.Context
import com.itj.sandersonwidget.ProgressBarsWidgetProvider

class ComponentNameFetcherImpl : ComponentNameFetcher {

    override fun fetchProgressBarsWidgetProviderComponentName(context: Context): ComponentName {
        return ComponentName(context, ProgressBarsWidgetProvider::class.java)
    }
}
