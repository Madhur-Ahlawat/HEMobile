package com.heandroid.data.repository.dashboard

import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.remote.ApiService
import com.heandroid.utils.common.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import javax.inject.Inject

class DashBoardRepo @Inject constructor(private val apiService: ApiService) {


    suspend fun getVehicleData() = apiService.getVehicleData()

    suspend fun getAlertMessages() = apiService.getAlertMessages(Constants.LANGUAGE)

    suspend fun crossingHistoryApiCall(requestParam: CrossingHistoryRequest) =
        apiService.getVehicleCrossingHistoryData(requestParam)

    suspend fun getAccountDetailsApiCall()=apiService.getAccountDetailsData()
    suspend fun getThresholdAmountApiCAll()=apiService.getThresholdValue()

}