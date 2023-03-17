package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * Created by Mohammed Sameer Ahmad .
 */
@Parcelize
class AccountCreateRequestModel: Parcelable {

    @Parcelize
    data class RequestModel(
        var userInfoModel: UserInfoModel = UserInfoModel()
    ): Parcelable

    @Parcelize
    data class UserInfoModel(var firstName: String? = null,
                             var lastName: String? = null,
                             var postCode: String? = null,
                             var userAddress: DataAddress? = null,
                             var isOptedForSmsNotification: Boolean = false,
                             var isTermsConditionAccepted: Boolean = false): Parcelable

    @Parcelize
    data class UserAddress(var organisation: String? = null,
                           var poperty: String? = null,
                           var street: String? = null,
                           var locality: String? = null,
                           var town: String? = null,
                           val country: String? = null,
                           var postcode: String? = null): Parcelable
}