package com.heandroid.data.remote

import com.heandroid.BuildConfig
import com.heandroid.data.model.auth.forgot.email.ForgotEmailModel
import com.heandroid.data.model.auth.forgot.password.ConfirmOptionModel
import com.heandroid.data.model.auth.forgot.password.RequestOTPModel
import com.heandroid.data.model.auth.forgot.password.ResetPasswordModel
import com.heandroid.data.model.auth.login.AuthResponseModel
import com.heandroid.data.model.auth.forgot.email.ForgotEmailResponseModel
import com.heandroid.data.model.auth.forgot.password.ConfirmOptionResponseModel
import com.heandroid.data.model.auth.forgot.password.ForgotPasswordResponseModel
import com.heandroid.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.data.model.notification.AlertMessageApiResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    @FormUrlEncoded
    @POST(BuildConfig.LOGIN)
    suspend fun login(
        @Field("client_id") clientId: String? = BuildConfig.CLIENT_ID,
        @Field("grant_type") grant_type: String? = BuildConfig.GRANT_TYPE,
        @Field("agencyID") agencyID: String? = BuildConfig.AGENCY_ID,
        @Field("client_secret") client_secret: String? = BuildConfig.CLIENT_SECRET,
        @Field("value") value: String?,
        @Field("password") password: String?,
        @Field("validatePasswordCompliance") validatePasswordCompliance: String?
    ): Response<LoginResponse?>?


    @DELETE(BuildConfig.LOGOUT)
    suspend fun logout(): Response<AuthResponseModel?>


    @POST(BuildConfig.FORGOT_EMAIL)
    suspend fun forgotEmail(
        @Query("agencyId") agencyId: String?,
        @Body body: ForgotEmailModel?
    ): Response<ForgotEmailResponseModel?>?


    @POST(BuildConfig.FORGOT_CONFIRM_OPTION)
    suspend fun confirmOptionForForgot(
        @Query("agencyId") agencyId: String?,
        @Body body: ConfirmOptionModel?
    ): Response<ConfirmOptionResponseModel?>?


    @POST(BuildConfig.REQUEST_OTP)
    suspend fun requestOTP(
        @Query("agencyId") agencyId: String?,
        @Body model: RequestOTPModel?
    ): Response<SecurityCodeResponseModel?>?

    @POST(BuildConfig.RESET_PASSWORD)
    suspend fun resetPassword(
        @Query("agencyId") agencyId: String?,
        @Body model: ResetPasswordModel?
    ): Response<ForgotPasswordResponseModel?>?

    @GET(BuildConfig.VEHICLE_DATA)
    suspend fun getVehicleData(): Response<List<VehicleResponse>>

    @POST(BuildConfig.GET_ALERT_MESSAGES)
    suspend fun getAlertMessages(@Query("language") language: String): Response<AlertMessageApiResponse>

}