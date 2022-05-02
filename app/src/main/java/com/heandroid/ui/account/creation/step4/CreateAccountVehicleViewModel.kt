package com.heandroid.ui.account.creation.step4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.account.ValidVehicleCheckRequest
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.repository.auth.CreateAccountRespository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountVehicleViewModel @Inject constructor(private val repo: CreateAccountRespository) : BaseViewModel() {

    private val findVehicleMutData = MutableLiveData<Resource<VehicleInfoDetails?>?>()
    val findVehicleLiveData: LiveData<Resource<VehicleInfoDetails?>?> get() = findVehicleMutData

    private val validVehicleMutData = MutableLiveData<Resource<String?>?>()
    val validVehicleLiveData: LiveData<Resource<String?>?> get() = validVehicleMutData

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

    fun validVehicleCheck(vehicleValidReqModel: ValidVehicleCheckRequest?, agencyId: Int?) {

        viewModelScope.launch {
            try{
                validVehicleMutData.setValue(
                    ResponseHandler.success(
                        repo.validVehicleCheck(
                            vehicleValidReqModel, agencyId)
                    , errorManager
                ))

            }catch (e: Exception) {
                validVehicleMutData.setValue(ResponseHandler.failure(e))
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