package com.heandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.model.EmptyApiResponse
import com.heandroid.model.VehicleResponse
import com.heandroid.model.crossingHistory.request.CrossingHistoryDownloadRequest
import com.heandroid.model.crossingHistory.request.CrossingHistoryRequest
import com.heandroid.model.crossingHistory.response.CrossingHistoryApiResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class VehicleMgmtViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    val addVehicleApiVal = MutableLiveData<Resource<Response<EmptyApiResponse>>>()
    val updateVehicleApiVal = MutableLiveData<Resource<Response<EmptyApiResponse>>>()
    val crossingHistoryVal = MutableLiveData<Resource<Response<CrossingHistoryApiResponse>>>()
    val crossingHistoryDownloadVal = MutableLiveData<Resource<Response<ResponseBody>>>()

    fun addVehicleApi(request: VehicleResponse
    ) {

        viewModelScope.launch {
            addVehicleApiVal.postValue(Resource.loading(null))
            try {
                val respFromApi = apiHelper.addVehicleApiCall( request)
                addVehicleApiVal.postValue(setAddUpdateVehicleApiResponse(respFromApi))
            } catch (e: Exception) {
                addVehicleApiVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun setAddUpdateVehicleApiResponse(usersFromApi: Response<EmptyApiResponse>): Resource<Response<EmptyApiResponse>>? {
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

    fun updateVehicleApi(request: VehicleResponse
    ) {

        viewModelScope.launch {
            updateVehicleApiVal.postValue(Resource.loading(null))
            try {
                val respFromApi = apiHelper.updateVehicleApiCall( request)
                updateVehicleApiVal.postValue(setAddUpdateVehicleApiResponse(respFromApi))
            } catch (e: Exception) {
                updateVehicleApiVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

//    fun getListData(body: CrossingHistoryRequest?) : Flow<PagingData<CrossingHistoryItem>> {
//        return Pager(config = PagingConfig(pageSize = 1, maxSize = 5),
//                     pagingSourceFactory = {CrossingPaging(apiHelper,body)}).flow.cachedIn(viewModelScope)
//    }

    fun crossingHistoryApiCall(request: CrossingHistoryRequest
    ) {

        viewModelScope.launch {
            crossingHistoryVal.postValue(Resource.loading(null))
            try {
                val respFromApi = apiHelper.crossingHistoryApiCall(request)
                crossingHistoryVal.postValue(setCrossingHistoryApiResponse(respFromApi))
            } catch (e: Exception) {
                crossingHistoryVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun setCrossingHistoryApiResponse(respFromApi: Response<CrossingHistoryApiResponse>): Resource<Response<CrossingHistoryApiResponse>>? {
        return if(respFromApi.isSuccessful) {
            Resource.success(respFromApi)
        } else {
            var errorCode = respFromApi.code()
            if(errorCode==401) {
                Resource.error(null, "Invalid token")
            } else {
                Resource.error(null, "Unknown error")
            }

        }
    }


    fun downloadCrossingHistoryApiCall(request: CrossingHistoryDownloadRequest
    ) {

        viewModelScope.launch {
            crossingHistoryDownloadVal.postValue(Resource.loading(null))
            try {
                val respFromApi = apiHelper.downloadCrossingHistoryAPiCall(request)
                crossingHistoryDownloadVal.postValue(setDownloadCrossingHistoryApiResponse(respFromApi))
            } catch (e: Exception) {
                crossingHistoryDownloadVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun setDownloadCrossingHistoryApiResponse(respFromApi: Response<ResponseBody>): Resource<Response<ResponseBody>>? {
        return if(respFromApi.isSuccessful) {
            Resource.success(respFromApi)
        } else {
            var errorCode = respFromApi.code()
            if(errorCode==401) {
                Resource.error(null, "Invalid token")
            } else {
                Resource.error(null, "Unknown error")
            }

        }
    }

}