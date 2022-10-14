package com.conduent.nationalhighways.data.repository.notification

import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class NotificationViewAllRepo @Inject constructor(private val apiService: ApiService) {
    suspend fun deleteAlertItem(cssLookUpKey:String) = apiService.dismissAlert(cssLookUpKey)
    suspend fun readAlertItem(cssLookUpKey:String) = apiService.readAlert(cssLookUpKey)
}