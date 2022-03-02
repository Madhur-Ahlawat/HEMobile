package com.heandroid.ui.auth.forgot.email

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.email.ForgotEmailModel
import com.heandroid.data.model.auth.forgot.email.ForgotEmailResponseModel
import com.heandroid.data.repository.auth.ForgotEmailRepository
import com.heandroid.ui.base.BaseApplication
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotEmailViewModel @Inject constructor(private val repository: ForgotEmailRepository) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _forgotEmail = MutableLiveData<Resource<ForgotEmailResponseModel?>?>()
    val forgotEmail: LiveData<Resource<ForgotEmailResponseModel?>?> get() = _forgotEmail

    fun forgotEmail(model: ForgotEmailModel?) {
        viewModelScope.launch {
            try {
                _forgotEmail.postValue(ResponseHandler.success(repository.forgotEmail(model), errorManager))
            } catch (e: Exception) {
                _forgotEmail.postValue(ResponseHandler.failure(e))
            }
        }
    }


    fun validation(model: ForgotEmailModel?): Pair<Boolean, String> {
        var ret = Pair(true, "")
        if (model?.accountNumber?.isEmpty() == true && model.zipCode?.isEmpty() == true) ret =
            Pair(false, BaseApplication.INSTANCE.getString(R.string.txt_error_account_zip))
        else if (model?.accountNumber?.isEmpty() == true) ret =
            Pair(false, BaseApplication.INSTANCE.getString(R.string.txt_error_account_number))
        else if (model?.zipCode?.isEmpty() == true) ret =
            Pair(false, BaseApplication.INSTANCE.getString(R.string.txt_error_zip_code))
        return ret
    }

    fun loadUserName(username: String): StringBuffer {
        val buffer = StringBuffer()
        for (i in username.indices) {
            if (i > 1 && i < username.length - 2) buffer.append("*")
            else buffer.append(username[i])
        }
        return buffer
    }

}

