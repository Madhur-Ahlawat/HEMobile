package com.conduent.nationalhighways.data.repository.auth

import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.data.model.auth.forgot.email.ForgotEmailModel
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject


class ForgotEmailRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun forgotEmail(model: ForgotEmailModel?) =
        apiService.forgotEmail(BuildConfig.AGENCY_ID, model)

}