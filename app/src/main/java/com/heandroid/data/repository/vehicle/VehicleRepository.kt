package com.heandroid.data.repository.vehicle


import com.heandroid.data.model.crossingHistory.CrossingHistoryDownloadRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.vehicle.DeleteVehicleRequest
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class VehicleRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun addVehicleApiCall(requestParam: VehicleResponse?) =
        apiService.addVehicleApi(requestParam)

    suspend fun updateVehicleApiCall(requestParam: VehicleResponse) =
        apiService.updateVehicleApi(requestParam)

    suspend fun crossingHistoryApiCall(requestParam: CrossingHistoryRequest?) =
        apiService.getVehicleCrossingHistoryData(requestParam)

    suspend fun downloadCrossingHistoryAPiCall(requestParam: CrossingHistoryDownloadRequest) =
        apiService.getDownloadTransactionListDataInFile(requestParam)

    suspend fun getVehicleListApiCall() = apiService.getVehicleData()

    suspend fun deleteVehicleListApiCall(deleteVehicleRequest : DeleteVehicleRequest) = apiService.deleteVehicle(deleteVehicleRequest)

}