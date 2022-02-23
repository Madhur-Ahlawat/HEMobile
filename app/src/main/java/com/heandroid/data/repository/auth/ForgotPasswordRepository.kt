package com.heandroid.data.repository.auth

import android.os.Build
import com.heandroid.BuildConfig
import com.heandroid.data.model.request.auth.forgot.password.ConfirmOptionModel
import com.heandroid.data.model.request.auth.forgot.password.RequestOTPModel
import com.heandroid.data.model.request.auth.forgot.password.ResetPasswordModel
import com.heandroid.data.remote.ApiService
import javax.inject.Inject


class ForgotPasswordRepository @Inject constructor(private val apiService: ApiService)  {

    suspend fun confirmOptionForForgot(model: ConfirmOptionModel?) = apiService.confirmOptionForForgot(BuildConfig.AGENCY_ID,model)
    suspend fun requestOTP(model: RequestOTPModel?) = apiService.requestOTP(BuildConfig.AGENCY_ID,model)
    suspend fun resetPassword(model: ResetPasswordModel?) = apiService.resetPassword(BuildConfig.AGENCY_ID,model)

}