package com.conduent.nationalhighways.data.repository.raiseEnquiry

import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryStatusRequest
import com.conduent.nationalhighways.data.remote.ApiService
import okhttp3.MultipartBody
import org.json.JSONObject
import javax.inject.Inject

class RaiseEnquiryRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun categoriesApiCall() = apiService.getCategoryList()
    suspend fun subcategoriesApiCall(category: String) = apiService.getSubCategory(category)
    suspend fun raiseEnquiryApi(
        enquiryRequest: EnquiryRequest
    ) =
        apiService.raiseEnquiry(
            enquiryRequest
        )

    suspend fun uploadFile(data: MultipartBody.Part?) =
        apiService.uploadFile(data)

    suspend fun getAccountSRList() =
        apiService.GET_ACCOUNT_SR_LIST(JSONObject())

    suspend fun getGeneralAccountSRList(jsonObject: EnquiryStatusRequest) =
        apiService.GET_GENERAL_ACCOUNT_SR_DETAILS(jsonObject)


}