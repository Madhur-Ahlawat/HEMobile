package com.conduent.nationalhighways.data.model.payment

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RetrievePaymentListApiResponse (
	@SerializedName("count") val count:Int?,
	@SerializedName("transactionList") val transactionList :List<PaymentModel>?
):Serializable