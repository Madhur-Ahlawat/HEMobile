package com.heandroid.ui.startNow.contactdartcharge

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.contactdartcharge.CaseEnquiryHistoryRequest
import com.heandroid.data.model.contactdartcharge.CaseEnquiryHistoryResponse
import com.heandroid.data.repository.contactdartcharge.ContactDartChargeRepository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDartChargeViewModel @Inject constructor(private val repository: ContactDartChargeRepository) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _caseHistoryApiVal = MutableLiveData<Resource<CaseEnquiryHistoryResponse?>?>()
    val caseHistoryApiVal: LiveData<Resource<CaseEnquiryHistoryResponse?>?> get() = _caseHistoryApiVal

    fun getCaseHistoryData(request: CaseEnquiryHistoryRequest?) {
        viewModelScope.launch {
            try {
                _caseHistoryApiVal.postValue(
                    ResponseHandler.success(
                        repository.getCaseHistoryDataApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _caseHistoryApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

}