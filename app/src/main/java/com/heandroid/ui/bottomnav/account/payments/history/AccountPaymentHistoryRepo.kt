package com.heandroid.ui.bottomnav.account.payments.history

import com.heandroid.data.model.accountpayment.AccountPaymentHistoryRequest
import com.heandroid.data.model.accountpayment.AccountTopUpUpdateThresholdRequest
import com.heandroid.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class AccountPaymentHistoryRepo @Inject constructor(private val apiService: ApiService) {
    suspend fun getAccountPayment(requestParam: AccountPaymentHistoryRequest?) =
        apiService.getPaymentHistoryData(requestParam)

    suspend fun getThresholdAmount() = apiService.getThresholdValuePayment()
    suspend fun updateThresholdAmount(requestParam: AccountTopUpUpdateThresholdRequest?) =
        apiService.updateThresholdValue(requestParam)

    suspend fun downloadPaymentHistoryAPiCall(requestParam: TransactionHistoryDownloadRequest) =
        apiService.getDownloadTransactionListDataInFile(requestParam)

    suspend fun getVehicleListApiCall() = apiService.getVehicleData(startIndex = "1", count = "100")
}