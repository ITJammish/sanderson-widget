package com.itj.sandersonwidget.utils

import android.content.Context
import android.content.Intent

interface IntentProvider {

    fun fetchUpdateWidgetIntent(context: Context): Intent
}
