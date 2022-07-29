package com.heandroid.ui.bottomnav.dashboard

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.account.AccountResponse
import com.heandroid.data.model.account.ThresholdAmountApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.notification.AlertMessageApiResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.remote.NoConnectivityException
import com.heandroid.data.repository.dashboard.DashBoardRepo
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashBoardRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _crossingHistoryVal = MutableLiveData<Resource<CrossingHistoryApiResponse?>?>()
    val crossingHistoryVal: LiveData<Resource<CrossingHistoryApiResponse?>?> get() = _crossingHistoryVal


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _vehicleListVal = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    val vehicleListVal: LiveData<Resource<List<VehicleResponse?>?>?> get() = _vehicleListVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _accountDetailsVal = MutableLiveData<Resource<AccountResponse?>?>()
    val accountOverviewVal: MutableLiveData<Resource<AccountResponse?>?> get() = _accountDetailsVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _thresholdAmountVal = MutableLiveData<Resource<ThresholdAmountApiResponse?>?>()
    val thresholdAmountVal: MutableLiveData<Resource<ThresholdAmountApiResponse?>?> get() = _thresholdAmountVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _alertsVal = MutableLiveData<Resource<AlertMessageApiResponse?>?>()
    val getAlertsVal: LiveData<Resource<AlertMessageApiResponse?>?> get() = _alertsVal


    fun getVehicleInformationApi() {
        viewModelScope.launch {
            try {
                _vehicleListVal.postValue(
                    ResponseHandler.success(
                        repository.getVehicleData(),
                        errorManager
                    )
                )

            } catch (e: Exception) {
                _vehicleListVal.postValue(ResponseHandler.failure(e))
            }
        }


    }


    fun getAlertsApi() {
        viewModelScope.launch {
            try {
                _alertsVal.postValue(
                    ResponseHandler.success(
                        repository.getAlertMessages(),
                        errorManager
                    )
                )
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
                        repository.crossingHistoryApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _crossingHistoryVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getAccountDetailsData() {
        viewModelScope.launch {
            try {
                _accountDetailsVal.postValue(
                    ResponseHandler.success(repository.getAccountDetailsApiCall(), errorManager)
                )
            } catch (e: Exception) {
                _accountDetailsVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getThresholdAmountData() {
        viewModelScope.launch {
            try {
                _thresholdAmountVal.postValue(
                    ResponseHandler.success(repository.getThresholdAmountApiCAll(), errorManager)
                )
            } catch (e: Exception) {
                _thresholdAmountVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getDashboardAllData(request: CrossingHistoryRequest) {
        viewModelScope.launch {
            try {
                coroutineScope {
                    val vehicleCountResponse: Response<List<VehicleResponse?>?>?
                    val crossingCountResponse: Response<CrossingHistoryApiResponse?>?
//                    val thresholdAmountResponse: Response<ThresholdAmountApiResponse?>?
                    val overviewResponse: Response<AccountResponse?>?
                    val alertsResponse: Response<AlertMessageApiResponse?>?

                    val callVehicleCount = async { repository.getVehicleData() }
                    delay(100)
                    val callCrossingCount = async { repository.crossingHistoryApiCall(request) }
                    delay(100)
//                    val callThreshold = async { repository.getThresholdAmountApiCAll() }
//                    delay(100)
                    val callOverview = async { repository.getAccountDetailsApiCall() }
                    delay(100)
                    val callAlerts = async { repository.getAlertMessages() }

                    vehicleCountResponse = callVehicleCount.await()
                    crossingCountResponse = callCrossingCount.await()
//                    thresholdAmountResponse = callThreshold.await()
                    overviewResponse = callOverview.await()
                    alertsResponse = callAlerts.await()

                    if (vehicleCountResponse?.isSuccessful == true &&
                        crossingCountResponse?.isSuccessful == true &&
//                        thresholdAmountResponse?.isSuccessful == true &&
                        overviewResponse?.isSuccessful == true &&
                        alertsResponse?.isSuccessful == true
                    ) {
                        _vehicleListVal.value = Resource.Success(vehicleCountResponse.body())
                        _crossingHistoryVal.value = Resource.Success(crossingCountResponse.body())
//                        _thresholdAmountVal.value = Resource.Success(thresholdAmountResponse.body())
                        _accountDetailsVal.value = Resource.Success(overviewResponse.body())
                        _alertsVal.value = Resource.Success(alertsResponse.body())

                    } else {
                        _vehicleListVal.value =
                            Resource.DataError("Something went wrong. Try again later")
                    }
                }
            } catch (e: Exception) {
                if (e is NoConnectivityException) {
                    _vehicleListVal.value = Resource.DataError(e.message)
                } else if (e is SocketTimeoutException || e is InterruptedIOException) {
                    _vehicleListVal.value = Resource.DataError(Constants.VPN_ERROR)
                }
                _vehicleListVal.value = Resource.DataError(e.message)
            }
        }

    }

}
