package com.conduent.nationalhighways.data.model.nominatedcontacts

import com.google.gson.annotations.SerializedName


data class TerminateRequestModel(
    @SerializedName("accountId") val accountID: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("cellPhoneNumber") val number: String?,
    @SerializedName("emailId") val emailId:String?,
    @SerializedName("firstName") val firstName:String?,
    @SerializedName("lastName") val lastName:String?

)
