package com.heandroid.ui.bottomnav.notification

import com.heandroid.data.remote.ApiService
import com.heandroid.utils.common.Constants
import javax.inject.Inject

class NotificationViewAllRepo @Inject constructor(private val apiService: ApiService) {
    suspend fun deleteAlertItem() = apiService.dismissAlert(Constants.ALERT_ITEM_KEY)
}