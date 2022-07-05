package com.heandroid.data.model.nominatedcontacts

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class NominatedContactRes(
    @SerializedName("secondarAccountDetailsType")
    val secondaryAccountDetailsType: SecondaryAccountDetailsType?,
    @SerializedName("statusCode") val statusCode: String?,
    @SerializedName("message") val message: String?
)

data class SecondaryAccountDetailsType(
    @SerializedName("secondarAccountDetails")
    val secondaryAccountList: MutableList<SecondaryAccountData?>?
)

@Parcelize
data class SecondaryAccountData(
    @SerializedName("secAccountRowId") var secAccountRowId: String?,
    @SerializedName("emailAddress") var emailAddress: String?,
    @SerializedName("accountStatus") var accountStatus: String?,
    @SerializedName("firstName") var firstName: String?,
    @SerializedName("lastName") var lastName: String?,
    @SerializedName("phoneNumber") var phoneNumber: String?,
    @SerializedName("securityCode") var securityCode: String?,
    @SerializedName("referenceId") var referenceId: String?,
    var mPermissionLevel: String? = "-",
    var isExpanded: Boolean? = false
) : Parcelable


data class UpdateSecAccountDetails(
    @SerializedName("accountId") val accountId: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("cellPhoneNumber") val mobileNumber: String?,
    @SerializedName("emailId") val emailId: String?
)

data class GetSecondaryAccessRightsResp(
    @SerializedName("accessRights")
    val accessRights: GetSecAccessRightsVo?
)

data class GetSecAccessRightsVo(
    @SerializedName("accessVo")
    val accessVo: MutableList<GetAccessVo?>?
)

data class GetAccessVo(
    @SerializedName("entity") val entity: String?,
    @SerializedName("value") val value: String?,
    @SerializedName("rowId") val rowId: String?
)