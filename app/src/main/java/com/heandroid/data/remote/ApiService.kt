package com.heandroid.data.remote

import com.example.dummyapplication.data.model.response.LoginResponse
import com.heandroid.BuildConfig
import com.heandroid.data.model.request.auth.forgot.email.ForgotEmailModel
import com.heandroid.data.model.request.auth.forgot.password.ConfirmOptionModel
import com.heandroid.data.model.response.auth.AuthResponseModel
import com.heandroid.data.model.response.auth.forgot.email.ForgotEmailResponseModel
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun login(@Field("client_id") clientId: String?= BuildConfig.CLIENT_ID,
                      @Field("grant_type") grant_type: String?=BuildConfig.GRANT_TYPE,
                      @Field("agencyID") agencyID: String?=BuildConfig.LOGIN_AGENCY_ID,
                      @Field("client_secret") client_secret: String?=BuildConfig.CLIENT_SECRET,
                      @Field("value") value: String?,
                      @Field("password") password: String?,
                      @Field("validatePasswordCompliance") validatePasswordCompliance: String?): Response<LoginResponse?>?


    @DELETE("oauth/token/revoke")
    suspend fun logout() : Response<AuthResponseModel?>


    @POST("forgotUserDetails")
    suspend fun forgotEmail(@Query("agencyId") agencyId: String?=BuildConfig.FORGOT_AGENCY_ID,
                            @Body body: ForgotEmailModel?) : Response<ForgotEmailResponseModel?>?


    @POST("forgotPassword/confirmationOptions")
    suspend fun confirmOptionForForgot(@Query("agencyId") agencyId: String=BuildConfig.FORGOT_AGENCY_ID,
                               @Body body: ConfirmOptionModel?) : Response<ConfirmOptionModel?>?

}