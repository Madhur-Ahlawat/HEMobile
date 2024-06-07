package com.conduent.nationalhighways.data.repository.contactdartcharge

import com.conduent.nationalhighways.data.model.contactdartcharge.CaseEnquiryHistoryRequest
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseHistoryRangeModel
import com.conduent.nationalhighways.data.model.contactdartcharge.CreateNewCaseReq
import com.conduent.nationalhighways.data.remote.ApiService
import okhttp3.MultipartBody
import javax.inject.Inject

class ContactDartChargeRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getCaseHistoryDataApiCall(requestParam: CaseEnquiryHistoryRequest?) =
        apiService.getCaseHistoryData(request = requestParam)

    suspend fun getCaseHistoryLoginDataApiCall(requestParam: CaseHistoryRangeModel?) =
        apiService.getLoginCaseHistoryData(request = requestParam)

    suspend fun getCaseCategoriesList() =
        apiService.getCaseCategoriesList()

    suspend fun getCaseSubCategoriesList(cat: String) =
        apiService.getCaseSubCategoriesList(cat)

    suspend fun createNewCase(requestParam: CreateNewCaseReq?) =
        apiService.createNewCase(modelReq = requestParam)

    suspend fun uploadFile(data: MultipartBody.Part?) =
        apiService.uploadFile(data)

}