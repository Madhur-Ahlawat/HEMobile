package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryResponseModel
import com.conduent.nationalhighways.data.repository.raiseEnquiry.RaiseEnquiryRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RaiseNewEnquiryViewModel @Inject constructor(
    private val repository: RaiseEnquiryRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    private val _categoriesData = MutableLiveData<Resource<List<CaseCategoriesModel?>?>?>()
    val categoriesLiveData: LiveData<Resource<List<CaseCategoriesModel?>?>?> get() = _categoriesData

    private val _subcategoriesData = MutableLiveData<Resource<List<CaseCategoriesModel?>?>?>()
    val subcategoriesLiveData: LiveData<Resource<List<CaseCategoriesModel?>?>?> get() = _subcategoriesData

    var enquiryModel = MutableLiveData<EnquiryModel>()

    private val _enquiryResponseModel = MutableLiveData<Resource<EnquiryResponseModel?>?>()
    val enquiryResponseLiveData: LiveData<Resource<EnquiryResponseModel?>?> get() = _enquiryResponseModel

    init {
        enquiryModel.value = EnquiryModel()
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
        Log.d("TAG", "raiseEnquiryApi() called with: enquiryRequest = $enquiryRequest")
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
}