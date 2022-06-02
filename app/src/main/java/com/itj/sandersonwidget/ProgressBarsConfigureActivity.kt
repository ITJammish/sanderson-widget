package com.itj.sandersonwidget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.itj.sandersonwidget.databinding.ActivityProgressBarsConfigureBinding
import com.itj.sandersonwidget.domain.storage.SharedPreferencesStorage
import com.itj.sandersonwidget.domain.storage.Storage
import com.itj.sandersonwidget.ui.helper.Theme.*
import com.itj.sandersonwidget.ui.notifications.NotificationManager

/**
 * The configuration screen for the [ProgressBarsWidgetProvider] AppWidget.
 */
class ProgressBarsConfigureActivity : AppCompatActivity() {

    companion object {
        private const val NO_THEME_CHOSEN = "no_theme_chosen"
    }

    private lateinit var sharedPreferences: Storage
    private lateinit var binding: ActivityProgressBarsConfigureBinding

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var chosenThemeLabel: String = NO_THEME_CHOSEN
    private var onThemeSpinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            chosenThemeLabel = parent?.getItemAtPosition(position) as String? ?: NO_THEME_CHOSEN
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {} // Do nothing
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        sharedPreferences = SharedPreferencesStorage(this)
        binding = ActivityProgressBarsConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NotificationManager().createNotificationChannel(this)
        setAppWidgetId()
        bindViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_configuration, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_add_widget -> onAddWidgetClick()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setAppWidgetId() {
        // Find the widget id from the intent.
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
    }

    private fun bindViews() {
        binding.articleSwitch.isChecked = sharedPreferences.retrieveArticlesEnabled(appWidgetId)
        binding.progressItemsNotificationsSwitch.isChecked =
            sharedPreferences.retrieveProgressUpdateNotificationsEnabled()
        binding.articleNotificationsSwitch.isChecked = sharedPreferences.retrieveArticleUpdateNotificationsEnabled()

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
    }

    private fun onAddWidgetClick() {
        val context = this@ProgressBarsConfigureActivity

        // When the button is clicked, store the prefs locally
        with(sharedPreferences) {
            storeArticlesEnabled(appWidgetId, binding.articleSwitch.isChecked)
            storeProgressUpdateNotificationsEnabled(binding.progressItemsNotificationsSwitch.isChecked)
            storeArticleUpdateNotificationsEnabled(binding.articleNotificationsSwitch.isChecked)
        }

        // Store chosen theme
        if (chosenThemeLabel != NO_THEME_CHOSEN) {
            // User visible values defined in <string-array name="themes_array">
            val chosenTheme = with(context) {
                when (chosenThemeLabel) {
                    getString(R.string.theme_label_blank) -> Blank
                    getString(R.string.theme_label_blank_orange) -> BlankOrange
                    getString(R.string.theme_label_blank_blue) -> BlankBlue
                    getString(R.string.theme_label_blank_green) -> BlankGreen
                    getString(R.string.theme_label_blank_purple) -> BlankPurple
                    getString(R.string.theme_label_blank_red) -> BlankRed
                    else -> Blank
                }
            }
            sharedPreferences.storeTheme(appWidgetId, chosenTheme.id)
        }

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
}
