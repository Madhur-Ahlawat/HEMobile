package com.heandroid.ui.websiteservice

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.webstatus.WebSiteStatus
import com.heandroid.data.repository.websiteservice.WebsiteServiceRepository
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
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

    fun tollRates() {
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

}