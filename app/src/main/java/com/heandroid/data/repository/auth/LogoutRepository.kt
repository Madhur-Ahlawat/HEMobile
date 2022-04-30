package com.heandroid.data.repository.auth

import com.heandroid.data.remote.ApiService
import javax.inject.Inject


class LogoutRepository @Inject constructor(private val apiService: ApiService)  {

    suspend fun logout() = apiService.logout()

}