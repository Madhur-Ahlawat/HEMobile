package com.heandroid.network

import com.heandroid.model.*
import com.heandroid.utils.Constants
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun loginWithField(
        @Field("client_id") clientId: String,
        @Field("grant_type") grant_type: String,
        @Field("agencyID") agencyID: String,
        @Field("client_secret") client_secret: String,
        @Field("value") value: String,
        @Field("password") password: String,
        @Field("validatePasswordCompliance") validatePasswordCompliance: String,
    ): Response<LoginResponse>

    @GET("bosuser/api/account/vehicle")
    suspend fun getVehicleData(@Header("Authorization") token: String): Response<List<VehicleResponse>>

    @GET("https://maas-test.services.conduent.com/bosuser/api/account/overview")
    suspend fun getAccountOverview(@Header("Authorization") token: String): Response<AccountResponse>

    @FormUrlEncoded
    @POST("https://maas-test.services.conduent.com/oauth/token")
    suspend fun loginUser(@FieldMap body: HashMap<String, String>): Response<LoginResponse>

    @FormUrlEncoded
    @POST("https://maas-test.services.conduent.com/oauth/token")
    suspend fun getRenewalAccessToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("agencyID") agencyId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String,
        @Field("validatePasswordCompliance") validatePasswordCompliance: String
    ): Response<LoginResponse>

    @POST("https://maas-test.services.conduent.com/payments/api/account/retrievepaymentslist")
    suspend fun retrievePaymentListApi(
        @Header("Authorization") token: String,
        @Body requestParam: RetrievePaymentListRequest
    ): Response<RetrievePaymentListApiResponse>

    @POST("https://maas-test.services.conduent.com/trips/api/transactions")
    suspend fun getMonthlyUsageApi(
        @Header("Authorization") token: String,
        @Body requestParam: RetrievePaymentListRequest
    ): Response<RetrievePaymentListApiResponse>

    @POST("https://maas-test.services.conduent.com/bosuser/api/account/forgotUserDetails")
    suspend fun recoveruserNameApi(
        @Query("agencyId") agencyId: String,
        @Body requestParam: ForgotUsernameRequest
    ): Response<ForgotUsernameApiResponse>

    @POST("https://maas-test.services.conduent.com/bosuser/api/account/forgotPassword/confirmationOptions")
    suspend fun forgotPasswordConfirmationOptionsApi(
        @Query("agencyId") agencyId: String,
        @Body requestParam: ConfirmationOptionRequestModel
    ): Response<ConfirmationOptionsResponseModel>

    @POST("https://maas-test.services.conduent.com/bosuser/api/account/forgotPassword/requestReset")
    suspend fun getSecurityCodeFromOptionApi(
        @Query("agencyId") agencyId: String,
        @Body requestPram: GetSecurityCodeRequestModel
    ): Response<GetSecurityCodeResponseModel>

    @POST("https://maas-test.services.conduent.com/bosuser/api/v1/account/forgotPassword/verifyOTP")
    suspend fun verifySecurityCodeApi(@Body requestParam: VerifySecurityCodeRequestModel): Response<VerifySecurityCodeResponseModel>

    @POST("https://maas-test.services.conduent.com/bosuser/api/v2/account/forgotPassword/setNewPassword")
    suspend fun setNewPasswordApi(@Body requestParam: SetNewPasswordRequest): Response<VerifySecurityCodeResponseModel>

    @POST("bosuser/api/account/getAlertMessages")
    suspend fun getAlertMessages(
        @Header("Authorization") token: String,
        @Query("language") language: String
    ): Response<AlertMessageApiResponse>

}
