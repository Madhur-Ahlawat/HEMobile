package com.conduent.nationalhighways.data.repository.paymenthistory

import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdRequest
import com.conduent.nationalhighways.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.conduent.nationalhighways.data.remote.ApiService
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