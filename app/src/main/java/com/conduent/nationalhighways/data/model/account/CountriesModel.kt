package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountriesModel(
    val id: String?,
    val countryCode: String?,
    val countryName: String?
) : Parcelable

@Parcelize
data class CountryCodes(val id: String?, val key: String?, val value: String?) : Parcelable
