package com.heandroid.data.model.nominatedcontacts

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class NominatedContactRes(
    @SerializedName("secondarAccountDetailsType") val secondaryAccountDetailsType: SecondaryAccountDetailsType?,
    @SerializedName("statusCode") val statusCode: String,
    @SerializedName("message") val message: String
)

data class SecondaryAccountDetailsType(@SerializedName("secondarAccountDetails") val secondaryAccountList: MutableList<SecondaryAccountData?>?)


@Parcelize
data class SecondaryAccountData(
    @SerializedName("secAccountRowId") val secAccountRowId: String,
    @SerializedName("emailAddress") val emailAddress: String,
    @SerializedName("accountStatus") val accountStatus: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    var mPermissionLevel: String? = "-",
    var isExpanded: Boolean = false
) : Parcelable






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