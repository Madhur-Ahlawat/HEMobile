package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.data.model.contactdartcharge.UploadFileResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryListResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryStatusRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.ServiceRequest
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
import org.json.JSONObject
import java.io.File
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class RaiseNewEnquiryViewModel @Inject constructor(
    private val repository: RaiseEnquiryRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    var enquiryModel = MutableLiveData<EnquiryModel>()
    var edit_enquiryModel = MutableLiveData<EnquiryModel>()
    var enquiryDetailsModel = MutableLiveData<ServiceRequest>()
    var enquiry_status_number = MutableLiveData<String>()
    var enquiry_last_name = MutableLiveData<String>()

    val retryEvent = MutableLiveData<Unit>()
    val noOfApiTries = MutableLiveData<Int>()

    init {
        enquiryModel.value = EnquiryModel()
        edit_enquiryModel.value = EnquiryModel()
        enquiryDetailsModel.value=ServiceRequest()
        enquiry_status_number.value=""
        enquiry_last_name.value=""
    }




}