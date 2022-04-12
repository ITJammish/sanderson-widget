package com.itj.sandersonwidget.utils

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import com.itj.sandersonwidget.ProgressBarsWidgetProvider

class IntentProviderImpl : IntentProvider {

    override fun fetchUpdateWidgetIntent(context: Context): Intent {
        return Intent(context.applicationContext, ProgressBarsWidgetProvider::class.java).also {
            it.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
    }
}
