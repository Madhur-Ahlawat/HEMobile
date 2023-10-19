package com.conduent.nationalhighways.ui.bottomnav.dashboard

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.account.AccountInformation
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.ThresholdAmountApiResponse
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.CheckedCrossingRecentTransactionsResponseModel
import com.conduent.nationalhighways.data.model.accountpayment.CheckedCrossingTransactionsRequestModel
import com.conduent.nationalhighways.data.model.auth.login.AuthResponseModel
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.payment.PaymentReceiptDeliveryTypeSelectionRequest
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.data.remote.NoConnectivityException
import com.conduent.nationalhighways.data.repository.dashboard.DashBoardRepo
import com.conduent.nationalhighways.ui.transactions.paging.TransactionsPagingSource
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashBoardRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    val transactionsList = Pager(PagingConfig(10)) {
        TransactionsPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
    private val alertMutData = MutableLiveData<Resource<AlertMessageApiResponse?>?>()
    val accountType: LiveData<AccountResponse> get() = _accountType
    private val _accountType = MutableLiveData<AccountResponse>()
    fun setAccountType(accountResponse: AccountResponse) {
        _accountType.postValue(accountResponse)
    }

    val alertLivData: LiveData<Resource<AlertMessageApiResponse?>?> get() = alertMutData

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
    val paymentHistoryLiveData: LiveData<Resource<AccountPaymentHistoryResponse?>?> get() = accountPaymentMutLiveData


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val accountPaymentMutLiveData =
        MutableLiveData<Resource<AccountPaymentHistoryResponse?>?>()
    val paymentHistoryLiveDataCheckedCrossing: LiveData<Resource<CheckedCrossingRecentTransactionsResponseModel?>?> get() = accountPaymentMutLiveDataCheckedCrossing

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val accountPaymentMutLiveDataCheckedCrossing =
        MutableLiveData<Resource<CheckedCrossingRecentTransactionsResponseModel?>?>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _alertsVal = MutableLiveData<Resource<AlertMessageApiResponse?>?>()
    val getAlertsVal: LiveData<Resource<AlertMessageApiResponse?>?> get() = _alertsVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _whereToReceivePaymentReceipt = MutableLiveData<Resource<ResponseBody?>?>()
    val whereToReceivePaymentReceipt: LiveData<Resource<ResponseBody?>?> get() = _whereToReceivePaymentReceipt


    var personalInformationData: MutableLiveData<PersonalInformation> = MutableLiveData()
    var accountInformationData: MutableLiveData<AccountInformation> = MutableLiveData()


    init {
        personalInformationData.value = PersonalInformation(
            "", "", "", "", "",
            "", "", "", "", "", "", "",
            "", "", "", "", "", "",
            "", "", false, false, false,
            false, "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", "", "", ""
        )

    }

    fun paymentHistoryDetails(request: AccountPaymentHistoryRequest) {
        viewModelScope.launch {
            try {
                accountPaymentMutLiveData.postValue(
                    ResponseHandler.success(
                        repository.getAccountPayment(
                            request
                        ), errorManager
                    )
                )
            } catch (e: java.lang.Exception) {
                accountPaymentMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun paymentHistoryDetailsCheckCrossings(request: CheckedCrossingTransactionsRequestModel) {
        viewModelScope.launch {
            try {
                accountPaymentMutLiveDataCheckedCrossing.postValue(
                    ResponseHandler.success(
                        repository.getAccountPaymentCheckCrossings(
                            request
                        ), errorManager
                    )
                )
            } catch (e: java.lang.Exception) {
                accountPaymentMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }

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
                    val callOverview = async {
                        repository.getAccountDetailsApiCall()
                    }
                    delay(100)
                    val callAlerts = async { repository.getAlertMessages() }

                    vehicleCountResponse = callVehicleCount.await()
                    crossingCountResponse = callCrossingCount.await()
//                    thresholdAmountResponse = callThreshold.await()
                    overviewResponse = callOverview.await()
                    alertsResponse = callAlerts.await()

                    if (alertsResponse?.isSuccessful == true) {
                        _alertsVal.postValue(
                            ResponseHandler.success(
                                alertsResponse,
                                errorManager
                            )
                        )
                    } else {
                        _alertsVal.value =
                            Resource.DataError("Something went wrong. Try again later")
                    }

                    if (vehicleCountResponse?.isSuccessful == true) {
                        _vehicleListVal.postValue(
                            ResponseHandler.success(
                                vehicleCountResponse,
                                errorManager
                            )
                        )
                    } else {
                        _vehicleListVal.value =
                            Resource.DataError("Something went wrong. Try again later")
                    }

                    if (crossingCountResponse?.isSuccessful == true) {
                        _crossingHistoryVal.postValue(
                            ResponseHandler.success(
                                crossingCountResponse,
                                errorManager
                            )
                        )
                    } else {
                        _crossingHistoryVal.value =
                            Resource.DataError("Something went wrong. Try again later")
                    }

                    if (overviewResponse?.isSuccessful == true) {
                        _accountDetailsVal.postValue(
                            ResponseHandler.success(
                                overviewResponse,
                                errorManager
                            )
                        )
                    } else {
                        _accountDetailsVal.value =
                            Resource.DataError("Something went wrong. Try again later")
                    }
                }
            } catch (e: Exception) {
                if (e is NoConnectivityException) {
                    _accountDetailsVal.value = Resource.DataError(e.message)
                } else if (e is SocketTimeoutException || e is InterruptedIOException) {
                    _accountDetailsVal.value = Resource.DataError(Constants.VPN_ERROR)
                } else {
                    _accountDetailsVal.value = Resource.DataError(e.message)
                }
            }
        }

    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _logout = MutableLiveData<Resource<AuthResponseModel?>?>()
    val logout: LiveData<Resource<AuthResponseModel?>?> get() = _logout
    fun logout() {
        viewModelScope.launch {
            try {
                _logout.postValue(ResponseHandler.success(repository.logout(), errorManager))
            } catch (e: java.lang.Exception) {
                _logout.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun whereToReceivePaymentReceipt(request: PaymentReceiptDeliveryTypeSelectionRequest) {
        viewModelScope.launch {
            try {
                _whereToReceivePaymentReceipt.postValue(
                    ResponseHandler.success(
                        repository.whereToReceivePaymentReceipt(
                            request
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _whereToReceivePaymentReceipt.postValue(ResponseHandler.failure(e))
            }
        }
    }
}
