package com.heandroid.ui.account.profile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.R
import com.heandroid.data.model.account.UpdateProfileRequest
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.data.model.profile.*
import com.heandroid.data.repository.profile.ProfileRepository
import com.heandroid.ui.base.BaseApplication
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import com.heandroid.utils.common.ResponseHandler.success
import com.heandroid.utils.common.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _accountDetail = MutableLiveData<Resource<ProfileDetailModel?>?>()
    val accountDetail: LiveData<Resource<ProfileDetailModel?>?> get() = _accountDetail

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updatePassword = MutableLiveData<Resource<UpdatePasswordResponseModel?>?>()
    val updatePassword: LiveData<Resource<UpdatePasswordResponseModel?>?> get() = _updatePassword

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _emailValidation = MutableLiveData<Resource<EmailVerificationResponse?>?>()
    val emailValidation: LiveData<Resource<EmailVerificationResponse?>?> get() = _emailValidation

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _emailVerificationApiVal = MutableLiveData<Resource<EmailVerificationResponse?>?>()
    val emailVerificationApiVal: LiveData<Resource<EmailVerificationResponse?>?> get() = _emailVerificationApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateAccountPinApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val updateAccountPinApiVal : LiveData<Resource<EmptyApiResponse?>?> get()  = _updateAccountPinApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateProfileApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val updateProfileApiVal : LiveData<Resource<EmptyApiResponse?>?> get()  = _updateProfileApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateUserProfileDataApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val updateUserProfileDataApiVal : LiveData<Resource<EmptyApiResponse?>?> get()  = _updateUserProfileDataApiVal

    fun updateAccountPin(request: AccountPinChangeModel) {
        viewModelScope.launch {
            try {
                _updateAccountPinApiVal.postValue(success(repository.updateAccountPin(request), errorManager))
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

    fun emailValidationForUpdatation(model : ProfileUpdateEmailModel?){
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

    fun updatePassword(model: UpdateAccountPassword?) {
        viewModelScope.launch {
            try {
                _updatePassword.postValue(success(repository.updatePassword(model), errorManager))
            } catch (e: Exception) {
                _updatePassword.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun checkPassword(model: UpdateAccountPassword?): Pair<Boolean, String> {
        var ret = Pair(true, "")
        if (!Utils.isValidPassword(model?.newPassword)) ret =
            Pair(false, BaseApplication.INSTANCE.getString(R.string.confirm_password_validation))
        else if (!Utils.isValidPassword(model?.confirmPassword)) ret =
            Pair(false, BaseApplication.INSTANCE.getString(R.string.confirm_password_validation))
        else if (model?.currentPassword?.equals(model.newPassword) == true) ret =
            Pair(false, BaseApplication.INSTANCE.getString(R.string.current_password_validation))
        return ret
    }

    fun updateUserDetails(request: UpdateProfileRequest) {
        viewModelScope.launch {
            try {
                _updateProfileApiVal.postValue(success(repository.updateProfile(request), errorManager))
            } catch (e: Exception) {
                _updateProfileApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun updateUserProfileDetailsApi(request: UpdateProfileRequest)
    {
        viewModelScope.launch {
            try {
                _updateUserProfileDataApiVal.postValue(success(repository.updateUserProfileApiCall(request), errorManager))
            }catch (e: Exception)
            {
                _updateUserProfileDataApiVal.postValue(ResponseHandler.failure(e));
            }
        }
    }

}