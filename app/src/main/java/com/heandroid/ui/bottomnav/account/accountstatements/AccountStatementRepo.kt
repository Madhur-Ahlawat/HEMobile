package com.heandroid.ui.bottomnav.account.accountstatements

import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class AccountStatementRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getAccountStatement() = apiService.getAccountStatements()

}