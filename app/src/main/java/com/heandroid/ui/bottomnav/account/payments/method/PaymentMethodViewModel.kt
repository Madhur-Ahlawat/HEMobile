package com.heandroid.ui.bottomnav.account.payments.method

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.payment.*
import com.heandroid.data.model.profile.ProfileDetailModel
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



    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _saveNewCard = MutableLiveData<Resource<PaymentMethodDeleteResponseModel?>?>()
    val saveNewCard : LiveData<Resource<PaymentMethodDeleteResponseModel?>?> get() = _saveNewCard



    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _accountDetail = MutableLiveData<Resource<ProfileDetailModel?>?>()
    val accountDetail : LiveData<Resource<ProfileDetailModel?>?> get()  = _accountDetail




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


    fun saveNewCard(model: AddCardModel?){
        viewModelScope.launch {
            try{
                _saveNewCard.postValue(success(repository.saveNewCard(model)))
            }catch (e: Exception){
                _saveNewCard.postValue(failure(e))
            }
        }
    }


    fun accountDetail(){
        viewModelScope.launch {
            try{
                _accountDetail.postValue(success(repository.accountDetail()))
            }catch (e: Exception){
                _accountDetail.postValue(failure(e))
            }
        }
    }
}