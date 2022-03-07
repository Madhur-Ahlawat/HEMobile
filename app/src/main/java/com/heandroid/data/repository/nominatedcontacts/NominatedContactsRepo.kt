package com.heandroid.data.repository.nominatedcontacts

import com.heandroid.data.model.nominatedcontacts.*
import com.heandroid.data.remote.ApiService
import javax.inject.Inject


class NominatedContactsRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun createSecondaryAccount(model: CreateAccountRequestModel?) = apiService.createSecondaryAccount(model)

    suspend fun updateAccessRight(body: UpdateAccessRightModel?) = apiService.updateAccessRight(body)


    suspend fun updateSecondaryAccount(body: CreateAccountRequestModel?) = apiService.updateSecondaryAccount(body)


    suspend fun getSecondaryAccount() = apiService.getSecondaryAccount()

    suspend fun getSecondaryAccessRights(accountId: String) = apiService.getSecondaryAccessRights(accountId)




}