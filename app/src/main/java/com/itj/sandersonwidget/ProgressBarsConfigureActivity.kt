package com.itj.sandersonwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.itj.sandersonwidget.databinding.ActivityProgressBarsConfigureBinding
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage
import com.itj.sandersonwidget.domain.storage.Storage

/**
 * The configuration screen for the [ProgressBarsWidgetProvider] AppWidget.
 *
 * Keeping up with Brandon
 *
 * Toggles:
 * - Show progress bars
 * - Show articles
 * - Receive notifications for:
 *      - Progress updates
 *      - New articles
 *
 * Styles:
 * - Themes/backgrounds from major book series?
 */
class ProgressBarsConfigureActivity : Activity() {
    private lateinit var sharedPreferences: Storage

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var onClickListener = View.OnClickListener {
        val context = this@ProgressBarsConfigureActivity

        // When the button is clicked, store the prefs locally
        val articlesEnabled = binding.articleSwitch.isChecked
        sharedPreferences.storeArticlesEnabled(articlesEnabled)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
    // Todo make matching nicer than hardcoded and error prone Strings
    private var onThemeSpinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val chosenTheme = when(parent?.getItemAtPosition(position)) {
                "Way of Kings" -> R.style.Theme_SandersonWidget_AppWidgetContainer_WayOfKings
                "Words of Radiance" -> R.style.Theme_SandersonWidget_AppWidgetContainer_WordsOfRadiance
                "Oathbringer" -> R.style.Theme_SandersonWidget_AppWidgetContainer_Oathbringer
                else -> R.style.Theme_SandersonWidget_AppWidgetContainer_WayOfKings
            }
            sharedPreferences.storeTheme(chosenTheme)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Do nothing
        }
    }
    private lateinit var binding: ActivityProgressBarsConfigureBinding

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        sharedPreferences = SharedPreferencesStorage(this)
        binding = ActivityProgressBarsConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addButton.setOnClickListener(onClickListener)
        with(binding.themeSpinner) {
            adapter = ArrayAdapter.createFromResource(
                this@ProgressBarsConfigureActivity,
                R.array.themes_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            onItemSelectedListener = onThemeSpinnerItemSelectedListener
        }

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        binding.articleSwitch.isChecked = sharedPreferences.retrieveArticlesEnabled()
    }
}
