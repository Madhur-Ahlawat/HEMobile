package com.heandroid.data.repository.profile

import com.heandroid.data.model.account.UpdateProfileRequest
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.profile.AccountPinChangeModel
import com.heandroid.data.model.profile.ProfileUpdateEmailModel
import com.heandroid.data.model.profile.UpdateAccountPassword
import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun emailVerificationApiCall(requestParam: EmailVerificationRequest?) = apiService.sendEmailVerification(request=requestParam)
    suspend fun accountDetail() = apiService.accountDetail()
    suspend fun emailValidationForUpdation(model: ProfileUpdateEmailModel?) = apiService.emailValidationForUpdation(model)
    suspend fun updatePassword(model: UpdateAccountPassword?)= apiService.updatePassword(model)
    suspend fun updateProfile(model: UpdateProfileRequest)= apiService.updateProfileData(model)
    suspend fun updateAccountPin(model: AccountPinChangeModel)= apiService.updateAccountPin(model)
    suspend fun updateUserProfileApiCall(model: UpdateProfileRequest)= apiService.updateUserProfileApi(model)
}