package com.heandroid.network

import com.heandroid.model.AccountResponse
import com.heandroid.model.LoginResponse
import com.heandroid.model.VehicleResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("https://maas-test.services.conduent.com/oauth/token")
    suspend fun loginWithField(
        @Field("client_id") clientId: String,
        @Field("grant_type") grant_type: String,
        @Field("agencyID") agencyID: String,
        @Field("client_secret") client_secret: String,
        @Field("value") value: String,
        @Field("password") password: String,
        @Field("validatePasswordCompliance") validatePasswordCompliance: String,
    ): Response<LoginResponse>

    @GET("https://maas-test.services.conduent.com/bosuser/api/account/vehicle")
    fun getVehicleData(@Header("Authorization") token: String): Call<List<VehicleResponse>>

    @GET("https://maas-test.services.conduent.com/bosuser/api/account/overview")
    fun getAccountOverview(@Header("Authorization") token: String): Call<AccountResponse>

    @FormUrlEncoded
    @POST("https://maas-test.services.conduent.com/oauth/token")
    suspend fun loginUser(@FieldMap body: HashMap<String, String>): Response<LoginResponse>

}
