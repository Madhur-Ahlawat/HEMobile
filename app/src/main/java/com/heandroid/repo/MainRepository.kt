package com.heandroid.repo

import com.heandroid.network.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun loginUser(clientID:String,
                          grantType:String,
                          agecyId:String,
                          clientSecret:String,
                          value:String,
                          password:String,
                          validatePasswordCompliance:String) = apiHelper.loginApiCall(clientID, grantType, agecyId, clientSecret, value, password, validatePasswordCompliance)
}