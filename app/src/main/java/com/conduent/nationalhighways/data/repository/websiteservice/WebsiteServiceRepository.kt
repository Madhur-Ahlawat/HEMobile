package com.conduent.nationalhighways.data.repository.websiteservice

import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class WebsiteServiceRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun webSiteServiceStatus() = apiService.webSiteServiceStatus()
}