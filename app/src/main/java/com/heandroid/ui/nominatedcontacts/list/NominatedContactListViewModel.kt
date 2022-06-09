package com.heandroid.ui.nominatedcontacts.list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.nominatedcontacts.*
import com.heandroid.data.repository.nominatedcontacts.NominatedContactsRepo
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler.failure
import com.heandroid.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel

class NominatedContactListViewModel @Inject constructor(
    private val repo: NominatedContactsRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _contactList = MutableLiveData<Resource<NominatedContactRes?>?>()
    val contactList: LiveData<Resource<NominatedContactRes?>?> get() = _contactList


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getSecondaryRights = MutableLiveData<Resource<GetSecondaryAccessRightsResp?>?>()
    val getSecondaryRights: LiveData<Resource<GetSecondaryAccessRightsResp?>?> get() = _getSecondaryRights

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getResendActivationMail = MutableLiveData<Resource<ResendRespModel?>?>()
    val getResendActivationMail: LiveData<Resource<ResendRespModel?>?> get() = _getResendActivationMail


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _terminateNominatedContact = MutableLiveData<Resource<ResponseBody?>?>()
    val terminateNominatedContact: LiveData<Resource<ResponseBody?>?> get() = _terminateNominatedContact

    fun resendActivationMailContacts(body: ResendActivationMail) {
        viewModelScope.launch {
            try {
                _getResendActivationMail.postValue(
                    success(
                        repo.resendActivationMailContacts(body),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _getResendActivationMail.postValue(failure(e))
            }

        }

    }

    fun terminateNominatedContact(body: TerminateRequestModel) {
        viewModelScope.launch {
            try {
                _terminateNominatedContact.postValue(
                    success(
                        repo.terminateNominatedContact(body),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _terminateNominatedContact.postValue(failure(e))
            }

        }

    }

    fun nominatedContactList() {
        viewModelScope.launch {

            try {
                _contactList.postValue(success(repo.getSecondaryAccount(), errorManager))
            } catch (e: Exception) {
                _contactList.postValue(failure(e))
            }

        }

    }


    // For Expand
    fun getSecondaryRights(accId: String) {
        viewModelScope.launch {
            try {
                _getSecondaryRights.postValue(
                    success(
                        repo.getSecondaryAccessRights(accId),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _getSecondaryRights.postValue(failure(e))
            }
        }
    }

}