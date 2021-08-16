package com.heandroid

import com.heandroid.data.AccountResponse
import com.heandroid.data.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("https://maas-test.services.conduent.com/oauth/token")
    fun loginWithField(
        @Field("client_id") clientId: String,
        @Field("grant_type") grant_type: String,
        @Field("agencyID") agencyID: String,
        @Field("client_secret") client_secret: String,
        @Field("value") value: String,
        @Field("password") password: String,
        @Field("validatePasswordCompliance") validatePasswordCompliance: String,
    ): Call<LoginResponse>

    @GET("https://maas-test.services.conduent.com/bosuser/api/account/overview")
    fun getAccountOverview(@Header("Authorization") token:String): Call<AccountResponse>

}
