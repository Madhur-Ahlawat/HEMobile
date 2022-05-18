package com.heandroid.data.repository.makeoneofpayments

import com.heandroid.data.model.communicationspref.CommunicationPrefsRequestModel
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.makeoneofpayment.CrossingDetailsModelsRequest
import com.heandroid.data.model.makeoneofpayment.OneOfPaymentModelRequest
import com.heandroid.data.model.payment.PaymentReceiptDeliveryTypeSelectionRequest
import com.heandroid.data.remote.ApiService
import com.heandroid.utils.common.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import javax.inject.Inject

class MakeOneOfPaymentRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getCrossingDetails(model:CrossingDetailsModelsRequest) = apiService.getCrossingDetails(model)

    suspend fun oneOfPaymentsPay(model : OneOfPaymentModelRequest) = apiService.oneOfPaymentsPay(model)

    suspend fun whereToReceivePaymentReceipt(request: PaymentReceiptDeliveryTypeSelectionRequest)  = apiService.whereToReceivePaymentReceipt(request)

}