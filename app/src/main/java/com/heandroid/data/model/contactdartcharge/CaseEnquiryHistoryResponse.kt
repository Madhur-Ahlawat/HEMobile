package com.heandroid.data.model.contactdartcharge

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class CaseEnquiryHistoryResponse(
    val serviceRequestList: ServiceRequestList?,
    val statusCode: String?,
    val message: String?
)

data class ServiceRequestList(
    val serviceRequest: List<ServiceRequest?>?,
    val count: String?
)

@Parcelize
data class ServiceRequest(
    val id: String?,
    val created: String?,
    val status: String?,
    val category: String?,
    val subcategory: String?,
    val description: String?,
    val fileList: List<String?>?,
    val fileUploadList: List<String?>?,
    val date: String?,
    val response: String?
) : Parcelable
