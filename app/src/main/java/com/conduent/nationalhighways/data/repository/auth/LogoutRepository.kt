package com.conduent.nationalhighways.data.repository.auth

import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject


class LogoutRepository @Inject constructor(private val apiService: ApiService)  {

    suspend fun logout() = apiService.logout()

}