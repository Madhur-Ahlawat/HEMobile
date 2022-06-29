package com.heandroid.ui.payment

import android.util.Patterns
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.R
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.makeoneofpayment.CrossingDetailsModelsRequest
import com.heandroid.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.heandroid.data.model.makeoneofpayment.OneOfPaymentModelRequest
import com.heandroid.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.heandroid.data.model.payment.PaymentReceiptDeliveryTypeSelectionRequest
import com.heandroid.data.repository.makeoneofpayments.MakeOneOfPaymentRepo
import com.heandroid.ui.base.BaseApplication
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class MakeOneOfPaymentViewModel @Inject constructor(
    private val repository: MakeOneOfPaymentRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getCrossingDetails = MutableLiveData<Resource<CrossingDetailsModelsResponse?>?>()
    val getCrossingDetails: LiveData<Resource<CrossingDetailsModelsResponse?>?> get() = _getCrossingDetails

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _whereToReceivePaymentReceipt = MutableLiveData<Resource<ResponseBody?>?>()
    val whereToReceivePaymentReceipt: LiveData<Resource<ResponseBody?>?> get() = _whereToReceivePaymentReceipt

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

    fun validationEmail(model: String?, model2: String, type: Int): Pair<Boolean, String> {
        var ret = Pair(true, "")
        if (model?.isEmpty() == true && model2.isEmpty()) ret =
            Pair(false, BaseApplication.INSTANCE.getString(R.string.error_email))
        else if (model != model2)
            ret =
                Pair(false, BaseApplication.INSTANCE.getString(R.string.error_confirm_mail))
        else if (type == 0) {
            if (!Patterns.EMAIL_ADDRESS.matcher(model).matches()) ret = Pair(
                false, BaseApplication.INSTANCE.getString(
                    R.string.error_valid_email
                )
            )
        }
        return ret
    }

    fun whereToReceivePaymentReceipt(request: PaymentReceiptDeliveryTypeSelectionRequest) {
        viewModelScope.launch {
            try {
                _whereToReceivePaymentReceipt.postValue(
                    ResponseHandler.success(
                        repository.whereToReceivePaymentReceipt(
                            request
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _whereToReceivePaymentReceipt.postValue(ResponseHandler.failure(e))
            }
        }
    }


}
