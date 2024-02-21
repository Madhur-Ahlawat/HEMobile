package com.conduent.nationalhighways.data.model.raiseEnquiry

import java.io.File

data class EnquiryRequest(
    val fname: String,
    val lname: String,
    val eid: String,
    val phoneNo: String,
    val phoneNoCountryCode: String,
    val otherDetails: String,
    val selectSubArea: String,
    val seletedArea: String,
    val fileNames: ArrayList<String> = ArrayList()
) {
}