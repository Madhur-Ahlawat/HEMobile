package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
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
    var lastName: String? = null): Parcelable
}