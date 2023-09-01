package com.conduent.nationalhighways.data.model.raiseEnquiry

data class EnquiryResponseModel(
    val inquiryId: String = "",
    val statusCode: String = "",
    val message: String = "",
    val srNumber: String = "",
    val emailMessage: String = "",
    val emailStatusCode: String = "",
) {
}