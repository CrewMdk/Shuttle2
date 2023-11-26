package com.simplecityapps.provider.emby.http

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QueryResult(
    @Json(name = "Items") val items: List<Item>,
    @Json(name = "TotalRecordCount") val totalRecordCount: Int
)
