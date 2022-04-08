package com.heandroid.ui.bottomnav.account.payments.method

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.payment.*
import com.heandroid.data.repository.payment.PaymentMethodRepository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.ui.bottomnav.notification.NotificationViewAllRepo
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import com.heandroid.utils.common.ResponseHandler.failure
import com.heandroid.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(val repository: PaymentMethodRepository) : BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _savedCardList = MutableLiveData<Resource<PaymentMethodResponseModel?>?>()
    val savedCardList : LiveData<Resource<PaymentMethodResponseModel?>?> get() = _savedCardList

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _deleteCard = MutableLiveData<Resource<PaymentMethodDeleteResponseModel?>?>()
    val deleteCard : LiveData<Resource<PaymentMethodDeleteResponseModel?>?> get() = _deleteCard

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _defaultCard = MutableLiveData<Resource<PaymentMethodEditResponse?>?>()
    val defaultCard : LiveData<Resource<PaymentMethodEditResponse?>?> get() = _defaultCard



    fun saveCardList(){
        viewModelScope.launch {
            try{
                _savedCardList.postValue(success(repository.savedCard()))
            }catch (e: Exception){
                _savedCardList.postValue(failure(e))
            }
        }
    }


    fun deleteCard(model: PaymentMethodDeleteModel?){
        viewModelScope.launch {
            try{
                _deleteCard.postValue(success(repository.deleteCard(model)))
            }catch (e: Exception){
                _deleteCard.postValue(failure(e))
            }
        }
    }


    fun editDefaultCard(model: PaymentMethodEditModel?){
        viewModelScope.launch {
            try{
                _defaultCard.postValue(success(repository.editDefaultCard(model)))
            }catch (e: Exception){
                _defaultCard.postValue(failure(e))
            }
        }
    }
}