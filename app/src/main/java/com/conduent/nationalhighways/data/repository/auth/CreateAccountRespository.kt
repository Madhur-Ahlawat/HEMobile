package com.conduent.nationalhighways.data.repository.auth

import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.account.UserNameCheckReq
import com.conduent.nationalhighways.data.model.account.ValidVehicleCheckRequest
import com.conduent.nationalhighways.data.model.account.payment.AccountCreationRequest
import com.conduent.nationalhighways.data.model.createaccount.ConfirmEmailRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class CreateAccountRespository @Inject constructor(private val apiService: ApiService) {
    suspend fun createAccount(model: CreateAccountRequestModel?) =
        apiService.createAccount(model = model)

    suspend fun createAccountNew(model: AccountCreationRequest?) =
        apiService.createAccountNew(model = model)

    suspend fun emailVerificationApiCall(requestParam: EmailVerificationRequest?) =
        apiService.sendEmailVerification(request = requestParam)

    suspend fun confirmEmailApiCall(requestParam: ConfirmEmailRequest?) =
        apiService.confirmEmailVerification(request = requestParam)

    suspend fun getVehicleDetail(vehicleNumber: String?, agencyId: Int?) =
        apiService.getAccountFindVehicle(vehicleNumber, agencyId)

    suspend fun getNewVehicleDetail(vehicleNumber: String?, agencyId: Int?) =
        apiService.getNewAccountFindVehicle(vehicleNumber, agencyId)

    suspend fun validVehicleCheck(vehicleValidReqModel: ValidVehicleCheckRequest?, agencyId: Int?) =
        apiService.validVehicleCheck(vehicleValidReqModel, agencyId)

    suspend fun userNameAvailabilityCheck(reqModel: UserNameCheckReq?) =
        apiService.userNameAvailabilityCheck(reqModel)
}