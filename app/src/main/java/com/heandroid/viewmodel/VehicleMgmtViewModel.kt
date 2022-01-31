package com.heandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.model.*
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class VehicleMgmtViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    val addVehicleApiVal = MutableLiveData<Resource<Response<AddVehicleApiResponse>>>()

    fun addVehicleApi(
        authToken: String , request: VehicleResponse
    ) {

        viewModelScope.launch {
            addVehicleApiVal.postValue(Resource.loading(null))
            try {
                val respFromApi = apiHelper.addVehicleApiCall(authToken , request)
                addVehicleApiVal.postValue(setAddVehicleApiResponse(respFromApi))
            } catch (e: Exception) {
                addVehicleApiVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun setAddVehicleApiResponse(usersFromApi: Response<AddVehicleApiResponse>): Resource<Response<AddVehicleApiResponse>>? {
        return if(usersFromApi.isSuccessful) {
            Resource.success(usersFromApi)
        } else {
            var errorCode = usersFromApi.code()
            if(errorCode==401) {
                Resource.error(null, "Invalid token")
            } else {
                Resource.error(null, "Unknown error")
            }

        }
    }

}