package com.conduent.nationalhighways.data.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardResponseModel(
    val card: Card? = null,
    val check: Check? = null,
    val initiatedBy: InitiatedBy? = null,
    val token: String? = null,
    val tokenType: String? = null,
    val wallet: Wallet? = null,
    var checkCheckBox:Boolean=false

) : Parcelable