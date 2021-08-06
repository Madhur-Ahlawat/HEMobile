package com.heandroid

import com.heandroid.data.LoginRequest
import com.heandroid.data.LoginResponse
import com.heandroid.utils.Constants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST(Constants.LOGIN_URL)
    @Headers( "Content-Type: application/json;charset=UTF-8")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}