package com.conduent.nationalhighways.data.model.raiseEnquiry

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EnquiryListResponseModel(
    val serviceRequestList: ServiceRequestModel,
    val statusCode: Int
) : Parcelable {
}

@Parcelize
data class ServiceRequestModel(val serviceRequest: ArrayList<ServiceRequest>) : Parcelable {

}

@Parcelize
data class ServiceRequest(
    val id: String? = null,
    val created: String? = null,
    val status: String? = null, val category: String? = null,
    val subcategory: String? = null, val description: String? = null,
    val fileList: ArrayList<FileList> = ArrayList(),
    var closedDate: String? = null
) : Parcelable

@Parcelize
data class FileList(val filename: String, val fileId: String) : Parcelable {

}