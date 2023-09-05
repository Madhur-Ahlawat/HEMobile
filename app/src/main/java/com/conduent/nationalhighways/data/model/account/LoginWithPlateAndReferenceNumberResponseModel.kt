package com.conduent.nationalhighways.data.model.account

class LoginWithPlateAndReferenceNumberResponseModel : ArrayList<LoginWithPlateAndReferenceNumberResponseModel1Item>()
data class LoginWithPlateAndReferenceNumberResponseModel1Item(
    val accountActStatus: String,
    val accountBalance: String,
    val accountNo: String,
    val accountStatusCd: String,
    val accountType: String,
    val accountTypeCd: String,
    val expirationDate: String,
    val plateCountry: String,
    val plateNo: String,
    val unusedTrip: String,
    val vehicleClass: String
)