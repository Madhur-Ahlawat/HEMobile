package com.heandroid.data.model.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardModel(val cardNo: String?,var name: String?,var expiry: String?,var cvv : String?):Parcelable