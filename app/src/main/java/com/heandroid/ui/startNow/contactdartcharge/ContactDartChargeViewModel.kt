package com.heandroid.ui.startNow.contactdartcharge

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.contactdartcharge.*
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getCaseCategoriesListVal =
        MutableLiveData<Resource<List<CaseCategoriesModel?>?>?>()
    val getCaseCategoriesListVal: LiveData<Resource<List<CaseCategoriesModel?>?>?> get() = _getCaseCategoriesListVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getCaseSubCategoriesListVal =
        MutableLiveData<Resource<List<CaseCategoriesModel?>?>?>()
    val getCaseSubCategoriesListVal: LiveData<Resource<List<CaseCategoriesModel?>?>?> get() = _getCaseSubCategoriesListVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _createNewCaseVal = MutableLiveData<Resource<CreateNewCaseResp?>?>()
    val createNewCaseVal: LiveData<Resource<CreateNewCaseResp?>?> get() = _createNewCaseVal

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

    fun getCaseCategoriesList() {
        viewModelScope.launch {
            try {
                _getCaseCategoriesListVal.postValue(
                    ResponseHandler.success(
                        repository.getCaseCategoriesList(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _getCaseCategoriesListVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getCaseSubCategoriesList() {
        viewModelScope.launch {
            try {
                _getCaseSubCategoriesListVal.postValue(
                    ResponseHandler.success(
                        repository.getCaseSubCategoriesList(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _getCaseSubCategoriesListVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun createNewCase(request: CreateNewCaseReq?) {
        viewModelScope.launch {
            try {
                _createNewCaseVal.postValue(
                    ResponseHandler.success(
                        repository.createNewCase(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _createNewCaseVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

}