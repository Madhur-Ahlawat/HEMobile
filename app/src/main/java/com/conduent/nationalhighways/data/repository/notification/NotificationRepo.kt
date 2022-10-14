package com.conduent.nationalhighways.data.repository.notification

import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.utils.common.Constants
import javax.inject.Inject

class NotificationRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getAlertMessages() = apiService.getAlertMessages(Constants.LANGUAGE)

}