package com.heandroid.data.model.response.auth.forgot.password

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ConfirmOptionResponseModel(var phone : String?,var email: String?,var statusCode: String?,var message: String?,  var address:String?) : Parcelable