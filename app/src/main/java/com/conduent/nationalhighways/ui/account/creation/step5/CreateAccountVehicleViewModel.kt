package com.conduent.nationalhighways.ui.account.creation.step5

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.GetPlateInfoResponseModel
import com.conduent.nationalhighways.data.model.account.GetPlateInfoResponseModelItem
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.account.ValidVehicleCheckRequest
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.data.repository.auth.CreateAccountRespository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountVehicleViewModel @Inject constructor(
    private val repo: CreateAccountRespository,
    val errorManager: ErrorManager
) : ViewModel() {

    private val findVehicleMutData = MutableLiveData<Resource<VehicleInfoDetails?>?>()
    val findVehicleLiveData: LiveData<Resource<VehicleInfoDetails?>?> get() = findVehicleMutData

    private val findOneOffVehicleMutData =
        MutableLiveData<Resource<ArrayList<NewVehicleInfoDetails>?>?>()
    val findOneOffVehicleLiveData: LiveData<Resource<ArrayList<NewVehicleInfoDetails>?>?> get() = findOneOffVehicleMutData

    private val findVehiclePlateMutData = MutableLiveData<Resource<GetPlateInfoResponseModel?>?>()
    val findVehiclePlateLiveData: LiveData<Resource<GetPlateInfoResponseModel?>?> get() = findVehiclePlateMutData

    private val findNewVehicleMutData = MutableLiveData<Resource<List<NewVehicleInfoDetails?>?>>()
    val findNewVehicleLiveData: LiveData<Resource<List<NewVehicleInfoDetails?>?>> get() = findNewVehicleMutData

    private val validVehicleMutData = MutableLiveData<Resource<String?>?>()
    val validVehicleLiveData: LiveData<Resource<String?>?> get() = validVehicleMutData

    private val heartBeatMutableLiveData = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val heartBeatLiveData: LiveData<Resource<EmptyApiResponse?>?> get() = heartBeatMutableLiveData


    fun getVehicleData(vehicleNumber: String?, agencyId: Int?) {
        viewModelScope.launch {
            try {
                findVehicleMutData.setValue(
                    ResponseHandler.success(
                        repo.getVehicleDetail(
                            vehicleNumber,
                            agencyId
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                findVehicleMutData.setValue(ResponseHandler.failure(e))
            }
        }
    }
    fun getOneOffVehicleData(vehicleNumber: String?, agencyId: Int?) {
        viewModelScope.launch {
            try {
                findOneOffVehicleMutData.setValue(
                    ResponseHandler.success(
                        repo.getOneOffVehicleDetail(
                            vehicleNumber,
                            agencyId
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                findOneOffVehicleMutData.setValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getVehiclePlateData(vehicleNumber: String?, agencyId: Int?) {
        viewModelScope.launch {
            try {
                findVehiclePlateMutData.setValue(
                    ResponseHandler.success(
                        repo.getVehiclePlateInfo(
                            vehicleNumber,
                            agencyId
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                findVehicleMutData.setValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getNewVehicleData(vehicleNumber: String?, agencyId: Int?) {
        viewModelScope.launch {
            try {
                findNewVehicleMutData.postValue(
                    ResponseHandler.success(
                        repo.getNewVehicleDetail(
                            vehicleNumber,
                            agencyId
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                findNewVehicleMutData.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun validVehicleCheck(vehicleValidReqModel: ValidVehicleCheckRequest?, agencyId: Int?) {

        viewModelScope.launch {
            try {
                validVehicleMutData.postValue(
                    ResponseHandler.success(
                        repo.validVehicleCheck(
                            vehicleValidReqModel, agencyId
                        ), errorManager
                    )
                )

            } catch (e: Exception) {
                validVehicleMutData.postValue(ResponseHandler.failure(e))
            }
        }

    }


    fun heartBeat(agencyId:String,referenceId:String) {

        viewModelScope.launch {
            try {
                heartBeatMutableLiveData.postValue(
                    ResponseHandler.success(
                        repo.getHeartBeat(
                            agencyId,referenceId
                        ), errorManager
                    )
                )

            } catch (e: Exception) {
                heartBeatMutableLiveData.postValue(ResponseHandler.failure(e))
            }
        }

    }


}


/* suspend fun getVehicleData(vehicleNumber: String?, agencyId: Int?) {
      viewModelScope.async {
          try {
              findVehicleMutData.setValue(ResponseHandler.success(repo.getVehicleDetail(vehicleNumber, agencyId), errorManager))
              return@async
          } catch (e: Exception) {
              findVehicleMutData.setValue(ResponseHandler.failure(e))
              return@async
          }
      }
  }*/