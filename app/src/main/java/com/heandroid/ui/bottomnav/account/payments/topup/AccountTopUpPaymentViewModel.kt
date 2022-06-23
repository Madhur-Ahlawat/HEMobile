package com.heandroid.ui.bottomnav.account.payments.topup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.accountpayment.AccountGetThresholdResponse
import com.heandroid.data.model.accountpayment.AccountTopUpUpdateThresholdRequest
import com.heandroid.data.model.accountpayment.AccountTopUpUpdateThresholdResponse
import com.heandroid.ui.bottomnav.account.payments.history.AccountPaymentHistoryRepo
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
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

    private val updateAmountMutLiveData =
        MutableLiveData<Resource<AccountTopUpUpdateThresholdResponse?>?>()
    val updateAmountLiveData: LiveData<Resource<AccountTopUpUpdateThresholdResponse?>?> get() = updateAmountMutLiveData

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
                updateAmountMutLiveData.postValue(
                    ResponseHandler.success(
                        repo.updateThresholdAmount(
                            request
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                updateAmountMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }
}