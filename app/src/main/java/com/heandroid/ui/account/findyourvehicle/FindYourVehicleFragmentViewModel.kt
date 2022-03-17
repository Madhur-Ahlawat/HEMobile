package com.heandroid.ui.account.creation.findyourvehicle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindYourVehicleFragmentViewModel @Inject constructor(private val repo: FindYourVehicleRepo) :
    BaseViewModel() {

    private val findVehicleMutData = MutableLiveData<Resource<VehicleInfoDetails?>?>()

    val findVehicleLiveData: LiveData<Resource<VehicleInfoDetails?>?> get() = findVehicleMutData

    fun getVehicleData(vehicleNumber: String?, agencyId: Int?) {
        viewModelScope.launch {
            try {
                findVehicleMutData.postValue(ResponseHandler.success(repo.getVehicleDetail(vehicleNumber, agencyId), errorManager)
                )
            } catch (e: Exception) {
                findVehicleMutData.postValue(ResponseHandler.failure(e))
            }
        }
    }
}