package com.heandroid.ui.auth.forgot.password

import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.password.ConfirmOptionModel
import com.heandroid.data.model.auth.forgot.password.RequestOTPModel
import com.heandroid.data.model.auth.forgot.password.ResetPasswordModel
import com.heandroid.data.model.auth.forgot.password.ConfirmOptionResponseModel
import com.heandroid.data.model.auth.forgot.password.ForgotPasswordResponseModel
import com.heandroid.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.heandroid.data.repository.auth.ForgotPasswordRepository
import com.heandroid.ui.base.BaseApplication
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler.failure
import com.heandroid.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val repository: ForgotPasswordRepository): BaseViewModel() {


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _confirmOption = MutableLiveData<Resource<ConfirmOptionResponseModel?>?>()
    val confirmOption : LiveData<Resource<ConfirmOptionResponseModel?>?> get()  = _confirmOption


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _otp = MutableLiveData<Resource<SecurityCodeResponseModel?>?>()
    val otp : LiveData<Resource<SecurityCodeResponseModel?>?> get()  = _otp


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _resetPassword = MutableLiveData<Resource<ForgotPasswordResponseModel?>?>()
    val resetPassword : LiveData<Resource<ForgotPasswordResponseModel?>?> get()  = _resetPassword


    fun confirmOptionForForgot(model: ConfirmOptionModel?){
        viewModelScope.launch {
            try {
                _confirmOption.postValue(success(repository.confirmOptionForForgot(model),errorManager))
            } catch (e: Exception) {
                _confirmOption.postValue(failure(e))
            }
        }
    }


    fun requestOTP(model: RequestOTPModel?){
        viewModelScope.launch {
            try {
                _otp.postValue(success(repository.requestOTP(model), errorManager))
            } catch (e: Exception) {
                _otp.postValue(failure(e))
            }
        }
    }

    fun resetPassword(model: ResetPasswordModel?){
        viewModelScope.launch {
            try {
                _resetPassword.postValue(success(repository.resetPassword(model), errorManager))
            } catch (e: Exception) {
                _resetPassword.postValue(failure(e))
            }
        }
    }


    fun validation(model: ConfirmOptionModel?): Pair<Boolean,String> {
        var ret=Pair(true,"")
        if (TextUtils.isEmpty(model?.identifier)) ret=Pair(false,BaseApplication.INSTANCE.getString(R.string.error_email))
        else if (TextUtils.isEmpty(model?.zipCode)) ret=Pair(false,BaseApplication.INSTANCE.getString(R.string.error_postal_code))
        return ret
    }


    fun checkPassword(model: ResetPasswordModel?): Pair<Boolean,String> {
        var ret=Pair(true,"")
        if (model?.newPassword?.equals(model.confirmPassword)==false) ret=Pair(false,BaseApplication.INSTANCE.getString(R.string.error_password_not_match))
        return ret
    }

}

