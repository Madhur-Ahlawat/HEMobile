package com.conduent.nationalhighways.data.repository.dashboard

import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.utils.common.Constants
import javax.inject.Inject

class DashBoardRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getVehicleData() = apiService.getVehicleData(startIndex = "1", count = "100")

    suspend fun getAlertMessages() = apiService.getAlertMessages(Constants.LANGUAGE)

    suspend fun crossingHistoryApiCall(requestParam: CrossingHistoryRequest?) =
        apiService.getVehicleCrossingHistoryData(requestParam)

    suspend fun getAccountDetailsApiCall() = apiService.getAccountDetailsData()
    suspend fun getThresholdAmountApiCAll() = apiService.getThresholdValue()

}