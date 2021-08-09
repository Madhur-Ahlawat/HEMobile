package com.heandroid

import com.heandroid.data.LoginRequest
import com.heandroid.data.LoginResponse
import com.heandroid.utils.Constants
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {


    @FormUrlEncoded
    @POST("https://maas-test.services.conduent.com/oauth/token")
    fun loginWithField(@Field("client_id") clientId:String,
    @Field("grant_type") grant_type:String,
    @Field("agencyID") agencyID:String,
    @Field("client_secret") client_secret:String,
    @Field("value") value:String,
    @Field("password") password:String,
    @Field("validatePasswordCompliance") validatePasswordCompliance:String):Call<LoginResponse>


    //this is not working
//    @FormUrlEncoded
//    @POST("https://maas-test.services.conduent.com/oauth/token")
//    fun login(@FieldMap params: Map<String, String>): Call<LoginResponse>

}
