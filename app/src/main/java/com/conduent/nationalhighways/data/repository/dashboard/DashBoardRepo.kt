package com.conduent.nationalhighways.data.repository.dashboard

import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.CheckedCrossingTransactionsRequestModel
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.payment.PaymentReceiptDeliveryTypeSelectionRequest
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.utils.common.Constants
import javax.inject.Inject

class DashBoardRepo @Inject constructor(private val apiService: ApiService) {
    suspend fun getAccountPayment(requestParam: AccountPaymentHistoryRequest?) =
        apiService.getPaymentHistoryData(requestParam)

    suspend fun getAccountPaymentCheckCrossings(requestParam: CheckedCrossingTransactionsRequestModel?) =
        apiService.getTransactionsListCheckCrossings(requestParam)

    suspend fun logout() = apiService.logout()
    suspend fun getThresholdAmount() = apiService.getThresholdValuePayment()
    suspend fun getVehicleData() = apiService.getVehicleData(startIndex = "1", count = "20")
    suspend fun whereToReceivePaymentReceipt(request: PaymentReceiptDeliveryTypeSelectionRequest?) =
        apiService.whereToReceivePaymentReceipt(request)

    suspend fun getAlertMessages() = apiService.getAlertMessages(Constants.LANGUAGE)

    suspend fun crossingHistoryApiCall(requestParam: CrossingHistoryRequest?) =
        apiService.getVehicleCrossingHistoryData(requestParam)

    suspend fun getAccountDetailsApiCall() = apiService.getAccountDetailsData()
    suspend fun getLrdsStatusApi() = apiService.getLrdsStatus()
    suspend fun changeInActiveStatusApi() = apiService.inActiveStatusApi()
    suspend fun getThresholdAmountApiCAll() = apiService.getThresholdValue()

}