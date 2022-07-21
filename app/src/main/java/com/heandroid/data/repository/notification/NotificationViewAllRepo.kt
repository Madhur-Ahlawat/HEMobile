package com.heandroid.data.repository.notification

import com.heandroid.data.remote.ApiService
import com.heandroid.utils.common.Constants
import javax.inject.Inject

class NotificationViewAllRepo @Inject constructor(private val apiService: ApiService) {
    suspend fun deleteAlertItem(cssLookUpKey:String) = apiService.dismissAlert(cssLookUpKey)
    suspend fun readAlertItem(cssLookUpKey:String) = apiService.readAlert(cssLookUpKey)
}