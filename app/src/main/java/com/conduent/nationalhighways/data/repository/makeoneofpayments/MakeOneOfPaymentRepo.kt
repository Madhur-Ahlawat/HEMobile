package com.conduent.nationalhighways.data.repository.makeoneofpayments

import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.OneOfPaymentModelRequest
import com.conduent.nationalhighways.data.model.payment.PaymentReceiptDeliveryTypeSelectionRequest
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class MakeOneOfPaymentRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getCrossingDetails(model: CrossingDetailsModelsRequest?) =
        apiService.getCrossingDetails(model)

    suspend fun oneOfPaymentsPay(model: OneOfPaymentModelRequest?) =
        apiService.oneOfPaymentsPay(model)

    suspend fun whereToReceivePaymentReceipt(request: PaymentReceiptDeliveryTypeSelectionRequest?) =
        apiService.whereToReceivePaymentReceipt(request)

}