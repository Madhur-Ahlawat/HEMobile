package com.heandroid.data.repository.account

import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class AccountCreationRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getAddressListForPostalCode(postCode: String) =
        apiService.getAddressListBasedOnPostalCode(postCode = postCode)

    suspend fun getCountriesList() = apiService.getCountriesList()

    suspend fun getCountryCodesList() = apiService.getCountryCodes()
}