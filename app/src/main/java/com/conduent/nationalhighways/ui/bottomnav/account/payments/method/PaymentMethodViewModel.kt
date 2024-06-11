package com.conduent.nationalhighways.ui.bottomnav.account.payments.method

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.payment.*
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.data.repository.payment.PaymentMethodRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import com.conduent.nationalhighways.utils.common.ResponseHandler.failure
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    val repository: PaymentMethodRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)

    val _savedCardListState = MutableStateFlow<Resource<PaymentMethodResponseModel?>?>(null)
    val savedCardState: StateFlow<Resource<PaymentMethodResponseModel?>?> get() = _savedCardListState

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
     val _saveDirectDebitNewCardState = MutableStateFlow<Resource<PaymentMethodDeleteResponseModel?>?>(null)
    val saveDirectDebitNewCardState: StateFlow<Resource<PaymentMethodDeleteResponseModel?>?> get() = _saveDirectDebitNewCardState


    private val _savedCardList = MutableLiveData<Resource<PaymentMethodResponseModel?>?>()
    val savedCardList: LiveData<Resource<PaymentMethodResponseModel?>?> get() = _savedCardList

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _deleteCard = MutableLiveData<Resource<PaymentMethodDeleteResponseModel?>?>()
    val deleteCard: LiveData<Resource<PaymentMethodDeleteResponseModel?>?> get() = _deleteCard


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val _deleteCard_State = MutableStateFlow<Resource<PaymentMethodDeleteResponseModel?>?>(null)
    val deleteCardState: StateFlow<Resource<PaymentMethodDeleteResponseModel?>?> get() = _deleteCard_State


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _defaultCard = MutableLiveData<Resource<PaymentMethodEditResponse?>?>()
    val defaultCard: LiveData<Resource<PaymentMethodEditResponse?>?> get() = _defaultCard


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _saveNewCard = MutableLiveData<Resource<PaymentMethodDeleteResponseModel?>?>()
    val saveNewCard: LiveData<Resource<PaymentMethodDeleteResponseModel?>?> get() = _saveNewCard


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _saveDirectDebitNewCard =
        MutableLiveData<Resource<PaymentMethodDeleteResponseModel?>?>()
    val saveDirectDebitNewCard: LiveData<Resource<PaymentMethodDeleteResponseModel?>?> get() = _saveDirectDebitNewCard



    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _deletePrimaryCard = MutableLiveData<Resource<PaymentMethodDeleteResponseModel?>?>()
    val deletePrimaryCard: LiveData<Resource<PaymentMethodDeleteResponseModel?>?> get() = _deletePrimaryCard


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _accountDetail = MutableLiveData<Resource<ProfileDetailModel?>?>()
    val accountDetail: LiveData<Resource<ProfileDetailModel?>?> get() = _accountDetail


    fun saveCardListState() {
        viewModelScope.launch {
            try {
                _savedCardListState.emit(
                    success(
                        repository.savedCard(
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _savedCardListState.emit(failure(e))
            }
        }
    }

    fun saveDirectDebitNewCardState(model: SaveNewCardRequest?) {
        viewModelScope.launch {
            try {
                _saveDirectDebitNewCardState.emit(
                    success(
                        repository.saveDirectDebitNewCard(
                            model
                        )
                    )
                )
            } catch (e: Exception) {
                _saveDirectDebitNewCardState.emit(failure(e))
            }
        }
    }

    fun saveCardList() {
        viewModelScope.launch {
            try {
                _savedCardList.postValue(
                    success(
                        repository.savedCard(
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _savedCardList.postValue(failure(e))
            }
        }
    }


    fun deleteCard(model: PaymentMethodDeleteModel?) {
        viewModelScope.launch {
            try {
                _deleteCard.postValue(success(repository.deleteCard(model)))
            } catch (e: Exception) {
                _deleteCard.postValue(failure(e))
            }
        }
    }
    fun deleteCardState(model: PaymentMethodDeleteModel?) {
        viewModelScope.launch {
            try {
                _deleteCard_State.emit(success(repository.deleteCard(model)))
            } catch (e: Exception) {
                _deleteCard_State.emit(failure(e))
            }
        }
    }


    fun editDefaultCard(model: PaymentMethodEditModel?) {
        viewModelScope.launch {
            try {
                _defaultCard.postValue(success(repository.editDefaultCard(model)))
            } catch (e: Exception) {
                _defaultCard.postValue(failure(e))
            }
        }
    }


    fun saveNewCard(model: AddCardModel?) {
        viewModelScope.launch {
            try {
                _saveNewCard.postValue(success(repository.saveNewCard(model)))
            } catch (e: Exception) {
                _saveNewCard.postValue(failure(e))
            }
        }
    }

    fun saveDirectDebitNewCard(model: SaveNewCardRequest?) {
        viewModelScope.launch {
            try {
                _saveDirectDebitNewCard.postValue(
                    success(
                        repository.saveDirectDebitNewCard(
                            model
                        )
                    )
                )
            } catch (e: Exception) {
                _saveDirectDebitNewCard.postValue(failure(e))
            }
        }
    }

    fun deletePrimaryCard() {
        viewModelScope.launch {
            try {
                _deletePrimaryCard.postValue(success(repository.deletePrimaryCard()))
            } catch (e: Exception) {
                _deletePrimaryCard.postValue(failure(e))
            }
        }
    }


    fun accountDetail() {
        viewModelScope.launch {
            try {
                _accountDetail.postValue(success(repository.accountDetail()))
            } catch (e: Exception) {
                _accountDetail.postValue(failure(e))
            }
        }
    }
}