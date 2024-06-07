package com.conduent.nationalhighways.ui.bottomnav.account.payments.history

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.data.repository.paymenthistory.AccountPaymentHistoryRepo
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
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