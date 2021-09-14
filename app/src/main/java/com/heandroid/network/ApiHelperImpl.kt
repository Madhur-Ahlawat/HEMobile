package com.heandroid.network

import com.heandroid.model.RetrievePaymentListRequest

class ApiHelperImpl(private val apiService: ApiService):ApiHelper {

override suspend fun loginApiCall(clientID:String,
                                  grantType:String,
                                  agecyId:String,
                                  clientSecret:String,
                                  value:String,
                                  password:String,
                                  validatePasswordCompliance:String) = apiService.loginWithField(clientID, grantType, agecyId, clientSecret, value, password, validatePasswordCompliance)

override suspend fun getAccountOverviewApiCall(authToken:String) = apiService.getAccountOverview(authToken)
override suspend fun getVehicleListApiCall(authToken:String) = apiService.getVehicleData(authToken)
override suspend fun getRenewalAccessToken(clientId:String, grantType:String, agencyId:String, clientSecret:String,
                                           refreshToken:String, validatePasswordCompliance: String)=
    apiService.getRenewalAccessToken(clientId , grantType, agencyId, clientSecret, refreshToken, validatePasswordCompliance)

override suspend fun retrievePaymentList(authToken: String, requestParam: RetrievePaymentListRequest) = apiService.retrievePaymentListApi(authToken,requestParam)
override suspend fun getMonthlyUsageApiCall(authToken: String, requestParam: RetrievePaymentListRequest) = apiService.getMonthlyUsageApi(authToken,requestParam)


}