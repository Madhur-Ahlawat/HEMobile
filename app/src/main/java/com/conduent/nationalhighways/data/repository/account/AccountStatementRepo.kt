package com.conduent.nationalhighways.data.repository.account

import com.conduent.nationalhighways.data.model.account.ViewStatementsReqModel
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class AccountStatementRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getAccountStatement() = apiService.getAccountStatements()
    suspend fun viewStatements(request : ViewStatementsReqModel) = apiService.viewStatements(request)

}