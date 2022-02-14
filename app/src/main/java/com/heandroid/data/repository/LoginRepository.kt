package com.heandroid.data.repository

import com.example.dummyapplication.data.remote.ApiService
import javax.inject.Inject


class LoginRepository  @Inject constructor(private val apiService: ApiService)  {

     suspend fun loginApiCall(clientID: String, grantType: String, agencyId: String, clientSecret: String, value: String, password: String, validatePasswordCompliance: String)=
        apiService.loginWithField(clientID, grantType, agencyId, clientSecret, value, password, validatePasswordCompliance)
}