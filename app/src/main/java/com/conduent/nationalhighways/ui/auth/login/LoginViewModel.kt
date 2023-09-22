package com.conduent.nationalhighways.ui.auth.login

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.auth.forgot.email.LoginModel
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.data.repository.auth.LoginRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler.failure
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.Socket
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    val retryEvent = MutableLiveData<Unit>()
    val noOfApiTries = MutableLiveData<Int>()


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _login = MutableLiveData<Resource<LoginResponse?>?>()
    val login: LiveData<Resource<LoginResponse?>?> get() = _login


//    /** Error handling as UI **/
//    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
//    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
//    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate
//
//    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
//    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
//    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    init {
        noOfApiTries.value = 0
    }

    fun login(model: LoginModel?) {
        viewModelScope.launch {
            try {
                _login.postValue(success(repository.login(model), errorManager))
            } catch (e: Exception) {
                _login.postValue(failure(e))

                if (e is SocketTimeoutException) {
                    noOfApiTries.value = noOfApiTries.value!! + 1
                    // Handle timeout exception here.
                    retryEvent.postValue(Unit) // Trigger the retry popup.
                } else {
                    // Handle other exceptions.
                }
            }
        }
    }


}