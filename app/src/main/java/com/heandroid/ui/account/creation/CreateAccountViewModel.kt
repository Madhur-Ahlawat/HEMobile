package com.heandroid.ui.account.creation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.createaccount.ConfirmEmailRequest
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.repository.createaccount.CreateAccountRepository
import com.heandroid.data.repository.vehicle.VehicleRepository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(private val repository: CreateAccountRepository) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _emailVerificationApiVal = MutableLiveData<Resource<EmailVerificationResponse?>?>()
    val emailVerificationApiVal: LiveData<Resource<EmailVerificationResponse?>?> get() = _emailVerificationApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _confirmEmailApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val confirmEmailApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _confirmEmailApiVal

    fun emailVerificationApi(request: EmailVerificationRequest?) {
        viewModelScope.launch {
            try {
                _emailVerificationApiVal.postValue(
                    ResponseHandler.success(
                        repository.emailVerificationApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _emailVerificationApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun confirmEmailApi(request: ConfirmEmailRequest) {
        viewModelScope.launch {
            try {
                _confirmEmailApiVal.postValue(
                    ResponseHandler.success(
                        repository.confirmEmailApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _confirmEmailApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }


}