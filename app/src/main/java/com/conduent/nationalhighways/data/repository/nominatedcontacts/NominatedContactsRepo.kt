package com.conduent.nationalhighways.data.repository.nominatedcontacts

import com.conduent.nationalhighways.data.model.nominatedcontacts.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.nominatedcontacts.ResendActivationMail
import com.conduent.nationalhighways.data.model.nominatedcontacts.TerminateRequestModel
import com.conduent.nationalhighways.data.model.nominatedcontacts.UpdateAccessRightModel
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject


class NominatedContactsRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun createSecondaryAccount(model: CreateAccountRequestModel?) =
        apiService.createSecondaryAccount(model)

    suspend fun updateAccessRight(body: UpdateAccessRightModel?) =
        apiService.updateAccessRight(body)


    suspend fun updateSecondaryAccount(body: CreateAccountRequestModel?) =
        apiService.updateSecondaryAccount(body)


    suspend fun getSecondaryAccount() = apiService.getSecondaryAccount()

    suspend fun getSecondaryAccessRights(accountId: String) =
        apiService.getSecondaryAccessRights(accountId)

    suspend fun resendActivationMailContacts(body: ResendActivationMail?) =
        apiService.resendActivationMailContacts(body)

    suspend fun terminateNominatedContact(body: TerminateRequestModel?) =
        apiService.terminateNominatedContact(body)


}