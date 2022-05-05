package com.heandroid.data.repository.auth

import com.heandroid.BuildConfig
import com.heandroid.data.model.auth.forgot.email.ForgotEmailModel
import com.heandroid.data.remote.ApiService
import javax.inject.Inject


class ForgotEmailRepository @Inject constructor(private val apiService: ApiService)  {

    suspend fun forgotEmail(model: ForgotEmailModel?) = apiService.forgotEmail(BuildConfig.AGENCY_ID,model)

}