package com.heandroid.data.repository.auth

import com.heandroid.BuildConfig
import com.heandroid.data.model.request.auth.forgot.password.ConfirmOptionModel
import com.heandroid.data.remote.ApiService
import javax.inject.Inject


class ForgotPasswordRepository @Inject constructor(private val apiService: ApiService)  {

    suspend fun confirmOptionForForgot(model: ConfirmOptionModel?) = apiService.confirmOptionForForgot(BuildConfig.FORGOT_AGENCY_ID,model)


}