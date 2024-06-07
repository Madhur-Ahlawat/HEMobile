package com.conduent.nationalhighways.ui.auth.suspended

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithExistingCardModel
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithNewCardModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.repository.manualtopup.ManualTopUpRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler.failure
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManualTopUpViewModel @Inject constructor(
    private val repository: ManualTopUpRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _paymentWithNewCard =
        MutableLiveData<Resource<PaymentMethodDeleteResponseModel?>?>()
    val paymentWithNewCard: LiveData<Resource<PaymentMethodDeleteResponseModel?>?> get() = _paymentWithNewCard


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _paymentWithExistingCard =
        MutableLiveData<Resource<PaymentMethodDeleteResponseModel?>?>()
    val paymentWithExistingCard: LiveData<Resource<PaymentMethodDeleteResponseModel?>?> get() = _paymentWithExistingCard


    fun paymentWithNewCard(model: PaymentWithNewCardModel?) {
        viewModelScope.launch {
            try {
                _paymentWithNewCard.postValue(success(repository.paymentWithNewCard(model)))
            } catch (e: Exception) {
                _paymentWithNewCard.postValue(failure(e))
            }
        }
    }


    fun paymentWithExistingCard(model: PaymentWithExistingCardModel?) {
        viewModelScope.launch {
            try {
                _paymentWithExistingCard.postValue(success(repository.paymentWithExistingCard(model)))
            } catch (e: Exception) {
                _paymentWithExistingCard.postValue(failure(e))
            }
        }
    }


}