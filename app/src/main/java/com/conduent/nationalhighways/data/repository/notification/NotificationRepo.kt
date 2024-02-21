package com.conduent.nationalhighways.data.repository.notification

import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.utils.common.Constants
import javax.inject.Inject

class NotificationRepo @Inject constructor(private val apiService: ApiService) {
    suspend fun deleteAlertItem(cssLookUpKey:String) = apiService.dismissAlert(cssLookUpKey)
    suspend fun readAlertItem(cssLookUpKey:String) = apiService.readAlert(cssLookUpKey)

    suspend fun getAlertMessages() = apiService.getAlertMessages(Constants.LANGUAGE)

}