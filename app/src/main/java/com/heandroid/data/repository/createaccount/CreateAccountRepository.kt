package com.heandroid.data.repository.createaccount


import com.heandroid.data.model.createaccount.ConfirmEmailRequest
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryDownloadRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.vehicle.DeleteVehicleRequest
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class CreateAccountRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun emailVerificationApiCall(requestParam: EmailVerificationRequest?)
            = apiService.sendEmailVerification(requestParam)

    suspend fun confirmEmailApiCall(requestParam: ConfirmEmailRequest?) =
        apiService.confirmEmailVerification(requestParam)


}