package com.conduent.nationalhighways.data.repository.auth

import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.data.model.auth.forgot.password.ConfirmOptionModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.ResetPasswordModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpReq
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject


class ForgotPasswordRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun confirmOptionForForgot(model: ConfirmOptionModel?) =
        apiService.confirmOptionForForgot(BuildConfig.AGENCY_ID, model)

    suspend fun requestOTP(model: RequestOTPModel?) =
        apiService.requestOTP(BuildConfig.AGENCY_ID, model)

    suspend fun verifyRequestCode(model: VerifyRequestOtpReq?) = apiService.verifyRequestCode(model)
    suspend fun resetPassword(model: ResetPasswordModel?) =
        apiService.resetPassword(BuildConfig.AGENCY_ID, model)

    suspend fun towFAConfirmOption() = apiService.twoFAConfirmOption(BuildConfig.AGENCY_ID)
    suspend fun twoFARequestOTP(model: RequestOTPModel?) =
        apiService.twoFARequestCode(BuildConfig.AGENCY_ID, model)

    suspend fun twoFAVerifyOTP(model: VerifyRequestOtpReq) =
        apiService.twoFAVerifyRequestCode(BuildConfig.AGENCY_ID, model)

}