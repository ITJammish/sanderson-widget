package com.itj.sandersonwidget.network

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.StringRequest
import com.itj.sandersonwidget.domain.WebScraperResponseHandler

private const val TARGET_PAGE_URL = "https://www.brandonsanderson.com/"

internal fun getStringRequest(context: Context): StringRequest {
    return StringRequest(
        TARGET_PAGE_URL,
        { response -> WebScraperResponseHandler(context).handleWebScrapedResponse(response) },
        {
            // TODO add real logging/error handling
            Log.d("JamesDebug:", "ERROR: $it")
        },
    )
}
