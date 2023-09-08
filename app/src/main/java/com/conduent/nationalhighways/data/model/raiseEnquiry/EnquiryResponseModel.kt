package com.conduent.nationalhighways.data.model.raiseEnquiry

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EnquiryResponseModel(
//    val inquiryId: String = "",
    val statusCode: String = "",
    val message: String = "",
    val srNumber: String = "",
    val emailMessage: String = "",
    val emailStatusCode: String = "",
    var email:String="",
    var category:String=""

):Parcelable {
}