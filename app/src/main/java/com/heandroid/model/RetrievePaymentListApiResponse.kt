package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class RetrievePaymentListApiResponse (
	@SerializedName("count") val count:Int,
	@SerializedName("transactionList") val transactionList :List<PaymentModel>
)