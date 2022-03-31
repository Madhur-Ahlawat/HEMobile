package com.heandroid.data.repository.contactdartcharge

import com.heandroid.data.model.contactdartcharge.CaseEnquiryHistoryRequest
import com.heandroid.data.model.contactdartcharge.CreateNewCaseReq
import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class ContactDartChargeRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getCaseHistoryDataApiCall(requestParam: CaseEnquiryHistoryRequest?) =
        apiService.getCaseHistoryData(request = requestParam)

    suspend fun getCaseCategoriesList() =
        apiService.getCaseCategoriesList()

    suspend fun getCaseSubCategoriesList() =
        apiService.getCaseSubCategoriesList()

    suspend fun createNewCase(requestParam: CreateNewCaseReq?) =
        apiService.createNewCase(modelReq = requestParam)

}