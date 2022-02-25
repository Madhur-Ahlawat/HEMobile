package com.heandroid.ui.bottomnav.vehicle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.repository.dashboard.DashBoardRepo
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class VehicleViewModel(private val repo: DashBoardRepo) : BaseViewModel() {

    val vehicleListVal = MutableLiveData<Resource<List<VehicleResponse>?>>()

    fun getVehicleInformationApi() {
        viewModelScope.launch {
            try {

                vehicleListVal.postValue(ResponseHandler.success(repo.getVehicleData(),errorManager))

            } catch (e: Exception) {
                vehicleListVal.postValue(ResponseHandler.failure(e))
            }
        }

    }



   /*  val addVehicleApiVal = MutableLiveData<Resource<Response<EmptyApiResponse>>>()
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
     }*/
}