package com.itj.sandersonwidget.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE

class SharedPreferencesStorage(context: Context) : Storage {

    companion object {
        private const val PREFS_NAME = "com.itj.sandersonwidget.ProgressBars"
        private const val PROJECT_ITEMS = "project_items"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    override fun store(items: List<String>) {
        with(sharedPreferences.edit()) {
            putStringSet(PROJECT_ITEMS, items.toMutableSet())
            apply()
        }
    }

    override fun retrieve(): List<String> {
        return sharedPreferences.getStringSet(PROJECT_ITEMS, emptySet())?.toList() ?: emptyList()
    }
}
