package com.conduent.nationalhighways.ui.websiteservice

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
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

}