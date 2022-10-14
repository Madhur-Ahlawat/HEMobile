package com.conduent.nationalhighways.ui.auth.forgot.email

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.auth.forgot.email.ForgotEmailModel
import com.conduent.nationalhighways.data.model.auth.forgot.email.ForgotEmailResponseModel
import com.conduent.nationalhighways.data.repository.auth.ForgotEmailRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotEmailViewModel @Inject constructor(
    private val repository: ForgotEmailRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _forgotEmail = MutableLiveData<Resource<ForgotEmailResponseModel?>?>()
    val forgotEmail: LiveData<Resource<ForgotEmailResponseModel?>?> get() = _forgotEmail

    fun forgotEmail(model: ForgotEmailModel?) {
        viewModelScope.launch {
            try {
                _forgotEmail.postValue(
                    ResponseHandler.success(
                        repository.forgotEmail(model),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _forgotEmail.postValue(ResponseHandler.failure(e))
            }
        }
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

