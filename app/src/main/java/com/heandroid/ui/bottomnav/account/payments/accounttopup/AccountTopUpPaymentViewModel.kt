package com.heandroid.ui.bottomnav.account.payments.accounttopup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.account.ThresholdAmountApiResponse
import com.heandroid.data.model.accountpayment.AccountTopUpUpdateThresholdRequest
import com.heandroid.data.model.accountpayment.AccountTopUpUpdateThresholdResponse
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.ui.bottomnav.account.payments.history.AccountPaymentHistoryRepo
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AccountTopUpPaymentViewModel @Inject constructor(private val repo: AccountPaymentHistoryRepo) : BaseViewModel(){

    private val thresholdMutLiveData = MutableLiveData<Resource<ThresholdAmountApiResponse?>?>()
    val thresholdLiveData: LiveData<Resource<ThresholdAmountApiResponse?>?> get() = thresholdMutLiveData

    private val updateAmountMutLiveData = MutableLiveData<Resource<AccountTopUpUpdateThresholdResponse?>?>()
    val updateAmountLiveData: LiveData<Resource<AccountTopUpUpdateThresholdResponse?>?> get() = updateAmountMutLiveData

    fun getThresholdAmount(){
        viewModelScope.launch {
            try{
                thresholdMutLiveData.postValue(ResponseHandler.success(repo.getThresholdAmount(), errorManager))
            }catch (e:Exception){
                thresholdMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun updateThresholdAmount(request: AccountTopUpUpdateThresholdRequest?){
        viewModelScope.launch {
            try{
                updateAmountMutLiveData.postValue(ResponseHandler.success(repo.updateThresholdAmount(request), errorManager))
            }catch (e:Exception){
                updateAmountMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }
}