package com.conduent.nationalhighways.data.repository.checkpaidcrossings

import com.conduent.nationalhighways.data.model.checkpaidcrossings.BalanceTransferRequest
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsRequest
import com.conduent.nationalhighways.data.model.checkpaidcrossings.UsedTollTransactionsRequest
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class CheckPaidCrossingsRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun loginWithRefAndPlateNumber(request: CheckPaidCrossingsRequest?) = apiService.loginWithRefAndPlateNumber(request)

    suspend fun balanceTransfer(request: BalanceTransferRequest?) = apiService.balanceTransfer(request)

    suspend fun getTollTransactions(request: UsedTollTransactionsRequest?) = apiService.getTollTransactions(request)

    suspend fun getVehicleDetail(vehicleNumber: String?, agencyId: Int?) = apiService.getAccountFindVehicle(vehicleNumber, agencyId)


}