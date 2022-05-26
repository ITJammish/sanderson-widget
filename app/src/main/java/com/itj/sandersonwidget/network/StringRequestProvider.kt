package com.itj.sandersonwidget.network

import android.content.Context
import com.android.volley.toolbox.StringRequest
import com.itj.sandersonwidget.domain.WebScraperResponseHandler
import com.itj.sandersonwidget.utils.log

private const val TARGET_PAGE_URL = "https://www.brandonsanderson.com/"

internal fun getStringRequest(context: Context): StringRequest {
    return StringRequest(
        TARGET_PAGE_URL,
        { response -> WebScraperResponseHandler(context).handleWebScrapedResponse(response) },
        { log("$it") },
    )
}
