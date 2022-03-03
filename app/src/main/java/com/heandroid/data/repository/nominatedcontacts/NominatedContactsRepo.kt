package com.heandroid.data.repository.nominatedcontacts

import com.heandroid.data.model.nominatedcontacts.SecondaryAccountBody
import com.heandroid.data.model.nominatedcontacts.UpdateSecAccessRightsReq
import com.heandroid.data.model.nominatedcontacts.UpdateSecAccountDetails
import com.heandroid.data.remote.ApiService
import javax.inject.Inject


class NominatedContactsRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun createSecondaryAccount(secondaryBody: SecondaryAccountBody) =
        apiService.createSecondaryAccount(secondaryBody)

    suspend fun getSecondaryAccount() = apiService.getSecondaryAccount()

    suspend fun getSecondaryAccessRights(accountId: String) =
        apiService.getSecondaryAccessRights(accountId)

    suspend fun updateSecondaryAccount(body: UpdateSecAccountDetails) =
        apiService.updateSecondaryAccount(body)

    suspend fun updateSecondaryAccessRights(body: UpdateSecAccessRightsReq) =
        apiService.updateSecondaryAccessRights(body)

}