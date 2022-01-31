package com.heandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.model.AccountResponse
import com.heandroid.model.AlertMessageApiResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class VehicleMgmtViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    val addVehicleApiVal = MutableLiveData<Resource<Response<AlertMessageApiResponse>>>()

    fun getAccountOverViewApi(
        authToken: String
    ) {

        viewModelScope.launch {
            addVehicleApiVal.postValue(Resource.loading(null))
            try {
                val respFromApi = apiHelper.getAccountOverviewApiCall(authToken)
                //loginUserVal.postValue(Resource.success(usersFromApi))
                addVehicleApiVal.postValue(setAccountOverviewApiResponse(usersFromApi))
            } catch (e: Exception) {
                addVehicleApiVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun setAccountOverviewApiResponse(usersFromApi: Response<AccountResponse>): Resource<Response<AccountResponse>>? {
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