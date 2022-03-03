package com.heandroid.data.model.nominatedcontacts

import com.google.gson.annotations.SerializedName

data class NominatedContactRes(
    @SerializedName("secondarAccountDetailsType") val secondaryAccountDetailsType: SecondaryAccountDetailsType?,
    @SerializedName("statusCode") val statusCode: String,
    @SerializedName("message") val message: String
)

data class SecondaryAccountDetailsType(@SerializedName("secondarAccountDetails") val secondaryAccountList: MutableList<SecondaryAccountData>)

data class SecondaryAccountData(
    @SerializedName("secAccountRowId") val secAccountRowId: String,
    @SerializedName("emailAddress") val emailAddress: String,
    @SerializedName("accountStatus") val accountStatus: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    var isExpanded: Boolean = false
)

data class SecondaryAccountBody(
    val firstName: String,
    val lastName: String,
    val emailId: String,
    val phoneNumber: String,
    val accessType: String = ""
)

data class SecondaryAccountResp(
    @SerializedName("success") val success: Boolean,
    @SerializedName("statusCode") val statusCode: String,
    @SerializedName("message") val message: String,
    @SerializedName("emailStatusCode") val mailStatusCode: String,
    @SerializedName("emailMessage") val emailMessage: String,
    @SerializedName("secondaryAccountId") val secondaryAccountId: String
)

data class UpdateSecAccessRightsReq(
    @SerializedName("action") val action: String,
    @SerializedName("accountId") val accountId: String,
    @SerializedName("updData") val updateList: MutableList<UpdateSecPermissions>
)

data class UpdateSecPermissions(
    @SerializedName("entity") val entity: String,
    @SerializedName("value") val permission: String
)


data class UpdateSecAccountDetails(
    @SerializedName("accountId") val accountId: String,
    @SerializedName("status") val status: String,
    @SerializedName("cellPhoneNumber") val mobileNumber: String,
    @SerializedName("emailId") val emailId: String
)

data class GetSecondaryAccessRightsResp(@SerializedName("accessRights") val accessRights: GetSecAccessRightsVo)

data class GetSecAccessRightsVo(@SerializedName("accessVo") val accessVo: MutableList<GetAccessVo>)

data class GetAccessVo(
    @SerializedName("entity") val entity: String,
    @SerializedName("value") val value: String,
    @SerializedName("rowId") val rowId: String
)