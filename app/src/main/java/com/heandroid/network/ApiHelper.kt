package com.heandroid.network

class ApiHelper(private val apiService: ApiService) {

    suspend fun loginApiCall(clientID:String,
                             grantType:String,
                             agecyId:String,
                             clientSecret:String,
                             value:String,
                             password:String,
                             validatePasswordCompliance:String) = apiService.loginWithField(clientID, grantType, agecyId, clientSecret, value, password, validatePasswordCompliance)

    suspend fun getAccountOverviewApiCall(authToken:String) = apiService.getAccountOverview(authToken)
    suspend fun getVehicleListApiCall(authToken:String) = apiService.getVehicleData(authToken)
    suspend fun getRenewalAccessToken(clientId:String , grantType:String, agencyId:String, clientSecret:String,
                                      refreshToken:String, validatePasswordCompliance: String)=
            apiService.getRenewalAccessToken(clientId , grantType, agencyId, clientSecret, refreshToken, validatePasswordCompliance)
}