package com.conduent.nationalhighways.data.repository.websiteservice

import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class WebsiteServiceRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun webSiteServiceStatus() = apiService.webSiteServiceStatus()

    suspend fun allowPushNotification(request: PushNotificationRequest) = apiService.allowPushNotification(request)
}