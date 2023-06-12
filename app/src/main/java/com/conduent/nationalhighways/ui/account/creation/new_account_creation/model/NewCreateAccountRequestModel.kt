package com.conduent.nationalhighways.ui.account.creation.new_account_creation.model

import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails


object NewCreateAccountRequestModel{
    var referenceId: String? = ""
    var emailAddress: String? = ""
    var mobileNumber: String? = ""
    var communicationTextMessage:Boolean=false
    var termsCondition:Boolean=false
    var twoStepVerification:Boolean=false
    var personalAccount:Boolean=false
    var firstName:String=""
    var lastName:String=""
    var companyName:String=""
    var addressline1:String=""
    var addressline2:String=""
    var townCity:String=""
    var state:String=""
    var country:String=""
    var zipCode:String=""
    var prePay:Boolean=false
    var payAsYouGo:Boolean=false
    var plateCountry:String = ""
    var plateNumber:String=""
    var plateNumberIsNotInDVLA:Boolean=false
    var vehicleList = mutableListOf<NewVehicleInfoDetails>()
    var isRucEligible:Boolean=false
    var isExempted:Boolean=false
    var isVehicleAlreadyAdded:Boolean=false
    var isVehicleAlreadyAddedLocal:Boolean=false




}


