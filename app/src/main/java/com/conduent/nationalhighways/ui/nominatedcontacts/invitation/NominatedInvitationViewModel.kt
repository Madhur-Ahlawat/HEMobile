package com.conduent.nationalhighways.ui.nominatedcontacts.invitation

import android.util.Patterns
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.nominatedcontacts.*
import com.conduent.nationalhighways.data.repository.nominatedcontacts.NominatedContactsRepo
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler.failure
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NominatedInvitationViewModel @Inject constructor(
    private val repository: NominatedContactsRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _createAccount = MutableLiveData<Resource<CreateProfileDetailModelModel?>?>()
    val createAccount: LiveData<Resource<CreateProfileDetailModelModel?>?> get() = _createAccount


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateAccount = MutableLiveData<Resource<ResponseBody?>?>()
    val updateAccount: LiveData<Resource<ResponseBody?>?> get() = _updateAccount


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateAccessRight = MutableLiveData<Resource<ResponseBody?>?>()
    val updateAccessRight: LiveData<Resource<ResponseBody?>?> get() = _updateAccessRight


    fun createAccount(model: CreateAccountRequestModel?) {
        viewModelScope.launch {
            try {
                _createAccount.postValue(
                    success(
                        repository.createSecondaryAccount(model),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _createAccount.postValue(failure(e))
            }
        }
    }

    fun updateAccessRight(model: UpdateAccessRightModel?) {
        viewModelScope.launch {
            try {
                _updateAccessRight.postValue(
                    success(
                        repository.updateAccessRight(model),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _updateAccessRight.postValue(failure(e))
            }
        }
    }


    fun updateSecondaryAccountData(body: CreateAccountRequestModel?) {
        viewModelScope.launch {
            try {
                _updateAccount.postValue(
                    success(
                        repository.updateSecondaryAccount(body),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _updateAccount.postValue(failure(e))
            }
        }
    }


    fun validationFullName(model: CreateAccountRequestModel?): Pair<Boolean, String> {
        var ret = Pair(true, "")
        if (model?.firstName?.isEmpty() == true) ret =
            Pair(false, BaseApplication.INSTANCE?.getString(R.string.error_first_name) ?: "")
        else if (model?.lastName?.isEmpty() == true) ret =
            Pair(false, BaseApplication.INSTANCE?.getString(R.string.error_last_name) ?: "")
        return ret
    }


    fun validationEmail(model: CreateAccountRequestModel?): Pair<Boolean, String> {
        var ret = Pair(true, "")
        if (model?.emailId?.isEmpty() == true) ret =
            Pair(false, BaseApplication.INSTANCE?.getString(R.string.error_email) ?: "")
        else if (!Patterns.EMAIL_ADDRESS.matcher(model?.emailId).matches()) ret =
            Pair(false, BaseApplication.INSTANCE?.getString(R.string.error_valid_email) ?: "")
        return ret
    }

}