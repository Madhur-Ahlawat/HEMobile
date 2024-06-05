package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.ServiceRequest
import com.conduent.nationalhighways.data.repository.raiseEnquiry.RaiseEnquiryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val apiState = MutableLiveData<String>()
    val apiEndTime = MutableLiveData<String>()

    init {
        enquiryModel.value = EnquiryModel()
        edit_enquiryModel.value = EnquiryModel()
        enquiryDetailsModel.value=ServiceRequest()
        enquiry_status_number.value=""
        enquiry_last_name.value=""
        apiState.value=""
        apiEndTime.value=""
    }
}