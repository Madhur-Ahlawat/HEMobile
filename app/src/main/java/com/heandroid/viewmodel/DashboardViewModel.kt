package com.heandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.heandroid.model.*
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class DashboardViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    val accountOverviewVal = MutableLiveData<Resource<Response<AccountResponse>>>()
    val monthlyUsageVal = MutableLiveData<Resource<Response<RetrievePaymentListApiResponse>>>()
    val paymentListVal = MutableLiveData<Resource<Response<RetrievePaymentListApiResponse>>>()
    val vehicleListVal = MutableLiveData<Resource<Response<List<VehicleResponse>>>>()
    val forgotUsernameVal = MutableLiveData<Resource<Response<ForgotUsernameApiResponse>>>()

    fun getAccountOverViewApi(
      authToken: String
    ) {

        viewModelScope.launch {
            accountOverviewVal.postValue(Resource.loading(null))
            try {
                val usersFromApi = apiHelper.getAccountOverviewApiCall(authToken)
                //loginUserVal.postValue(Resource.success(usersFromApi))
                accountOverviewVal.postValue(setAccountOverviewApiResponse(usersFromApi))
            } catch (e: Exception) {
                accountOverviewVal.postValue(Resource.error(null , e.toString()))
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


    fun getVehicleInformationApi(authToken: String)
    {
        viewModelScope.launch {
        vehicleListVal.postValue(Resource.loading(null))
        try {
            val respFromApi = apiHelper.getVehicleListApiCall(authToken)
            //loginUserVal.postValue(Resource.success(usersFromApi))
            vehicleListVal.postValue(setVehicleListApiResponse(respFromApi))
        } catch (e: Exception) {
            vehicleListVal.postValue(Resource.error(null , e.toString()))
        }
    }

    }

    private fun setVehicleListApiResponse(respFromApi: Response<List<VehicleResponse>>): Resource<Response<List<VehicleResponse>>>? {
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

    fun retrievePaymentListApi(authToken: String, requestParam: RetrievePaymentListRequest)
    {
        viewModelScope.launch {
            paymentListVal.postValue(Resource.loading(null))
            try {
                val respFromApi = apiHelper.retrievePaymentList(authToken , requestParam)
                //loginUserVal.postValue(Resource.success(usersFromApi))
                paymentListVal.postValue(setPaymentListApiResponse(respFromApi))
            } catch (e: Exception) {
                paymentListVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun setPaymentListApiResponse(respFromApi: Response<RetrievePaymentListApiResponse>): Resource<Response<RetrievePaymentListApiResponse>>? {

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

    fun getMonthlyUsage(authToken: String, requestParam: RetrievePaymentListRequest)
    {
        viewModelScope.launch {
            monthlyUsageVal.postValue(Resource.loading(null))
            try {
                val respFromApi = apiHelper.getMonthlyUsageApiCall(authToken , requestParam)
                monthlyUsageVal.postValue(setMonthlyUsageApiResponse(respFromApi))
            } catch (e: Exception) {
                monthlyUsageVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }
    private fun setMonthlyUsageApiResponse(respFromApi: Response<RetrievePaymentListApiResponse>): Resource<Response<RetrievePaymentListApiResponse>>? {

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


    fun recoverUsernameApi(
        requestParam: ForgotUsernameRequest
    ) {

        viewModelScope.launch {
            forgotUsernameVal.postValue(Resource.loading(null))
            try {
                val respFromApi = apiHelper.getForgotUserNameApiCall(requestParam)
                //loginUserVal.postValue(Resource.success(usersFromApi))
                forgotUsernameVal.postValue(recoverUsernameApiResponse(respFromApi))
            } catch (e: Exception) {
                forgotUsernameVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun recoverUsernameApiResponse(respFromApi: Response<ForgotUsernameApiResponse>): Resource<Response<ForgotUsernameApiResponse>>? {
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