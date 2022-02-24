package com.heandroid.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class GetSecurityCodeResponseModel(

    val code : String?,
    val otpExpiryInSeconds : Long?,
    val referenceId : String?,
    val successful : Boolean

) : Parcelable