package com.heandroid.data.remote

import com.heandroid.data.model.response.EmptyApiResponse
import com.heandroid.data.model.response.login.LoginResponse
import com.heandroid.data.model.response.vehicle.CrossingHistoryApiResponse
import com.heandroid.data.model.request.vehicle.CrossingHistoryDownloadRequest
import com.heandroid.data.model.request.vehicle.CrossingHistoryRequest
import com.heandroid.data.model.response.vehicle.VehicleResponse
import okhttp3.ResponseBody
import retrofit2.Response

interface ApiHelper {

    suspend fun loginApiCall(
        clientID: String,
        grantType: String,
        agencyId: String,
        clientSecret: String,
        value: String,
        password: String,
        validatePasswordCompliance: String
    )
            : Response<LoginResponse>

    //    suspend fun getLogOut() : Response<LogOutResp>
//    suspend fun getAccountOverviewApiCall(authToken:String) : Response<AccountResponse>
    suspend fun getVehicleListApiCall(): Response<List<VehicleResponse>>
    suspend fun getRenewalAccessToken(
        clientId: String, grantType: String, agencyId: String, clientSecret: String,
        refreshToken: String, validatePasswordCompliance: String
    )
            : Response<LoginResponse>

    //    suspend fun retrievePaymentList(authToken: String , requestParam:RetrievePaymentListRequest):Response<RetrievePaymentListApiResponse>
//    suspend fun getMonthlyUsageApiCall(authToken: String , requestParam:RetrievePaymentListRequest):Response<RetrievePaymentListApiResponse>
//    suspend fun getForgotUserNameApiCall(agencyId : String ,  requestParam:ForgotUsernameRequest):Response<ForgotUsernameApiResponse>
//    suspend fun getConfirmationOptionsApiCall( agencyId : String , requestParam:ConfirmationOptionRequestModel):Response<ConfirmationOptionsResponseModel>
//    suspend fun getSecurityCodeApiCall( agencyId : String , requestParam:GetSecurityCodeRequestModel):Response<GetSecurityCodeResponseModel>
//    suspend fun verifySecurityCodeApiCall(requestParam:VerifySecurityCodeRequestModel):Response<VerifySecurityCodeResponseModel>
//    suspend fun setNewPasswordApiCall(requestParam:SetNewPasswordRequest):Response<VerifySecurityCodeResponseModel>
//    suspend fun getAlertMessageApiCAll( requestParam:String):Response<AlertMessageApiResponse>
    suspend fun addVehicleApiCall(requestParam: VehicleResponse): Response<EmptyApiResponse>
    suspend fun updateVehicleApiCall(requestParam: VehicleResponse): Response<EmptyApiResponse>
    suspend fun crossingHistoryApiCall(requestParam: CrossingHistoryRequest?): Response<CrossingHistoryApiResponse>
    suspend fun downloadCrossingHistoryAPiCall(requestParam: CrossingHistoryDownloadRequest): Response<ResponseBody>


}