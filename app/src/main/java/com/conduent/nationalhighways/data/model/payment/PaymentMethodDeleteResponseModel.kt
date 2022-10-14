package com.conduent.nationalhighways.data.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethodDeleteResponseModel(
    var statusCode: String?,
    var emailMessage: String?,
    var emailStatusCode: String?,
    var message: String?,
    var transactionId: String?
) : Parcelable
