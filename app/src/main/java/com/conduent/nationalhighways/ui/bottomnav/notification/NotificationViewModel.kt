package com.conduent.nationalhighways.ui.bottomnav.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.repository.notification.NotificationRepo
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repo: NotificationRepo,
    val errorManager: ErrorManager
) : ViewModel() {
    val notificationCheckUncheck = MutableStateFlow<AlertMessage?>(null)
    val notificationCheckUncheckStateFlow: StateFlow<AlertMessage?> = notificationCheckUncheck

    val dismissAlertMutData = MutableLiveData<Resource<String?>?>()
    val dismissAlertLiveData: LiveData<Resource<String?>?> get() = dismissAlertMutData

    val readAlertMutData = MutableLiveData<Resource<String?>?>()
    val readAlertAlertLiveData: LiveData<Resource<String?>?> get() = readAlertMutData

    private val alertMutData = MutableLiveData<Resource<AlertMessageApiResponse?>?>()
    val alertLivData: LiveData<Resource<AlertMessageApiResponse?>?> get() = alertMutData
    fun deleteAlertItem(cssLookUpKey: String) {
        viewModelScope.launch {
            try {
                dismissAlertMutData.postValue(
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
                readAlertMutData.postValue(
                    ResponseHandler.success(
                        repo.readAlertItem(cssLookUpKey),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                readAlertMutData.postValue(ResponseHandler.failure(e))
            }
        }
    }
    fun getAlertsApi(lang: String) {
        viewModelScope.launch {
            try {
                alertMutData.postValue(
                    ResponseHandler.success(
                        repo.getAlertMessages(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                alertMutData.postValue(ResponseHandler.failure(e))
            }
        }
    }

}