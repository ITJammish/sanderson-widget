package com.itj.sandersonwidget.utils

import android.util.Log

private const val WIDGET_LOG_TAG = "SandersonWidget"

fun log(errorMessage: String) {
    Log.e(WIDGET_LOG_TAG, "ERROR: $errorMessage")
}
