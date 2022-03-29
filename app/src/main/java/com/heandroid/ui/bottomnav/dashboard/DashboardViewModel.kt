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


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _vehicleListVal = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    val vehicleListVal : LiveData<Resource<List<VehicleResponse?>?>?> get() = _vehicleListVal

    val accountOverviewVal = MutableLiveData<Resource<AccountResponse>>()
    val monthlyUsageVal = MutableLiveData<Resource<RetrievePaymentListApiResponse>>()
    val paymentListVal = MutableLiveData<Resource<RetrievePaymentListApiResponse>>()

    val forgotUsernameVal = MutableLiveData<Response<ForgotUsernameApiResponse>>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _alertsVal = MutableLiveData<Resource<AlertMessageApiResponse?>?>()
    val getAlertsVal : LiveData<Resource<AlertMessageApiResponse?>?> get() = _alertsVal

    fun getVehicleInformationApi() {
        viewModelScope.launch {
            try {

                _vehicleListVal.postValue(ResponseHandler.success(repo.getVehicleData(),errorManager))

            } catch (e: Exception) {
                _vehicleListVal.postValue(ResponseHandler.failure(e))
            }
        }

    }


    fun getAlertsApi(
    ) {

        viewModelScope.launch {
            try {
                _alertsVal.postValue(ResponseHandler.success(repo.getAlertMessages(),errorManager))
            } catch (e: Exception) {
                _alertsVal.postValue(ResponseHandler.failure(e))
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