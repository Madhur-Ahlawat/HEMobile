package com.heandroid.data.model.payment

import com.google.gson.annotations.SerializedName
import com.heandroid.data.model.payment.PaymentModel
import java.io.Serializable

data class RetrievePaymentListApiResponse (
	@SerializedName("count") val count:Int?,
	@SerializedName("transactionList") val transactionList :List<PaymentModel>?
):Serializable