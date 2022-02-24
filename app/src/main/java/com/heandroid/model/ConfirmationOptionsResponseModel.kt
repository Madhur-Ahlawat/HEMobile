package com.heandroid.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


@Parcelize
data class ConfirmationOptionsResponseModel(
    val accountNumber:String,
    val email:String,
    val phone:String,
    var statusCode: String?
):Parcelable
