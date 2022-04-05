package com.heandroid.ui.bottomnav.account.payments.accountpaymenthistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryRequest
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryResponse
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AccountPaymentHistoryViewModel @Inject constructor(private val repo: AccountPaymentHistoryRepo) : BaseViewModel() {

    private val accountPaymentMutLiveData = MutableLiveData<Resource<AccountPaymentHistoryResponse?>?>()
    val paymentHistoryLiveData: LiveData<Resource<AccountPaymentHistoryResponse?>?> get() = accountPaymentMutLiveData

    fun paymentHistoryDetails(request: AccountPaymentHistoryRequest) {
        viewModelScope.launch {
            try {
                accountPaymentMutLiveData.postValue(ResponseHandler.success(repo.getAccountPayment(request), errorManager))
            } catch (e: Exception) {
                accountPaymentMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }
}