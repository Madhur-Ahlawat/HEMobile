package com.heandroid.data.model.account

data class AccountDetailsRequesstModel(var fullName:String="", var telePhoneNumber:String="", var eveningNo:String="", var existingNumber:String="",
                                       var postCode:String="", var address:String="", var createPassword:String="", var confirmPassword:String="",
                                       var pin:String="", var  enable:Boolean =true)