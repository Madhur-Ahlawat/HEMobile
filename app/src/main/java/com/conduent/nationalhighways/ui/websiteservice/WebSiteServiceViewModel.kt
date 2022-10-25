package com.conduent.nationalhighways.ui.websiteservice

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.data.model.webstatus.WebSiteStatus
import com.conduent.nationalhighways.data.repository.websiteservice.WebsiteServiceRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebSiteServiceViewModel @Inject constructor(
    private val repository: WebsiteServiceRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _webService = MutableLiveData<Resource<WebSiteStatus?>>()
    val webServiceLiveData: LiveData<Resource<WebSiteStatus?>> get() = _webService

    private val _pushNotification = MutableLiveData<Resource<EmptyApiResponse?>>()
    val pushNotification: LiveData<Resource<EmptyApiResponse?>> get() = _pushNotification

    fun checkServiceStatus() {
        viewModelScope.launch {
            try {
                _webService.postValue(
                    ResponseHandler.success(
                        repository.webSiteServiceStatus(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _webService.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun allowPushNotification(request: PushNotificationRequest) {
        viewModelScope.launch {
            try {
                _pushNotification.postValue(
                    ResponseHandler.success(
                        repository.allowPushNotification(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _pushNotification.postValue(ResponseHandler.failure(e))
            }
        }
    }

}