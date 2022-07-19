package com.heandroid.data.repository.communicationprefs

import com.heandroid.data.model.account.UpdateProfileRequest
import com.heandroid.data.model.communicationspref.CommunicationPrefsRequestModel
import com.heandroid.data.model.communicationspref.SearchProcessParamsModelReq
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.remote.ApiService
import com.heandroid.utils.common.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import javax.inject.Inject

class CommunicationPrefsRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getAccountSettingsPrefs() = apiService.getAccountSettings()

    suspend fun updateCommunicationSettingsPrefs(model : CommunicationPrefsRequestModel?) = apiService.updateCommunicationPrefs(model)

    suspend fun updateAccountSettingPrefs(model: UpdateProfileRequest?)= apiService.updateAccountSettingPrefs(model)

    suspend fun searchProcessParameters(model:SearchProcessParamsModelReq?)= apiService.searchProcessParameters(model)

}