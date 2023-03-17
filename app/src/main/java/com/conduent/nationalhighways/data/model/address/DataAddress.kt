package com.conduent.nationalhighways.data.model.address

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataAddress(

    @SerializedName("organisation") var organisation: String? = "",
    @SerializedName("poperty") var poperty: String? = "",
    @SerializedName("street") var street: String? = "",
    @SerializedName("locality") var locality: String? = "0",
    @SerializedName("town") var town: String? = "",
    @SerializedName("country") val country: String? = "",
    @SerializedName("postcode") var postcode: String? = ""
): Parcelable




