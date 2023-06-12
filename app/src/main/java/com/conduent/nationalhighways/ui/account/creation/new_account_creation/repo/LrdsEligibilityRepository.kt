package com.conduent.nationalhighways.ui.account.creation.new_account_creation.repo

import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.request.LrdsEligibiltyRequest
import javax.inject.Inject

class LrdsEligibilityRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun lrdsEligibiltyCheck(model: LrdsEligibiltyRequest) =
        apiService.LrdsEligibityCheck(model)

}