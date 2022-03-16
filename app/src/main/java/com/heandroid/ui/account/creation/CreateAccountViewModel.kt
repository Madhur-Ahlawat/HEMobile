package com.heandroid.ui.account.creation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.CreateAccountResponseModel
import com.heandroid.data.repository.auth.CreateAccountRespository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(private val repository: CreateAccountRespository): BaseViewModel()  {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _createAccount = MutableLiveData<Resource<CreateAccountResponseModel?>?>()
    val createAccount : LiveData<Resource<CreateAccountResponseModel?>?> get()  = _createAccount



    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _emailVerificationApiVal = MutableLiveData<Resource<EmailVerificationResponse?>?>()
    val emailVerificationApiVal: LiveData<Resource<EmailVerificationResponse?>?> get() = _emailVerificationApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _confirmEmailApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val confirmEmailApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _confirmEmailApiVal



    fun createAccount(model: CreateAccountRequestModel?) {
        viewModelScope.launch {
            try {
                _createAccount.postValue(success(repository.createAccount(model), errorManager))
            } catch (e: Exception) {
                _createAccount.postValue(failure(e))
            }
        }
    }
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