package com.heandroid.ui.websiteservice

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.webstatus.WebSiteStatus
import com.heandroid.data.repository.websiteservice.WebsiteServiceRepository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebSiteServiceViewModel @Inject constructor(private val repository: WebsiteServiceRepository):BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _webService= MutableLiveData<Resource<WebSiteStatus?>>()
    val webService : LiveData<Resource<WebSiteStatus?>> get() = _webService

    fun tollRates() {
        viewModelScope.launch {
            try {
                _webService.postValue(ResponseHandler.success(repository.webSiteServiceStatus(), errorManager))
            } catch (e: Exception) {
                _webService.postValue(ResponseHandler.failure(e))
            }
        }
    }

}