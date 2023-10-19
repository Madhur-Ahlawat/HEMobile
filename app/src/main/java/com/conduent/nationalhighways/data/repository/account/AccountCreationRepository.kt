package com.conduent.nationalhighways.data.repository.account

import com.conduent.nationalhighways.data.model.account.payment.AccountCreationRequest
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class AccountCreationRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getAddressListForPostalCode(postCode: String) =
        apiService.getAddressListBasedOnPostalCode(postCode = postCode)

    suspend fun getCountriesList() = apiService.getCountriesList()

    suspend fun getCountryCodesList() = apiService.getCountryCodes()

    suspend fun createAccountNew(model: AccountCreationRequest?) =
        apiService.createAccountNew(model = model)


}