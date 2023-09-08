package com.conduent.nationalhighways.data.model.raiseEnquiry

import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import java.io.File

data class EnquiryModel(
    var category: CaseCategoriesModel = CaseCategoriesModel("", ""),
    var subCategory: CaseCategoriesModel = CaseCategoriesModel("", ""),
    var comments: String = "",
    var name: String = "",
    var email: String = "",
    var countryCode: String = "",
    var fullcountryCode: String = "",
    var mobileNumber: String = "",
    var vehicleRegistration: String = "",
    var fileName: String ="",
    var file: File = File("")

    ) {
}