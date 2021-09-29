package com.heandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.heandroid.model.AccountResponse
import com.heandroid.model.LoginResponse
import com.heandroid.model.RetrievePaymentListRequest
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class DashboardViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    val accountOverviewVal = MutableLiveData<Resource<Response<AccountResponse>>>()
    val monthlyUsageVal = MutableLiveData<Resource<Response<RetrievePaymentListRequest>>>()
    val paymentListVal = MutableLiveData<Resource<Response<RetrievePaymentListRequest>>>()

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
        if(usersFromApi.isSuccessful)
        {
            return Resource.success(usersFromApi)
        }
        else
        {
            var errorCode = usersFromApi.code()
            return if(errorCode==401) {
                Resource.error(null, "Invalid token")
            } else {
                Resource.error(null, "Unknown error ")
            }

        }
    }


    fun getVehicleInformationApi(authToken: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiHelper.getVehicleListApiCall(authToken)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun retrievePaymentListApi(header: String, requestParam: RetrievePaymentListRequest) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiHelper.retrievePaymentList(
                            header,
                            requestParam
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getMonthlyUsage(header: String, requestParam: RetrievePaymentListRequest) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiHelper.getMonthlyUsageApiCall(
                            header,
                            requestParam
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

}