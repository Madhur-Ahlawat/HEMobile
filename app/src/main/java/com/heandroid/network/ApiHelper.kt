package com.heandroid.network

import com.heandroid.model.*
import retrofit2.Response

interface ApiHelper {

    suspend fun loginApiCall(clientID:String,
                             grantType:String,
                             agecyId:String,
                             clientSecret:String,
                             value:String,
                             password:String,
                             validatePasswordCompliance:String)
            : Response<LoginResponse>
    suspend fun getAccountOverviewApiCall(authToken:String) : Response<AccountResponse>
    suspend fun getVehicleListApiCall(authToken:String) : Response<List<VehicleResponse>>
    suspend fun getRenewalAccessToken(clientId:String , grantType:String, agencyId:String, clientSecret:String,
                                      refreshToken:String, validatePasswordCompliance: String)
            : Response<LoginResponse>

    suspend fun retrievePaymentList(authToken: String , requestParam:RetrievePaymentListRequest):Response<RetrievePaymentListApiResponse>
    suspend fun getMonthlyUsageApiCall(authToken: String , requestParam:RetrievePaymentListRequest):Response<RetrievePaymentListApiResponse>


}