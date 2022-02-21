package com.heandroid.data.remote

import com.heandroid.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("http://10.190.176.7:8080/oauth/token")
    suspend fun loginWithField(
        @Field("client_id") clientId: String,
        @Field("grant_type") grant_type: String,
        @Field("agencyID") agencyID: String,
        @Field("client_secret") client_secret: String,
        @Field("value") value: String,
        @Field("password") password: String,
        @Field("validatePasswordCompliance") validatePasswordCompliance: String,
    ): Response<LoginResponse>
}