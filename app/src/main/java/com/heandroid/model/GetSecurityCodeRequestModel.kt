package com.heandroid.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class GetSecurityCodeRequestModel(
    val optionType : String,
    val optionValue : String
) :Parcelable{

}
