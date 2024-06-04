package com.conduent.nationalhighways.ui.account.profile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.auth.forgot.password.ForgotPasswordResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.ResetPasswordModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.data.model.nominatedcontacts.NominatedContactRes
import com.conduent.nationalhighways.data.model.profile.*
import com.conduent.nationalhighways.data.repository.profile.ProfileRepository
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import com.conduent.nationalhighways.utils.common.Utils
import androidx.databinding.Observable
import com.conduent.nationalhighways.data.model.account.UserNameCheckReq
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpReq
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpResp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    val errorManager: ErrorManager
) : ViewModel(),Observable {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _userNameAvailabilityCheck = MutableLiveData<Resource<Boolean?>?>()
    val userNameAvailabilityCheck: LiveData<Resource<Boolean?>?> get() = _userNameAvailabilityCheck
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _verifyRequestCode = MutableLiveData<Resource<VerifyRequestOtpResp?>?>()
    val verifyRequestCode: LiveData<Resource<VerifyRequestOtpResp?>?> get() = _verifyRequestCode
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _accountDetail = MutableLiveData<Resource<ProfileDetailModel?>?>()
    val accountDetail: LiveData<Resource<ProfileDetailModel?>?> get() = _accountDetail

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updatePassword = MutableLiveData<Resource<ForgotPasswordResponseModel?>?>()
    val updatePassword: LiveData<Resource<ForgotPasswordResponseModel?>?> get() = _updatePassword

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _emailValidation = MutableLiveData<Resource<EmailVerificationResponse?>?>()
    val emailValidation: LiveData<Resource<EmailVerificationResponse?>?> get() = _emailValidation

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var _emailVerificationApiVal = MutableLiveData<Resource<EmailVerificationResponse?>?>()
    val emailVerificationApiVal: LiveData<Resource<EmailVerificationResponse?>?> get() = _emailVerificationApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateAccountPinApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val updateAccountPinApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _updateAccountPinApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateProfileApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val updateProfileApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _updateProfileApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateUserProfileDataApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val updateUserProfileDataApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _updateUserProfileDataApiVal
    fun verifyRequestCode(model: VerifyRequestOtpReq?) {
        viewModelScope.launch {
            try {
                _verifyRequestCode.postValue(success(repository.verifyRequestCode(model), errorManager))
            } catch (e: Exception) {
                _verifyRequestCode.postValue(ResponseHandler.failure(e))
            }
        }
    }
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getNominatedContactsApiVal = MutableLiveData<Resource<NominatedContactRes?>?>()
    val getNominatedContactsApiVal: LiveData<Resource<NominatedContactRes?>?> get() = _getNominatedContactsApiVal

    fun updateAccountPin(request: AccountPinChangeModel) {
        viewModelScope.launch {
            try {
                _updateAccountPinApiVal.postValue(
                    success(
                        repository.updateAccountPin(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _updateAccountPinApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun accountDetail() {
        viewModelScope.launch {
            try {
                _accountDetail.postValue(success(repository.accountDetail(), errorManager))
            } catch (e: Exception) {
                _accountDetail.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun emailValidationForUpdatation(model: ProfileUpdateEmailModel?) {
        viewModelScope.launch {
            try {
                _emailValidation.postValue(
                    success(
                        repository.emailValidationForUpdation(model),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _emailValidation.postValue(ResponseHandler.failure(e))
            }
        }
    }


    fun emailVerificationApi(request: EmailVerificationRequest?) {
        viewModelScope.launch {
            try {
                _emailVerificationApiVal.postValue(
                    success(
                        repository.emailVerificationApiCall(
                            request
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _emailVerificationApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }
    fun twoFAVerifyRequestCode(model: VerifyRequestOtpReq) {
        viewModelScope.launch {
            try {
                _verifyRequestCode.postValue(success(repository.twoFAVerifyOTP(model), errorManager))
            } catch (e: Exception) {
                _verifyRequestCode.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun updatePassword(model: ResetPasswordModel?) {
        viewModelScope.launch {
            try {
                _updatePassword.postValue(success(repository.updatePassword(model), errorManager))
            } catch (e: Exception) {
                _updatePassword.postValue(ResponseHandler.failure(e))
            }
        }
    }
    fun updateUserDetails(request: UpdateProfileRequest) {

        viewModelScope.launch {
            try {
                _updateProfileApiVal.postValue(
                    success(
                        repository.updateProfile(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _updateProfileApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }


    fun getNominatedContacts() {
        viewModelScope.launch {
            try {
                _getNominatedContactsApiVal.postValue(
                    success(
                        repository.getNominatedContactList(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _getNominatedContactsApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }
    fun userNameAvailabilityCheck(request: UserNameCheckReq) {
        viewModelScope.launch {
            try {
                _userNameAvailabilityCheck.postValue(
                    success(
                        repository.userNameAvailabilityCheck(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _userNameAvailabilityCheck.postValue(ResponseHandler.failure(e))
            }
        }
    }
}