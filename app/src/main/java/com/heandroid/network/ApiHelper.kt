package com.heandroid.network

class ApiHelper(private val apiService: ApiService) {

    suspend fun loginApiCall(clientID:String,
                             grantType:String,
                             agecyId:String,
                             clientSecret:String,
                             value:String,
                             password:String,
                             validatePasswordCompliance:String) = apiService.loginWithField(clientID, grantType, agecyId, clientSecret, value, password, validatePasswordCompliance)
}