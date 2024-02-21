package com.conduent.nationalhighways.ui.account.creation.new_account_creation.model

import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse


object NewCreateAccountRequestModel{
    var referenceId: String? = ""
    var emailAddress: String? = ""
    var mobileNumber: String? = ""
    var countryCode: String? = ""
    var telephoneNumber: String? = ""
    var telephone_countryCode: String? = ""
    var communicationTextMessage:Boolean=false
    var termsCondition:Boolean=false
    var twoStepVerification:Boolean=false
    var personalAccount:Boolean=false
    var firstName:String=""
    var lastName:String=""
    var companyName:String=""
    var addressLine1:String=""
    var addressLine2:String=""
    var townCity:String=""
    var state:String=""
    var country:String=""
    var address_country_code:String=""
    var zipCode:String=""
    var selectedAddressId:Int =-1
    var prePay:Boolean=false
    var plateCountry:String = ""
    var plateNumber:String=""
    var plateNumberIsNotInDVLA:Boolean=false
    var vehicleList = mutableListOf<NewVehicleInfoDetails>()
    var addedVehicleList = ArrayList<VehicleResponse?>()
    var addedVehicleList2 = ArrayList<VehicleResponse?>()
    var isRucEligible:Boolean=false
    var isExempted:Boolean=false
    var isVehicleAlreadyAdded:Boolean=false
    var isVehicleAlreadyAddedLocal:Boolean=false
    var isMaxVehicleAdded:Boolean=false
    var isManualAddress = false
    var emailSecurityCode:String=""
    var smsSecurityCode:String=""
    var password:String=""

    var sms_referenceId: String? = ""
    var oneOffVehiclePlateNumber:String=""
    var isCountryNotSupportForSms=false
    var notSupportedCountrySaveDetails=true


}


