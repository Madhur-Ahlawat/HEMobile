package com.heandroid.repo

import com.heandroid.model.RetrievePaymentListRequest
import com.heandroid.network.ApiHelper

class AppRepository(private val apiHelper: ApiHelper) {

    suspend fun loginUser(clientID:String,
                          grantType:String,
                          agecyId:String,
                          clientSecret:String,
                          value:String,
                          password:String,
                          validatePasswordCompliance:String) = apiHelper.loginApiCall(clientID, grantType, agecyId, clientSecret, value, password, validatePasswordCompliance)

    suspend fun getAccountOverviewApiCall(authToken:String) = apiHelper.getAccountOverviewApiCall(authToken)

    suspend fun getVehicleListInformationApiCall(authToken: String) = apiHelper.getVehicleListApiCall(authToken)

    suspend fun getRenewalAccessToken(clientId:String , grantType:String, agencyId:String, clientSecret:String, refreshToken:String, validatePasswordCompliance: String) =
            apiHelper.getRenewalAccessToken(clientId , grantType, agencyId, clientSecret, refreshToken, validatePasswordCompliance)
    suspend fun retrievePaymentList(authToken:String , requestParam:RetrievePaymentListRequest) = apiHelper.retrievePaymentList(authToken, requestParam)
    suspend fun getMonthlyUsageApiCall(authToken:String , requestParam:RetrievePaymentListRequest) = apiHelper.getMonthlyUsageApiCall(authToken, requestParam)
}