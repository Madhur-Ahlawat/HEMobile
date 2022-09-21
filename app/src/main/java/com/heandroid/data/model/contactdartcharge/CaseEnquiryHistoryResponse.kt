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
    val fileList: List<FileUploadList?>?,
    val fileUploadList: List<String?>?,
    var date: String?,
    val response: String?,
    var time:String?
) : Parcelable

@Parcelize
data class FileUploadList(val filename:String?,val fileId:String?):Parcelable
