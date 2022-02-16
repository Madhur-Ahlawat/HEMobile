package com.heandroid.network

import com.heandroid.model.*
import com.heandroid.model.crossingHistory.request.CrossingHistoryRequest
import com.heandroid.model.crossingHistory.response.CrossingHistoryApiResponse
import retrofit2.Response

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {

    override suspend fun loginApiCall(
        clientID: String,
        grantType: String,
        agecyId: String,
        clientSecret: String,
        value: String,
        password: String,
        validatePasswordCompliance: String
    ) = apiService.loginWithField(
        clientID,
        grantType,
        agecyId,
        clientSecret,
        value,
        password,
        validatePasswordCompliance
    )

    override suspend fun getLogOut()= apiService.logOut()

    override suspend fun getAccountOverviewApiCall(authToken: String) =
        apiService.getAccountOverview(authToken)

    override suspend fun getVehicleListApiCall() =
        apiService.getVehicleData()

    override suspend fun getRenewalAccessToken(
        clientId: String, grantType: String, agencyId: String, clientSecret: String,
        refreshToken: String, validatePasswordCompliance: String
    ) =
        apiService.getRenewalAccessToken(
            clientId,
            grantType,
            agencyId,
            clientSecret,
            refreshToken,
            validatePasswordCompliance
        )

    override suspend fun retrievePaymentList(
        authToken: String,
        requestParam: RetrievePaymentListRequest
    ) = apiService.retrievePaymentListApi(authToken, requestParam)

    override suspend fun getMonthlyUsageApiCall(
        authToken: String,
        requestParam: RetrievePaymentListRequest
    ) = apiService.getMonthlyUsageApi(authToken, requestParam)

    override suspend fun getForgotUserNameApiCall(
        agencyId: String,
        requestParam: ForgotUsernameRequest
    ) = apiService.recoveruserNameApi(agencyId, requestParam)

    override suspend fun getConfirmationOptionsApiCall(
        agencyId: String,
        requestParam: ConfirmationOptionRequestModel
    ): Response<ConfirmationOptionsResponseModel> =
        apiService.forgotPasswordConfirmationOptionsApi(agencyId, requestParam)


    override suspend fun getSecurityCodeApiCall( agencyId : String , requestParam:GetSecurityCodeRequestModel):Response<GetSecurityCodeResponseModel>
    = apiService.getSecurityCodeFromOptionApi(agencyId , requestParam)

    override suspend fun verifySecurityCodeApiCall(requestParam: VerifySecurityCodeRequestModel): Response<VerifySecurityCodeResponseModel>
            = apiService.verifySecurityCodeApi(requestParam)

    override suspend fun setNewPasswordApiCall(requestParam:SetNewPasswordRequest):Response<VerifySecurityCodeResponseModel>
            = apiService.setNewPasswordApi(requestParam)

    override suspend fun getAlertMessageApiCAll( language: String): Response<AlertMessageApiResponse> {
     return apiService.getAlertMessages(language)
    }
    override suspend fun addVehicleApiCall(requestParam: VehicleResponse) = apiService.addVehicleApi( requestParam)
    override suspend fun updateVehicleApiCall(requestParam: VehicleResponse) = apiService.updateVehicleApi( requestParam)
    override suspend fun crossingHistoryApiCall(requestParam: CrossingHistoryRequest?): Response<CrossingHistoryApiResponse> {
        return apiService.getVehicleCrossingHistoryData(requestParam)
    }


}