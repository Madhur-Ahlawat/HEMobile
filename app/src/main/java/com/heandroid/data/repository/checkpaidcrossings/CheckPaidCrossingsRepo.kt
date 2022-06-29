package com.heandroid.data.repository.checkpaidcrossings

import com.heandroid.data.model.checkpaidcrossings.BalanceTransferRequest
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsRequest
import com.heandroid.data.model.checkpaidcrossings.UsedTollTransactionsRequest
import com.heandroid.data.model.communicationspref.CommunicationPrefsRequestModel
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.remote.ApiService
import com.heandroid.utils.common.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import javax.inject.Inject

class CheckPaidCrossingsRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun loginWithRefAndPlateNumber(request: CheckPaidCrossingsRequest?) = apiService.loginWithRefAndPlateNumber(request)

    suspend fun balanceTransfer(request: BalanceTransferRequest?) = apiService.balanceTransfer(request)

    suspend fun getTollTransactions(request: UsedTollTransactionsRequest?) = apiService.getTollTransactions(request)

    suspend fun getVehicleDetail(vehicleNumber: String?, agencyId: Int?) = apiService.getAccountFindVehicle(vehicleNumber, agencyId)


}