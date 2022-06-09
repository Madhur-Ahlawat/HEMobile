package com.heandroid.ui.auth.logout

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.auth.login.AuthResponseModel
import com.heandroid.data.repository.auth.LogoutRepository
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler.failure
import com.heandroid.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val repository: LogoutRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _logout = MutableLiveData<Resource<AuthResponseModel?>?>()
    val logout: LiveData<Resource<AuthResponseModel?>?> get() = _logout

    fun logout() {
        viewModelScope.launch {
            try {
                _logout.postValue(success(repository.logout(), errorManager))
            } catch (e: java.lang.Exception) {
                _logout.postValue(failure(e))
            }
        }
    }
}