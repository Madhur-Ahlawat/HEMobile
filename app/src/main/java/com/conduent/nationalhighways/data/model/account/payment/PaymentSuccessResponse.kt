package com.conduent.nationalhighways.data.model.account.payment

import com.google.gson.annotations.SerializedName

data class PaymentSuccessResponse(

    @field:SerializedName("cavv")
    val cavv: String? = null,

    @field:SerializedName("xid")
    val xid: Any? = null,

    @field:SerializedName("threeDsVersion")
    val threeDsVersion: String? = null,

    @field:SerializedName("directoryServerId")
    val directoryServerId: String? = null,

    @field:SerializedName("cardHolderAuth")
    val cardHolderAuth: String? = null,

    @field:SerializedName("eci")
    val eci: String? = null
)
