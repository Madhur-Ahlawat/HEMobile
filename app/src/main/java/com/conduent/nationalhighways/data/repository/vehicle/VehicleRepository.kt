package com.conduent.nationalhighways.data.repository.vehicle


import com.conduent.nationalhighways.data.model.account.ValidVehicleCheckRequest
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.conduent.nationalhighways.data.model.vehicle.AddDeleteVehicleGroup
import com.conduent.nationalhighways.data.model.vehicle.DeleteVehicleRequest
import com.conduent.nationalhighways.data.model.vehicle.RenameVehicleGroup
import com.conduent.nationalhighways.data.model.vehicle.VehicleListManagementEditRequest
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.ui.vehicle.newVehicleManagement.AddVehicleRequest
import javax.inject.Inject

class VehicleRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun addVehicleApiCall(requestParam: VehicleResponse?) =
        apiService.addVehicleApi(requestParam)

    suspend fun addVehicleApiCallNew(requestParam: AddVehicleRequest?) =
        apiService.addVehicleApiNew(requestParam)

    suspend fun updateVehicleApiCall(requestParam: VehicleResponse?) =
        apiService.updateVehicleApi(requestParam)

    suspend fun crossingHistoryApiCall(requestParam: CrossingHistoryRequest?) =
        apiService.getVehicleCrossingHistoryData(requestParam)

    suspend fun downloadCrossingHistoryAPiCall(requestParam: TransactionHistoryDownloadRequest?) =
        apiService.getDownloadTransactionListDataInFile(requestParam)

    suspend fun getVehicleApiCall(start: String, count: String) =
        apiService.getVehicleData(startIndex = start, count = count)
    suspend fun getVehicleListApiCall(start: String, count: String) =
        apiService.getVehicleListData(startIndex = start, count = count)

    suspend fun getUnAllocatedVehiclesApiCall() = apiService.getUnAllocatedVehicles()

    suspend fun deleteVehicleListApiCall(deleteVehicleRequest: DeleteVehicleRequest?) =
        apiService.deleteVehicle(deleteVehicleRequest)

    suspend fun getVehicleGroupListApiCall() =
        apiService.getVehicleGroupList()

    suspend fun addVehicleGroupApiCall(requestParam: AddDeleteVehicleGroup?) =
        apiService.addVehicleGroup(requestParam)

    suspend fun renameVehicleGroupApiCall(requestParam: RenameVehicleGroup?) =
        apiService.renameVehicleGroup(requestParam)

    suspend fun deleteVehicleGroupApiCall(requestParam: AddDeleteVehicleGroup?) =
        apiService.deleteVehicleGroup(requestParam)

    suspend fun getVehicleListOfGroupApiCall(group: String) =
        apiService.getVehiclesListOfGroup(group)

    suspend fun getSearchVehicleForGroupApiCall(vehicleGroup: String, plateNumber: String) =
        apiService.getSearchVehiclesForGroup(vehicleGroup, plateNumber)

    suspend fun getDownloadVehicleList(type: String?) = apiService.getDownloadVehicleList(type)

    suspend fun updateVehicleListManagement(request: VehicleListManagementEditRequest?) =
        apiService.updateVehicleListManagement(request)

    suspend fun getVehicleDetail(vehicleNumber: String?, agencyId: Int?) =
        apiService.getAccountFindVehicle(vehicleNumber, agencyId)

    suspend fun validVehicleCheck(vehicleValidReqModel: ValidVehicleCheckRequest?, agencyId: Int?) =
        apiService.validVehicleCheck(vehicleValidReqModel, agencyId)

}