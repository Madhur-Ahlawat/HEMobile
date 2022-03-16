package com.heandroid.data.repository.websiteservice

import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class WebsiteServiceRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun webSiteServiceStatus() = apiService.webSiteServiceStatus()
}