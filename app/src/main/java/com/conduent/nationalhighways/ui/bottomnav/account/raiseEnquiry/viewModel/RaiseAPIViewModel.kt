package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.data.model.contactdartcharge.UploadFileResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryListResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryStatusRequest
import com.conduent.nationalhighways.data.repository.raiseEnquiry.RaiseEnquiryRepository
import com.conduent.nationalhighways.utils.MimeType
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class RaiseAPIViewModel @Inject constructor(
    val repository: RaiseEnquiryRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    private val _enquiryResponseModel = MutableLiveData<Resource<EnquiryResponseModel?>?>()
    val enquiryResponseLiveData: LiveData<Resource<EnquiryResponseModel?>?> get() = _enquiryResponseModel


    private val _categoriesData = MutableLiveData<Resource<List<CaseCategoriesModel?>?>?>()
    val categoriesLiveData: LiveData<Resource<List<CaseCategoriesModel?>?>?> get() = _categoriesData

    private val _subcategoriesData = MutableLiveData<Resource<List<CaseCategoriesModel?>?>?>()
    val subcategoriesLiveData: LiveData<Resource<List<CaseCategoriesModel?>?>?> get() = _subcategoriesData


    private val _uploadFileModel = MutableLiveData<Resource<UploadFileResponseModel?>?>()
    val uploadFileLiveData: LiveData<Resource<UploadFileResponseModel?>?> get() = _uploadFileModel


    private val _getAccountSRList = MutableLiveData<Resource<EnquiryListResponseModel?>?>()
    val getAccountSRList: LiveData<Resource<EnquiryListResponseModel?>?> get() = _getAccountSRList

    fun getAccountSRList() {
        viewModelScope.launch {
            try {
                _getAccountSRList.postValue(
                    ResponseHandler.success(
                        repository.getAccountSRList(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _getAccountSRList.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getAccountSRDetails(jsonObject: EnquiryStatusRequest) {
        viewModelScope.launch {
            try {
                _getAccountSRList.postValue(
                    ResponseHandler.success(
                        repository.getGeneralAccountSRList(jsonObject),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _getAccountSRList.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            try {
                _categoriesData.postValue(
                    ResponseHandler.success(
                        repository.categoriesApiCall(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _categoriesData.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getSubCategories(category: String) {
        viewModelScope.launch {
            try {
                _subcategoriesData.postValue(
                    ResponseHandler.success(
                        repository.subcategoriesApiCall(
                            category
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _subcategoriesData.postValue(ResponseHandler.failure(e))
            }
        }
    }


    fun raiseEnquiryApi(
        enquiryRequest: EnquiryRequest
    ) {
        viewModelScope.launch {
            try {

                _enquiryResponseModel.postValue(
                    ResponseHandler.success(
                        repository.raiseEnquiryApi(
                            enquiryRequest
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _enquiryResponseModel.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun uploadFileApi(
        file: File
    ) {

        val requestFile: RequestBody =
            file.asRequestBody(MimeType.selectMimeType(file).toMediaTypeOrNull())
        val data = MultipartBody.Part.createFormData("file", file.name, requestFile)

        viewModelScope.launch {
            try {

                _uploadFileModel.postValue(
                    ResponseHandler.success(
                        repository.uploadFile(
                            data
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _uploadFileModel.postValue(ResponseHandler.failure(e))
                if (e is SocketTimeoutException) {
//                    noOfApiTries.value = noOfApiTries.value!! + 1
                    // Handle timeout exception here.
//                    retryEvent.postValue(Unit) // Trigger the retry popup.
                } else {
                    // Handle other exceptions.
                }
            }
        }
    }

}