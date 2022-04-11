package com.heandroid.data.remote

import com.heandroid.BuildConfig.*
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.account.AccountResponse
import com.heandroid.data.model.account.ThresholdAmountApiResponse
import com.heandroid.data.model.webstatus.WebSiteStatus
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryRequest
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryResponse
import com.heandroid.data.model.address.DataAddress
import com.heandroid.data.model.auth.forgot.email.ForgotEmailModel
import com.heandroid.data.model.auth.forgot.email.ForgotEmailResponseModel
import com.heandroid.data.model.auth.forgot.password.*
import com.heandroid.data.model.auth.login.AuthResponseModel
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.data.model.contactdartcharge.*
import com.heandroid.data.model.createaccount.ConfirmEmailRequest
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryDownloadRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.manualtopup.PaymentWithExistingCardModel
import com.heandroid.data.model.manualtopup.PaymentWithNewCardModel
import com.heandroid.data.model.nominatedcontacts.*
import com.heandroid.data.model.notification.AlertMessageApiResponse
import com.heandroid.data.model.payment.*
import com.heandroid.data.model.profile.ProfileDetailModel
import com.heandroid.data.model.profile.ProfileUpdateEmailModel
import com.heandroid.data.model.profile.UpdateAccountPassword
import com.heandroid.data.model.profile.UpdatePasswordResponseModel
import com.heandroid.data.model.tollrates.TollRatesResp
import com.heandroid.data.model.vehicle.DeleteVehicleRequest
import com.heandroid.data.model.vehicle.VehicleResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

typealias  createAccountModel = com.heandroid.data.model.account.CreateAccountRequestModel
typealias  respAccountModel = com.heandroid.data.model.account.CreateAccountResponseModel

interface ApiService {


    @FormUrlEncoded
    @POST(LOGIN)
    suspend fun login(@Field("client_id") clientId: String? = CLIENT_ID,
                      @Field("grant_type") grant_type: String? = GRANT_TYPE,
                      @Field("agencyID") agencyID: String? = AGENCY_ID,
                      @Field("client_secret") client_secret: String? = CLIENT_SECRET,
                      @Field("value") value: String?,
                      @Field("password") password: String?,
                      @Field("validatePasswordCompliance") validatePasswordCompliance: String?): Response<LoginResponse?>?


    @DELETE(LOGOUT)
    suspend fun logout(): Response<AuthResponseModel?>


    @POST(FORGOT_EMAIL)
    suspend fun forgotEmail(@Query("agencyId") agencyId: String?,
                            @Body body: ForgotEmailModel?): Response<ForgotEmailResponseModel?>?


    @POST(FORGOT_CONFIRM_OPTION)
    suspend fun confirmOptionForForgot(@Query("agencyId") agencyId: String?,
                                       @Body body: ConfirmOptionModel?): Response<ConfirmOptionResponseModel?>?


    @POST(REQUEST_OTP)
    suspend fun requestOTP(@Query("agencyId") agencyId: String?,
                           @Body model: RequestOTPModel?): Response<SecurityCodeResponseModel?>?


    @POST(RESET_PASSWORD)
    suspend fun resetPassword(@Query("agencyId") agencyId: String?,
                              @Body model: ResetPasswordModel?): Response<ForgotPasswordResponseModel?>?


    @GET(VEHICLE)
    suspend fun getVehicleData(): Response<List<VehicleResponse?>?>?


    @POST(VEHICLE)
    suspend fun addVehicleApi(@Body model: VehicleResponse?): Response<EmptyApiResponse?>?

    @PUT(VEHICLE)
    suspend fun updateVehicleApi(@Body model: VehicleResponse?): Response<EmptyApiResponse?>?

    @POST(DELETE_VEHICLE)
    suspend fun deleteVehicle(@Body model: DeleteVehicleRequest?): Response<EmptyApiResponse?>?

    @POST(VEHICLE_CROSSING_HISTORY)
    suspend fun getVehicleCrossingHistoryData(@Body model: CrossingHistoryRequest?): Response<CrossingHistoryApiResponse?>?

    @Streaming
    @POST(DOWNLOAD_TRANSACTION)
    suspend fun getDownloadTransactionListDataInFile(@Body request: CrossingHistoryDownloadRequest?): Response<ResponseBody?>?


    @POST(CREATE_SECONDARY_ACCOUNT)
    suspend fun createSecondaryAccount(@Body model: CreateAccountRequestModel?): Response<CreateAccountResponseModel?>?

    @GET(SECONDARY_ACCOUNT)
    suspend fun getSecondaryAccount(): Response<NominatedContactRes?>

    @GET(SECONDARY_ACCESS_RIGHTS)
    suspend fun getSecondaryAccessRights(@Path("accountId") accountId: String): Response<GetSecondaryAccessRightsResp?>

    @PUT(UPDATE_SECONDARY_ACCOUNT)
    suspend fun updateSecondaryAccount(@Body body: CreateAccountRequestModel?): Response<ResponseBody?>

    @PUT(UPDATE_SECONDARY_ACCESS_RIGHTS)
    suspend fun updateAccessRight(@Body body: UpdateAccessRightModel?): Response<ResponseBody?>

    @POST(RESEND_ACTIVATION_MAIL)
    suspend fun resendActivationMailContacts(@Body body: ResendActivationMail?): Response<ResendRespModel?>

    @PUT(ACCOUNT_TERMINATED)
    suspend fun terminateNominatedContact(@Body body: TerminateRequestModel?): Response<ResponseBody?>

    @GET(TOLL_RATES)
    suspend fun getTollRates(): Response<List<TollRatesResp>?>?

