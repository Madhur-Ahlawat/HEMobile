package com.heandroid.ui.bottomnav.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.repository.notification.NotificationViewAllRepo
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewAllViewModel @Inject constructor(
    val repo: NotificationViewAllRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    private val alertMutData = MutableLiveData<Resource<String?>?>()
    val alertLivData: LiveData<Resource<String?>?> get() = alertMutData

    private val _readMutData = MutableLiveData<Resource<String?>?>()
    val readMutData: LiveData<Resource<String?>?> get() = _readMutData

    fun deleteAlertItem(cssLookUpKey: String) {
        viewModelScope.launch {
            try {
                alertMutData.postValue(
                    ResponseHandler.success(
                        repo.deleteAlertItem(cssLookUpKey),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                alertMutData.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun readAlertItem(cssLookUpKey: String) {
        viewModelScope.launch {
            try {
                _readMutData.postValue(
                    ResponseHandler.success(
                        repo.deleteAlertItem(cssLookUpKey),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _readMutData.postValue(ResponseHandler.failure(e))
            }
        }
    }
}