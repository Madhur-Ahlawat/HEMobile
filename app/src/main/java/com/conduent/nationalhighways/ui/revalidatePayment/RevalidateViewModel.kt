package com.conduent.nationalhighways.ui.revalidatePayment

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithExistingCardModel
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithNewCardModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.revalidate.RevalidateCardModel
import com.conduent.nationalhighways.data.repository.manualtopup.ManualTopUpRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler.failure
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RevalidateViewModel @Inject constructor(
    private val repository: ManualTopUpRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _paymentWithExistingCard =
        MutableLiveData<Resource<PaymentMethodDeleteResponseModel?>?>()
    val paymentWithExistingCard: LiveData<Resource<PaymentMethodDeleteResponseModel?>?> get() = _paymentWithExistingCard


    fun paymentWithExistingCard(model: RevalidateCardModel?) {
        viewModelScope.launch {
            try {
                _paymentWithExistingCard.postValue(success(repository.paymentWithExistingCard(model)))
            } catch (e: Exception) {
                _paymentWithExistingCard.postValue(failure(e))
            }
        }
    }


}