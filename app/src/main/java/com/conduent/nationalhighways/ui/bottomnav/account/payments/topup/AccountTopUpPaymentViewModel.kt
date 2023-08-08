package com.conduent.nationalhighways.ui.bottomnav.account.payments.topup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.accountpayment.AccountGetThresholdResponse
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdResponse
import com.conduent.nationalhighways.data.repository.paymenthistory.AccountPaymentHistoryRepo
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountTopUpPaymentViewModel @Inject constructor(
    private val repo: AccountPaymentHistoryRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    private val thresholdMutLiveData = MutableLiveData<Resource<AccountGetThresholdResponse?>?>()
    val thresholdLiveData: LiveData<Resource<AccountGetThresholdResponse?>?> get() = thresholdMutLiveData

    private val _updateAmountMutLiveData =
        MutableLiveData<Resource<AccountTopUpUpdateThresholdResponse?>?>()
    val updateAmountLiveData: LiveData<Resource<AccountTopUpUpdateThresholdResponse?>?> get() = _updateAmountMutLiveData

    fun getThresholdAmount() {
        viewModelScope.launch {
            try {
                thresholdMutLiveData.postValue(
                    ResponseHandler.success(
                        repo.getThresholdAmount(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                thresholdMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun updateThresholdAmount(request: AccountTopUpUpdateThresholdRequest?) {
        viewModelScope.launch {
            try {
                _updateAmountMutLiveData.postValue(
                    ResponseHandler.success(
                        repo.updateThresholdAmount(
                            request
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _updateAmountMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }
}