    @POST(GET_ALERT_MESSAGES)
    suspend fun getAlertMessages(@Query("language") language: String): Response<AlertMessageApiResponse?>

    @POST(DISMISS_ALERT)
    suspend fun dismissAlert(@Query("cscLookupKey") itemKey: String): Response<String?>

    @POST(EMAIL_VERIFICATION_REQUEST)
    suspend fun sendEmailVerification(@Query("agencyId") agencyId: String? = AGENCY_ID,
                                      @Body request: EmailVerificationRequest?): Response<EmailVerificationResponse?>?

    @POST(CONFIRM_EMAIL_VERIFICATION)
    suspend fun confirmEmailVerification(@Query("agencyId") agencyId: String? = AGENCY_ID,
                                         @Body request: ConfirmEmailRequest?): Response<EmptyApiResponse?>?

    @GET(WEB_SITE_SERVICE_STATUS)
    suspend fun webSiteServiceStatus(): Response<WebSiteStatus?>?


    @POST(CREATE_ACCOUNT)
    suspend fun createAccount(@Query("agencyId") agencyId: String = AGENCY_ID,
                              @Body model: com.heandroid.data.model.account.CreateAccountRequestModel?): Response<com.heandroid.data.model.account.CreateAccountResponseModel?>?

    @GET(FETCH_ADDRESS_BASED_ON_POSTAL_CODE)
    suspend fun getAddressListBasedOnPostalCode(@Query("agencyId") agencyId: String = AGENCY_ID,
                                                @Query("search") postCode: String): Response<List<DataAddress>>

    @GET(FIND_VEHICLE_ACCOUNT)
    suspend fun getAccountFindVehicle(@Path("vehicleNumber") vehicleNumber: String?,
                                      @Query("agencyId") agencyId: Int?): Response<VehicleInfoDetails?>?

    @POST(GET_GENERAL_ACCOUNT_SR_LIST)
    suspend fun getCaseHistoryData(@Body request: CaseEnquiryHistoryRequest?,
                                   @Query("agencyId") agencyId: String = AGENCY_ID): Response<CaseEnquiryHistoryResponse?>

    @GET(ACCOUNT_DETAIL)
    suspend fun accountDetail(@Query("agencyId") agencyId: String? = AGENCY_ID): Response<ProfileDetailModel?>?

    @GET(GET_CASE_ENQUIRIES_CATEGORY)
    suspend fun getCaseCategoriesList(@Query("agencyId") agencyId: String? = AGENCY_ID): Response<List<CaseCategoriesModel?>?>?

    @GET(GET_CASE_ENQUIRIES_SUB_CATEGORY)
    suspend fun getCaseSubCategoriesList(@Query("agencyId") agencyId: String? = AGENCY_ID): Response<List<CaseCategoriesModel?>?>?

    @POST(CREATE_NEW_CASE)
    suspend fun createNewCase(@Body modelReq: CreateNewCaseReq?,
                              @Query("agencyId") agencyId: String? = AGENCY_ID): Response<CreateNewCaseResp?>?

    @Multipart
    @POST(UPLOAD_FILE)
    suspend fun uploadFile(@Part file: MultipartBody.Part?): Response<UploadFileResponseModel?>?

    @PUT(EMAIL_VERIFICATION_FOR_UPDATION)
    suspend fun emailValidationForUpdation(@Body model: ProfileUpdateEmailModel?): Response<EmailVerificationResponse?>?

    @PUT(UPDATE_PASSWORD)
    suspend fun updatePassword(@Body model: UpdateAccountPassword?): Response<UpdatePasswordResponseModel?>?

    @POST(PAYMENT_HISTORY_TRANSACTION_LIST)
    suspend fun getPaymentHistoryData(@Body request: AccountPaymentHistoryRequest?): Response<AccountPaymentHistoryResponse?>

    @GET(ACCOUNT_DETAILS)
    suspend fun getAccountDetailsData():Response<AccountResponse?>?

    @GET(VIEW_ACCOUNT_BALANCE)
    suspend fun getThresholdValue() :Response<ThresholdAmountApiResponse?>?

    @GET(SAVED_CARD_LIST)
    suspend fun savedCard(@Query("agencyId") agencyId: String? = AGENCY_ID):Response<PaymentMethodResponseModel?>?



    @HTTP(method = "DELETE", path = SAVED_CARD_LIST, hasBody = true)
    suspend fun deleteCard(@Query("agencyId") agencyId: String? = AGENCY_ID,
                           @Body model: PaymentMethodDeleteModel?) : Response<PaymentMethodDeleteResponseModel?>?


    @POST(EDIT_CARD)
    suspend fun editDefaultCard(@Query("agencyId") agencyId: String? = AGENCY_ID,
                                @Body model : PaymentMethodEditModel?) : Response<PaymentMethodEditResponse?>?


    @POST(SAVED_CARD_LIST)
    suspend fun savedNewCard(@Query("agencyId") agencyId: String? = AGENCY_ID,
                             @Body model : AddCardModel?) : Response<PaymentMethodDeleteResponseModel?>?


    @POST(PAYMENT_WITH_NEW_CARD)
    suspend fun paymentWithNewCard(@Query("agencyId") agencyId: String? = AGENCY_ID,
                                   @Body model : PaymentWithNewCardModel?) : Response<PaymentMethodDeleteResponseModel?>?



    @POST(PAYMENT_WITH_EXISTING_CARD)
    suspend fun paymentWithExistingCard(@Query("agencyId") agencyId: String? = AGENCY_ID,
                                        @Body model : PaymentWithExistingCardModel?) : Response<PaymentMethodDeleteResponseModel?>?

}