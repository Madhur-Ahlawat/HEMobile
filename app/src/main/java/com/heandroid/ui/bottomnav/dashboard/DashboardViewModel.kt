package com.heandroid.ui.bottomnav.dashboard

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.account.AccountResponse
import com.heandroid.data.model.auth.forgot.email.ForgotUsernameApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.notification.AlertMessageApiResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.repository.dashboard.DashBoardRepo
import com.heandroid.model.RetrievePaymentListApiResponse
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val repo: DashBoardRepo) : BaseViewModel() {




    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _crossingHistoryVal = MutableLiveData<Resource<CrossingHistoryApiResponse?>?>()
    val crossingHistoryVal: LiveData<Resource<CrossingHistoryApiResponse?>?> get() = _crossingHistoryVal


    val accountOverviewVal = MutableLiveData<Resource<AccountResponse>>()
    val monthlyUsageVal = MutableLiveData<Resource<RetrievePaymentListApiResponse>>()
    val paymentListVal = MutableLiveData<Resource<RetrievePaymentListApiResponse>>()
    val vehicleListVal = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    val forgotUsernameVal = MutableLiveData<Response<ForgotUsernameApiResponse>>()
    val getAlertsVal = MutableLiveData<Resource<AlertMessageApiResponse?>?>()

    fun getVehicleInformationApi() {
        viewModelScope.launch {
            try {

                vehicleListVal.postValue(ResponseHandler.success(repo.getVehicleData(),errorManager))

            } catch (e: Exception) {
                vehicleListVal.postValue(ResponseHandler.failure(e))
            }
        }

    }


    fun getAlertsApi(
        lng: String
    ) {

        viewModelScope.launch {
            try {
                getAlertsVal.postValue(ResponseHandler.success(repo.getAlertMessages(),errorManager))
            } catch (e: Exception) {
                getAlertsVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun crossingHistoryApiCall(request: CrossingHistoryRequest) {
        viewModelScope.launch {
            try {
                _crossingHistoryVal.postValue(
                    ResponseHandler.success(
                        repo.crossingHistoryApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _crossingHistoryVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

}