package com.conduent.nationalhighways.ui.startNow.contactdartcharge

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseEnquiryHistoryRequest
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseEnquiryHistoryResponse
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseHistoryRangeModel
import com.conduent.nationalhighways.data.model.contactdartcharge.CreateNewCaseReq
import com.conduent.nationalhighways.data.model.contactdartcharge.CreateNewCaseResp
import com.conduent.nationalhighways.data.model.contactdartcharge.UploadFileResponseModel
import com.conduent.nationalhighways.data.repository.contactdartcharge.ContactDartChargeRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ContactDartChargeViewModel @Inject constructor(
    private val repository: ContactDartChargeRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _caseHistoryApiVal = MutableLiveData<Resource<CaseEnquiryHistoryResponse?>?>()
    val caseHistoryApiVal: LiveData<Resource<CaseEnquiryHistoryResponse?>?> get() = _caseHistoryApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _caseHistoryLoginApiVal = MutableLiveData<Resource<CaseEnquiryHistoryResponse?>?>()
    val caseHistoryLoginApiVal: LiveData<Resource<CaseEnquiryHistoryResponse?>?> get() = _caseHistoryLoginApiVal

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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _uploadFileVal = MutableLiveData<Resource<UploadFileResponseModel?>?>()
    val uploadFileVal: LiveData<Resource<UploadFileResponseModel?>?> get() = _uploadFileVal

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
    fun getCaseHistoryLoginData(request: CaseHistoryRangeModel?) {
        viewModelScope.launch {
            try {
                _caseHistoryLoginApiVal.postValue(
                    ResponseHandler.success(
                        repository.getCaseHistoryLoginDataApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _caseHistoryLoginApiVal.postValue(ResponseHandler.failure(e))
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

    fun getCaseSubCategoriesList(cat: String) {
        viewModelScope.launch {
            try {
                _getCaseSubCategoriesListVal.postValue(
                    ResponseHandler.success(
                        repository.getCaseSubCategoriesList(cat),
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

    fun uploadFileApi(data: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                _uploadFileVal.postValue(
                    ResponseHandler.success(
                        repository.uploadFile(data),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _uploadFileVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

}