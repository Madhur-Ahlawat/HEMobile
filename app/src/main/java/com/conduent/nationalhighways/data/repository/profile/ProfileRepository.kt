package com.conduent.nationalhighways.data.repository.profile

import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.auth.forgot.password.ResetPasswordModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.profile.AccountPinChangeModel
import com.conduent.nationalhighways.data.model.profile.ProfileUpdateEmailModel
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun emailVerificationApiCall(requestParam: EmailVerificationRequest?) = apiService.sendEmailVerification(request=requestParam)
    suspend fun accountDetail() = apiService.getUserProfileData()
    suspend fun emailValidationForUpdation(model: ProfileUpdateEmailModel?) = apiService.emailValidationForUpdation(model)
    suspend fun updatePassword(model: ResetPasswordModel?)= apiService.updatePassword(model=model)
    suspend fun updateProfile(model: UpdateProfileRequest?)= apiService.updateProfileData(model)
    suspend fun updateAccountPin(model: AccountPinChangeModel?)= apiService.updateAccountPin(model)
    suspend fun getNominatedContactList()=apiService.getNominatedUserList()
}