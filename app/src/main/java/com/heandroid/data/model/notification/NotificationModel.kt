package com.heandroid.data.model.notification

data class NotificationModel(
    val viewType: Int = 0,
    val message: String,
    val category: String,
    val date: String,
    val high_priority_btn: String,
    val headerViewAll: String = "View all",
    var isRead: Boolean = false, var iSel: Boolean = false
)
