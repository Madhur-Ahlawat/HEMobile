package com.heandroid.ui.bottomnav.account.payments.history

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryRequest
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryResponse
import com.heandroid.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AccountPaymentHistoryViewModel @Inject constructor(
    private val repo: AccountPaymentHistoryRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val accountPaymentMutLiveData =
        MutableLiveData<Resource<AccountPaymentHistoryResponse?>?>()
    val paymentHistoryLiveData: LiveData<Resource<AccountPaymentHistoryResponse?>?> get() = accountPaymentMutLiveData

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _paymentHistoryDownloadVal = MutableLiveData<Resource<ResponseBody?>?>()
    val paymentHistoryDownloadVal: LiveData<Resource<ResponseBody?>?> get() = _paymentHistoryDownloadVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _vehicleListVal = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    val vehicleListVal: LiveData<Resource<List<VehicleResponse?>?>?> get() = _vehicleListVal

    fun paymentHistoryDetails(request: AccountPaymentHistoryRequest) {
        viewModelScope.launch {
            try {
                accountPaymentMutLiveData.postValue(
                    ResponseHandler.success(
                        repo.getAccountPayment(
                            request
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                accountPaymentMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun downloadPaymentHistoryApiCall(request: TransactionHistoryDownloadRequest) {
        viewModelScope.launch {
            try {
                _paymentHistoryDownloadVal.postValue(
                    ResponseHandler.success(
                        repo.downloadPaymentHistoryAPiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _paymentHistoryDownloadVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getVehicleInformationApi() {
        viewModelScope.launch {
            try {
                _vehicleListVal.postValue(
                    ResponseHandler.success(
                        repo.getVehicleListApiCall(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _vehicleListVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

}