package com.conduent.nationalhighways.data.model.raiseEnquiry

import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel

data class EnquiryModel(
    var category: CaseCategoriesModel = CaseCategoriesModel("", ""),
    var subCategory: CaseCategoriesModel = CaseCategoriesModel("", ""),
    var comments: String = "",
    var name: String = "",
    var email: String = "",
    var countryCode: String = "",
    var mobileNumber: String = "",
    var vehicleRegistration: String = "",

    ) {
}