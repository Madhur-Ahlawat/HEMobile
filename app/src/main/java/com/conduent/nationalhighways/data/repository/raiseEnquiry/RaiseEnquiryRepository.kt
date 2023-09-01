package com.conduent.nationalhighways.data.repository.raiseEnquiry

import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryRequest
import com.conduent.nationalhighways.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class RaiseEnquiryRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun categoriesApiCall() = apiService.getCategoryList()
    suspend fun subcategoriesApiCall(category: String) = apiService.getSubCategory(category)
    suspend fun raiseEnquiryApi(
        enquiryRequest: EnquiryRequest
    ) =
        apiService.raiseEnquiry(
            enquiryRequest  )



}