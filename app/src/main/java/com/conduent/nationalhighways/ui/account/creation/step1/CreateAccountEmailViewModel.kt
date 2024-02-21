package com.conduent.nationalhighways.ui.account.creation.step1

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.UserNameCheckReq
import com.conduent.nationalhighways.data.model.createaccount.ConfirmEmailRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.data.repository.auth.CreateAccountRespository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler.failure
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountEmailViewModel @Inject constructor(
    private val repository: CreateAccountRespository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _emailVerificationApiVal = MutableLiveData<Resource<EmailVerificationResponse?>?>()
    val emailVerificationApiVal: LiveData<Resource<EmailVerificationResponse?>?> get() = _emailVerificationApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _userNameAvailabilityCheck = MutableLiveData<Resource<Boolean?>?>()
    val userNameAvailabilityCheck: LiveData<Resource<Boolean?>?> get() = _userNameAvailabilityCheck

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _confirmEmailApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val confirmEmailApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _confirmEmailApiVal


    val emailVerificationApi = MutableStateFlow<Resource<EmailVerificationResponse?>?>(null)
    val emailVerificationApiValStateFlow: StateFlow<Resource<EmailVerificationResponse?>?> = emailVerificationApi

    fun emailVerificationApiMutable(request: EmailVerificationRequest?) {
        viewModelScope.launch {
            try {
                emailVerificationApi.emit(
                    success(
                        repository.emailVerificationApiCall(
                            request
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                emailVerificationApi.emit(failure(e))
            }
        }
    }



    fun emailVerificationApi(request: EmailVerificationRequest?) {
        viewModelScope.launch {
            try {
                _emailVerificationApiVal.postValue(
                    success(
                        repository.emailVerificationApiCall(
                            request
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _emailVerificationApiVal.postValue(failure(e))
            }
        }
    }

    fun confirmEmailApi(request: ConfirmEmailRequest) {
        viewModelScope.launch {
            try {
                _confirmEmailApiVal.postValue(
                    success(
                        repository.confirmEmailApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _confirmEmailApiVal.postValue(failure(e))
            }
        }
    }
    fun userNameAvailabilityCheck(request: UserNameCheckReq) {
        viewModelScope.launch {
            try {
                _userNameAvailabilityCheck.postValue(
                    success(
                        repository.userNameAvailabilityCheck(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _userNameAvailabilityCheck.postValue(failure(e))
            }
        }
    }
}