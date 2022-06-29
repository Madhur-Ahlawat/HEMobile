package com.heandroid.ui.auth.forgot.password

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.R
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.auth.forgot.password.*
import com.heandroid.data.repository.auth.ForgotPasswordRepository
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler.failure
import com.heandroid.utils.common.ResponseHandler.success
import com.heandroid.utils.common.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: ForgotPasswordRepository,
    val errorManager: ErrorManager,
    val application: Application
) : ViewModel() {

    @Inject
    lateinit var sessionManager: SessionManager

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _confirmOption = MutableLiveData<Resource<ConfirmOptionResponseModel?>?>()
    val confirmOption: LiveData<Resource<ConfirmOptionResponseModel?>?> get() = _confirmOption


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _otp = MutableLiveData<Resource<SecurityCodeResponseModel?>?>()
    val otp: LiveData<Resource<SecurityCodeResponseModel?>?> get() = _otp


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _resetPassword = MutableLiveData<Resource<ForgotPasswordResponseModel?>?>()
    val resetPassword: LiveData<Resource<ForgotPasswordResponseModel?>?> get() = _resetPassword


    fun confirmOptionForForgot(model: ConfirmOptionModel?) {
        viewModelScope.launch {
            try {
                val response = repository.confirmOptionForForgot(model)
                if (response?.isSuccessful == true) {
                    val serverToken =
                        response?.headers()?.get("Authorization")?.split("Bearer ")?.get(1)
                    sessionManager.saveAuthToken(serverToken ?: "")
                    _confirmOption.postValue(Resource.Success(response.body()))
                } else {
                    _confirmOption.postValue(
                        Resource.DataError(
                            errorManager.getError(
                                response?.code() ?: 0
                            ).description
                        )
                    )
                }
            } catch (e: Exception) {
                _confirmOption.postValue(failure(e))
            }
        }
    }


    fun requestOTP(model: RequestOTPModel?) {
        viewModelScope.launch {
            try {
                _otp.postValue(success(repository.requestOTP(model), errorManager))
            } catch (e: Exception) {
                _otp.postValue(failure(e))
            }
        }
    }

    fun resetPassword(model: ResetPasswordModel?) {
        viewModelScope.launch {
            try {
                _resetPassword.postValue(success(repository.resetPassword(model), errorManager))
            } catch (e: Exception) {
                _resetPassword.postValue(failure(e))
            }
        }
    }

    fun checkPassword(model: ResetPasswordModel?): Pair<Boolean, String> {
        var ret = Pair(true, "")
        if (model?.newPassword?.equals(model.confirmPassword) == false) ret =
            Pair(false, application.getString(R.string.error_password_not_match))
        return ret
    }

}

