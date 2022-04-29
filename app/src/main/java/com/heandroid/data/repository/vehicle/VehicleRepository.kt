package com.heandroid.data.repository.vehicle


import com.heandroid.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.vehicle.AddDeleteVehicleGroup
import com.heandroid.data.model.vehicle.DeleteVehicleRequest
import com.heandroid.data.model.vehicle.RenameVehicleGroup
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

    suspend fun downloadCrossingHistoryAPiCall(requestParam: TransactionHistoryDownloadRequest) =
        apiService.getDownloadTransactionListDataInFile(requestParam)

    suspend fun getVehicleListApiCall() = apiService.getVehicleData()

    suspend fun deleteVehicleListApiCall(deleteVehicleRequest: DeleteVehicleRequest) =
        apiService.deleteVehicle(deleteVehicleRequest)

    suspend fun getVehicleGroupListApiCall() =
        apiService.getVehicleGroupList()

    suspend fun addVehicleGroupApiCall(requestParam: AddDeleteVehicleGroup?) =
        apiService.addVehicleGroup(requestParam)

    suspend fun renameVehicleGroupApiCall(requestParam: RenameVehicleGroup) =
        apiService.renameVehicleGroup(requestParam)

    suspend fun deleteVehicleGroupApiCall(requestParam: AddDeleteVehicleGroup) =
        apiService.deleteVehicleGroup(requestParam)

    suspend fun getVehicleListOfGroupApiCall(group: String) =
        apiService.getVehiclesListOfGroup(group)

    suspend fun getSearchVehicleForGroupApiCall(vehicleGroup: String, plateNumber: String) =
        apiService.getSearchVehiclesForGroup(vehicleGroup, plateNumber)

}