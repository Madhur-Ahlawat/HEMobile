package com.heandroid.ui.makeoneoffpayment

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.makeoneofpayment.CrossingDetailsModelsRequest
import com.heandroid.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.heandroid.data.model.makeoneofpayment.OneOfPaymentModelRequest
import com.heandroid.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.heandroid.data.repository.makeoneofpayments.MakeOneOfPaymentRepo
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MakeOneOfPaymentViewModel @Inject constructor(private val repository: MakeOneOfPaymentRepo) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getCrossingDetails = MutableLiveData<Resource<CrossingDetailsModelsResponse?>?>()
    val getCrossingDetails: LiveData<Resource<CrossingDetailsModelsResponse?>?> get() = _getCrossingDetails


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _oneOfPaymentsPay = MutableLiveData<Resource<OneOfPaymentModelResponse?>?>()
    val oneOfPaymentsPay: LiveData<Resource<OneOfPaymentModelResponse?>?> get() = _oneOfPaymentsPay

    fun getCrossingDetails(model: CrossingDetailsModelsRequest) {
        viewModelScope.launch {
            try {

                _getCrossingDetails.postValue(
                    ResponseHandler.success(
                        repository.getCrossingDetails(
                            model
                        ), errorManager
                    )
                )

            } catch (e: Exception) {
                _getCrossingDetails.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun oneOfPaymentsPay(model: OneOfPaymentModelRequest) {

        viewModelScope.launch {
            try {
                _oneOfPaymentsPay.postValue(
                    ResponseHandler.success(
                        repository.oneOfPaymentsPay(
                            model
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _oneOfPaymentsPay.postValue(ResponseHandler.failure(e))
            }
        }
    }

}
