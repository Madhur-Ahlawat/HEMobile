package com.heandroid.data.model.communicationspref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AvailablePaymentsModel(
    val accountType: String?,
    val expirationMonth: String?,
    val expirationYear: String?,
    val maskedAccountNumber: String?,
    val isPrimaryCard:String?,
    val bankAccoutNumber:String?,
    val bankRoutingNumber:String?,
    val paymentSeqNumber:String?,
    val rowId:String?,
    val paymeth:String?,
    val firstName:String?,
    val lastName:String?,
    val billingAddressId:String?,
    val addressLine1:String?,
    val city:String?,
    val state:String?,
    val zipCode:String?,
    val country:String?
):Parcelable
