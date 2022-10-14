package com.conduent.nationalhighways.data.repository.communicationprefs

import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModel
import com.conduent.nationalhighways.data.model.communicationspref.SearchProcessParamsModelReq
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class CommunicationPrefsRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getAccountSettingsPrefs() = apiService.getAccountSettings()

    suspend fun updateCommunicationSettingsPrefs(model : CommunicationPrefsRequestModel?) = apiService.updateCommunicationPrefs(model)

    suspend fun updateAccountSettingPrefs(model: UpdateProfileRequest?)= apiService.updateAccountSettingPrefs(model)

    suspend fun searchProcessParameters(model:SearchProcessParamsModelReq?)= apiService.searchProcessParameters(model)

}