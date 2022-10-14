package com.conduent.nationalhighways.data.model.webstatus

data class WebSiteStatus(
    val state: String?,
    val title: String?,
    val message: String?,
    val isActive: String,
    val startTime: String?,
    val endTime: String?
)
