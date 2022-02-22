package com.heandroid.ui.vehicle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.response.EmptyApiResponse
import com.heandroid.data.model.response.vehicle.CrossingHistoryApiResponse
import com.heandroid.data.model.response.vehicle.VehicleResponse
import com.heandroid.data.model.request.vehicle.CrossingHistoryDownloadRequest
import com.heandroid.data.model.request.vehicle.CrossingHistoryRequest
import com.heandroid.data.remote.ApiHelper
import com.heandroid.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class VehicleMgmtViewModel @Inject constructor(private val apiHelper: ApiHelper) : ViewModel() {

    private val addVehicleApiVal = MutableLiveData<Resource<Response<EmptyApiResponse>>>()
    val updateVehicleApiVal = MutableLiveData<Resource<Response<EmptyApiResponse>>>()
    val crossingHistoryVal = MutableLiveData<Resource<Response<CrossingHistoryApiResponse>>>()
    private val crossingHistoryDownloadVal = MutableLiveData<Resource<Response<ResponseBody>>>()
    val vehicleListVal = MutableLiveData<Resource<Response<List<VehicleResponse>>>>()


    fun addVehicleApi(
        request: VehicleResponse
    ) {

        viewModelScope.launch {
            addVehicleApiVal.postValue(Resource.Loading(null))
            try {
                val respFromApi = apiHelper.addVehicleApiCall(request)
                addVehicleApiVal.postValue(setAddUpdateVehicleApiResponse(respFromApi))
            } catch (e: Exception) {
                addVehicleApiVal.postValue(Resource.DataError(e.toString()))
            }
        }
    }

    private fun setAddUpdateVehicleApiResponse(usersFromApi: Response<EmptyApiResponse>): Resource<Response<EmptyApiResponse>>? {
        return if (usersFromApi.isSuccessful) {
            Resource.Success(usersFromApi)
        } else {
            var errorCode = usersFromApi.code()
            if (errorCode == 401) {
                Resource.DataError("Invalid token")
            } else {
                Resource.DataError("Unknown error")
            }

        }
    }

    fun updateVehicleApi(
        request: VehicleResponse
    ) {
        viewModelScope.launch {
            updateVehicleApiVal.postValue(Resource.Loading(null))
            try {
                val respFromApi = apiHelper.updateVehicleApiCall(request)
                updateVehicleApiVal.postValue(setAddUpdateVehicleApiResponse(respFromApi))
            } catch (e: Exception) {
                updateVehicleApiVal.postValue(Resource.DataError(e.toString()))
            }
        }
    }

//    fun getListData(body: CrossingHistoryRequest?) : Flow<PagingData<CrossingHistoryItem>> {
//        return Pager(config = PagingConfig(pageSize = 1, maxSize = 5),
//                     pagingSourceFactory = {CrossingPaging(apiHelper,body)}).flow.cachedIn(viewModelScope)
//    }

    fun crossingHistoryApiCall(
        request: CrossingHistoryRequest
    ) {

        viewModelScope.launch {
            crossingHistoryVal.postValue(Resource.Loading(null))
            try {
                val respFromApi = apiHelper.crossingHistoryApiCall(request)
                crossingHistoryVal.postValue(setCrossingHistoryApiResponse(respFromApi))
            } catch (e: Exception) {
                crossingHistoryVal.postValue(Resource.DataError(e.toString()))
            }
        }
    }

    private fun setCrossingHistoryApiResponse(respFromApi: Response<CrossingHistoryApiResponse>): Resource<Response<CrossingHistoryApiResponse>>? {
        return if (respFromApi.isSuccessful) {
            Resource.Success(respFromApi)
        } else {
            var errorCode = respFromApi.code()
            if (errorCode == 401) {
                Resource.DataError("Invalid token")
            } else {
                Resource.DataError("Unknown error")
            }

        }
    }


    fun downloadCrossingHistoryApiCall(
        request: CrossingHistoryDownloadRequest
    ) {

        viewModelScope.launch {
            crossingHistoryDownloadVal.postValue(Resource.Loading(null))
            try {
                val respFromApi = apiHelper.downloadCrossingHistoryAPiCall(request)
                crossingHistoryDownloadVal.postValue(
                    setDownloadCrossingHistoryApiResponse(
                        respFromApi
                    )
                )
            } catch (e: Exception) {
                crossingHistoryDownloadVal.postValue(Resource.DataError(e.toString()))
            }
        }
    }

    private fun setDownloadCrossingHistoryApiResponse(respFromApi: Response<ResponseBody>): Resource<Response<ResponseBody>>? {
        return if (respFromApi.isSuccessful) {
            Resource.Success(respFromApi)
        } else {
            var errorCode = respFromApi.code()
            if (errorCode == 401) {
                Resource.DataError("Invalid token")
            } else {
                Resource.DataError("Unknown error")
            }

        }
    }

    fun getVehicleInformationApi() {
        viewModelScope.launch {
            vehicleListVal.postValue(Resource.Loading(null))
            try {
                val respFromApi = apiHelper.getVehicleListApiCall()
                vehicleListVal.postValue(setVehicleListApiResponse(respFromApi))
            } catch (e: Exception) {
                vehicleListVal.postValue(Resource.DataError(e.toString()))
            }
        }
    }

    private fun setVehicleListApiResponse(respFromApi: Response<List<VehicleResponse>>): Resource<Response<List<VehicleResponse>>>? {
        return if (respFromApi.isSuccessful) {
            Resource.Success(respFromApi)
        } else {
            var errorCode = respFromApi.code()
            if (errorCode == 401) {
                Resource.DataError( "Invalid token")
            } else {
                Resource.DataError( "Unknown error")
            }

        }
    }

}