package com.heandroid.data.repository.notification

import com.heandroid.data.remote.ApiService
import com.heandroid.utils.common.Constants
import javax.inject.Inject

class NotificationRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getAlertMessages() = apiService.getAlertMessages(Constants.LANGUAGE)

}