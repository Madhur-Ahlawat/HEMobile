package com.conduent.nationalhighways.data.model.contactdartcharge

data class CreateNewCaseReq(
    val fname:String?="",
    val lname:String?="",
    val eid:String?="",
    val phoneNo:String?="",
    val accountNo:String?="",
    val otherDetails:String?,
    val selectSubArea:String?,
    val seletedArea:String?,
    val fileNames:List<String>?,
    val language:String?="",val phoneNoCountryCode:String="+44")
