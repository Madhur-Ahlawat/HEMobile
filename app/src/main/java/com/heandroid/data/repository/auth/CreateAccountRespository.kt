package com.heandroid.data.repository.auth

import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.ValidVehicleCheckRequest
import com.heandroid.data.model.createaccount.ConfirmEmailRequest
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class CreateAccountRespository @Inject constructor(private val apiService: ApiService)  {
    suspend fun createAccount(model: CreateAccountRequestModel?)= apiService.createAccount(model=model)
    suspend fun emailVerificationApiCall(requestParam: EmailVerificationRequest?) = apiService.sendEmailVerification(request=requestParam)
    suspend fun confirmEmailApiCall(requestParam: ConfirmEmailRequest?) = apiService.confirmEmailVerification(request=requestParam)
    suspend fun getVehicleDetail(vehicleNumber: String?, agencyId: Int?) = apiService.getAccountFindVehicle(vehicleNumber, agencyId)
    suspend fun validVehicleCheck(vehicleValidReqModel: ValidVehicleCheckRequest?, agencyId: Int?) = apiService.validVehicleCheck(vehicleValidReqModel, agencyId)
}