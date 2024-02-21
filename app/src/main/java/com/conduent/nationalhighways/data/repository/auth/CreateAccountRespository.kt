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

    suspend fun getHeartBeat(agencyId: String, referenceId: String) =
        apiService.getHeartBeat(agencyId, referenceId)


    suspend fun emailVerificationApiCall(requestParam: EmailVerificationRequest?) =
        apiService.sendEmailVerification(request = requestParam)

    suspend fun confirmEmailApiCall(requestParam: ConfirmEmailRequest?) =
        apiService.confirmEmailVerification(request = requestParam)

    suspend fun getOneOffVehicleDetail(vehicleNumber: String?, agencyId: Int?) =
        apiService.getOneOffAccountFindVehicle(vehicleNumber, agencyId)

    suspend fun getVehicleDetail(vehicleNumber: String?, agencyId: Int?) =
        apiService.getAccountFindVehicle(vehicleNumber, agencyId)

    suspend fun getVehiclePlateInfo(vehicleNumber: String?, agencyId: Int?) =
        apiService.getVehiclePlateInfo(vehicleNumber, agencyId)

    suspend fun getNewVehicleDetail(vehicleNumber: String?, agencyId: Int?) =
        apiService.getNewAccountFindVehicle(vehicleNumber, agencyId)

    suspend fun validVehicleCheck(vehicleValidReqModel: ValidVehicleCheckRequest?, agencyId: Int?) =
        apiService.validVehicleCheck(vehicleValidReqModel, agencyId)

    suspend fun userNameAvailabilityCheck(reqModel: UserNameCheckReq?) =
        apiService.userNameAvailabilityCheck(reqModel)
}