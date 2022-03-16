package com.heandroid.data.model.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class CaseEnquiryResponse(
    val serviceRequestList: ServiceRequestList,
    val statusCode: String,
    val message: String
)

data class ServiceRequestList(
    val serviceRequest: List<ServiceRequest>,
    val count: String
)

@Parcelize
data class ServiceRequest(
    val id: String,
    val created: String,
    val status: String,
    val category: String,
    val subcategory: String,
    val description: String,
    val fileList: List<String>?,
    val fileUploadList: List<String>?,
    val date: String,
    val response: String
) : Parcelable
