package com.conduent.nationalhighways.data.remote

import com.conduent.nationalhighways.BuildConfig.*
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.*
import com.conduent.nationalhighways.data.model.account.payment.AccountCreationRequest
import com.conduent.nationalhighways.data.model.accountpayment.*
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.data.model.auth.forgot.email.ForgotEmailModel
import com.conduent.nationalhighways.data.model.auth.forgot.email.ForgotEmailResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.*
import com.conduent.nationalhighways.data.model.auth.login.AuthResponseModel
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.data.model.checkpaidcrossings.*
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModel
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsResp
import com.conduent.nationalhighways.data.model.communicationspref.SearchProcessParamsModelReq
import com.conduent.nationalhighways.data.model.communicationspref.SearchProcessParamsModelResp
import com.conduent.nationalhighways.data.model.contactdartcharge.*
import com.conduent.nationalhighways.data.model.createaccount.ConfirmEmailRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.makeoneofpayment.OneOfPaymentModelRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithExistingCardModel
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithNewCardModel
import com.conduent.nationalhighways.data.model.nominatedcontacts.*
import com.conduent.nationalhighways.data.model.nominatedcontacts.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.nominatedcontacts.CreateProfileDetailModelModel
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.payment.*
import com.conduent.nationalhighways.data.model.profile.*
import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryListResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryStatusRequest
import com.conduent.nationalhighways.data.model.tollrates.TollRatesResp
import com.conduent.nationalhighways.data.model.vehicle.*
import com.conduent.nationalhighways.data.model.webstatus.WebSiteStatus
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.request.LrdsEligibiltyRequest
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.response.LrdsEligibilityResponse
import com.conduent.nationalhighways.ui.vehicle.newVehicleManagement.AddVehicleRequest
import com.conduent.nationalhighways.utils.common.Constants.AGENCY_ID
import com.conduent.nationalhighways.utils.common.Constants.PHONE_COUNTRY_CODE
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    @FormUrlEncoded
    @POST(LOGIN)
    suspend fun refreshToken(
        @Field("grant_type") grant_type: String = REFRESH_TOKEN,
        @Field("client_id") clientId: String = CLIENT_ID,
        @Field("client_secret") client_secret: String = CLIENT_SECRET,
        @Field("refresh_token") refresh_token: String
    ): Response<LoginResponse?>?

    @FormUrlEncoded
    @POST(LOGIN)
    suspend fun login(
        @Field("client_id") clientId: String? = CLIENT_ID,
        @Field("grant_type") grant_type: String? = GRANT_TYPE,
        @Field("agencyID") agencyID: String? = AGENCY_ID,
        @Field("client_secret") client_secret: String? = CLIENT_SECRET,
        @Field("value") value: String?,
        @Field("password") password: String?,
        @Field("validatePasswordCompliance") validatePasswordCompliance: String?
    ): Response<LoginResponse?>?

    @DELETE(LOGOUT)
    suspend fun logout(): Response<AuthResponseModel?>

    @POST(FORGOT_EMAIL)
    suspend fun forgotEmail(
        @Query("agencyId") agencyId: String?,
        @Body body: ForgotEmailModel?
    ): Response<ForgotEmailResponseModel?>?

    @POST(FORGOT_CONFIRM_OPTION)
    suspend fun confirmOptionForForgot(
        @Query("agencyId") agencyId: String?,
        @Body body: ConfirmOptionModel?
    ): Response<ConfirmOptionResponseModel?>?

    @POST(REQUEST_OTP)
    suspend fun requestOTP(
        @Query("agencyId") agencyId: String?,
        @Body model: RequestOTPModel?
    ): Response<SecurityCodeResponseModel?>?


    @POST(VERIFY_REQUEST_CODE)
    suspend fun verifyRequestCode(
        @Body model: VerifyRequestOtpReq?
    ): Response<VerifyRequestOtpResp?>?

    @POST(TWO_FA_CONFIRMATION_OPTION)
    suspend fun twoFAConfirmOption(@Query("agencyId") agencyId: String?): Response<ConfirmOptionResponseModel>

    @POST(TWO_FA_REQUEST_OTP)
    suspend fun twoFARequestCode(
        @Query("agencyId") agencyId: String?,
        @Body model: RequestOTPModel?
    ): Response<SecurityCodeResponseModel?>?

    @POST(TWO_FA_VERIFY_OTP)
    suspend fun twoFAVerifyRequestCode(
        @Query("agencyId") agencyId: String?,
        @Body model: VerifyRequestOtpReq?
    ): Response<VerifyRequestOtpResp?>?

    @POST(RESET_PASSWORD)
    suspend fun resetPassword(
        @Query("agencyId") agencyId: String?,
        @Body model: ResetPasswordModel?
    ): Response<ForgotPasswordResponseModel?>?

    @GET(VEHICLE)
    suspend fun getVehicleData(
        @Query("startIndex") startIndex: String?,
        @Query("count") count: String?
    ): Response<List<VehicleResponse?>?>?
  @GET(VEHICLELIST)
    suspend fun getVehicleListData(
        @Query("startIndex") startIndex: String?,
        @Query("count") count: String?
    ): Response<VehicleListResponse?>?

    @GET(VEHICLE_GROUP_UNALLOCATED)
    suspend fun getUnAllocatedVehicles(): Response<List<VehicleResponse?>?>?

    @POST(VEHICLE)
    suspend fun addVehicleApi(@Body model: VehicleResponse?): Response<EmptyApiResponse?>?

    @POST(VEHICLE)
    suspend fun addVehicleApiNew(@Body model: AddVehicleRequest?): Response<EmptyApiResponse?>?

    @PUT(VEHICLE)
    suspend fun updateVehicleApi(@Body model: VehicleResponse?): Response<EmptyApiResponse?>?

    @POST(DELETE_VEHICLE)
    suspend fun deleteVehicle(@Body model: DeleteVehicleRequest?): Response<EmptyApiResponse?>?

    @POST(VEHICLE_CROSSING_HISTORY)
    suspend fun getVehicleCrossingHistoryData(
        @Body model: CrossingHistoryRequest?
    ): Response<CrossingHistoryApiResponse?>?

    @Streaming
    @POST(DOWNLOAD_TRANSACTION)
    suspend fun getDownloadTransactionListDataInFile(
        @Body request: TransactionHistoryDownloadRequest?
    ): Response<ResponseBody?>?

    @POST(CREATE_SECONDARY_ACCOUNT)
    suspend fun createSecondaryAccount(
        @Body model: CreateAccountRequestModel?
    ): Response<CreateProfileDetailModelModel?>?

    @GET(SECONDARY_ACCOUNT)
    suspend fun getSecondaryAccount(): Response<NominatedContactRes?>?

    @GET(SECONDARY_ACCESS_RIGHTS)
    suspend fun getSecondaryAccessRights(
        @Path("accountId") accountId: String?
    ): Response<GetSecondaryAccessRightsResp?>?

    @PUT(UPDATE_SECONDARY_ACCOUNT)
    suspend fun updateSecondaryAccount(
        @Body body: CreateAccountRequestModel?
    ): Response<ResponseBody?>?

    @PUT(UPDATE_SECONDARY_ACCESS_RIGHTS)
    suspend fun updateAccessRight(
        @Body body: UpdateAccessRightModel?
    ): Response<ResponseBody?>?

    @POST(RESEND_ACTIVATION_MAIL)
    suspend fun resendActivationMailContacts(
        @Body body: ResendActivationMail?
    ): Response<ResendRespModel?>?

    @PUT(ACCOUNT_TERMINATED)
    suspend fun terminateNominatedContact(
        @Body body: TerminateRequestModel?
    ): Response<ResponseBody?>?

    @GET(TOLL_RATES)
    suspend fun getTollRates(): Response<List<TollRatesResp?>?>?

    @POST(GET_ALERT_MESSAGES)
    suspend fun getAlertMessages(
        @Query("language") language: String?
    ): Response<AlertMessageApiResponse?>?

    @POST(DISMISS_ALERT)
    suspend fun dismissAlert(@Query("cscLookupKey") itemKey: String?): Response<String?>?

    @POST(READ_ALERT)
    suspend fun readAlert(@Query("cscLookupKey") itemKey: String?): Response<String?>?

    @POST(EMAIL_VERIFICATION_REQUEST)
    suspend fun sendEmailVerification(
        @Query("agencyId") agencyId: String? = AGENCY_ID,
        @Body request: EmailVerificationRequest?
    ): Response<EmailVerificationResponse?>?

    @POST(CONFIRM_EMAIL_VERIFICATION)
    suspend fun confirmEmailVerification(
        @Query("agencyId") agencyId: String? = AGENCY_ID,
        @Body request: ConfirmEmailRequest?
    ): Response<EmptyApiResponse?>?

    @GET(WEB_SITE_SERVICE_STATUS)
    suspend fun webSiteServiceStatus(): Response<WebSiteStatus?>?

    @POST(CREATE_ACCOUNT)
    suspend fun createAccount(
        @Query("agencyId") agencyId: String? = AGENCY_ID,
        @Body model: com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel?
    ): Response<com.conduent.nationalhighways.data.model.account.CreateProfileDetailModelModel?>?

    @POST(CREATE_ACCOUNT)
    suspend fun createAccountNew(
        @Query("agencyId") agencyId: String? = AGENCY_ID,
        @Body model: AccountCreationRequest?
    ): Response<com.conduent.nationalhighways.data.model.account.CreateProfileDetailModelModel?>?

    @GET(FETCH_ADDRESS_BASED_ON_POSTAL_CODE)
    suspend fun getAddressListBasedOnPostalCode(
        @Query("agencyId") agencyId: String? = AGENCY_ID,
        @Query("search") postCode: String?
    ): Response<List<DataAddress?>?>?

    @GET(FIND_VEHICLE_ACCOUNT)
    suspend fun getAccountFindVehicle(
        @Path("vehicleNumber") vehicleNumber: String?,
        @Query("agencyId") agencyId: Int?
    ): Response<VehicleInfoDetails?>?

    @GET(FIND_VEHICLE_ACCOUNT)
    suspend fun getOneOffAccountFindVehicle(
        @Path("vehicleNumber") vehicleNumber: String?,
        @Query("agencyId") agencyId: Int?
    ): Response<ArrayList<NewVehicleInfoDetails>?>?

    @GET(FIND_VEHICLE_ACCOUNT)
    suspend fun getVehiclePlateInfo(
        @Path("vehicleNumber") vehicleNumber: String?,
        @Query("agencyId") agencyId: Int?
    ): Response<GetPlateInfoResponseModel?>?

    @GET(FIND_VEHICLE_ACCOUNT)
    suspend fun getNewAccountFindVehicle(
        @Path("vehicleNumber") vehicleNumber: String?,
        @Query("agencyId") agencyId: Int?
    ): Response<List<NewVehicleInfoDetails?>>?

    @POST(GET_GENERAL_ACCOUNT_SR_LIST)
    suspend fun getCaseHistoryData(
        @Body request: CaseEnquiryHistoryRequest?,
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<CaseEnquiryHistoryResponse?>?

    @POST(GET_ACCOUNT_SR_LIST)
    suspend fun getLoginCaseHistoryData(
        @Body request: CaseHistoryRangeModel?,
    ): Response<CaseEnquiryHistoryResponse?>?

    @GET(ACCOUNT_DETAILS)
    suspend fun accountDetail(
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<ProfileDetailModel?>?

    @GET(GET_CASE_ENQUIRIES_CATEGORY)
    suspend fun getCaseCategoriesList(
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<List<CaseCategoriesModel?>?>?

    @GET(GET_CASE_ENQUIRIES_SUB_CATEGORY)
    suspend fun getCaseSubCategoriesList(
        @Path("category") category: String?,
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<List<CaseCategoriesModel?>?>?

    @POST(CREATE_NEW_CASE)
    suspend fun createNewCase(
        @Body modelReq: CreateNewCaseReq?,
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<CreateNewCaseResp?>?

    @Multipart
    @POST(UPLOAD_FILE)
    suspend fun uploadFile(@Part file: MultipartBody.Part?): Response<UploadFileResponseModel?>?

    @PUT(EMAIL_VERIFICATION_FOR_UPDATION)
    suspend fun emailValidationForUpdation(
        @Body model: ProfileUpdateEmailModel?
    ): Response<EmailVerificationResponse?>?

    @PUT(UPDATE_PASSWORD)
    suspend fun updatePassword(
        @Body model: ResetPasswordModel?,
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<ForgotPasswordResponseModel?>?

    @POST(PAYMENT_HISTORY_TRANSACTION_LIST)
    suspend fun getPaymentHistoryData(
        @Body request: AccountPaymentHistoryRequest?
    ): Response<AccountPaymentHistoryResponse?>?

    @POST(PAYMENT_HISTORY_TRANSACTION_LIST_CHECK_CROSSINGS)
    suspend fun getTransactionsListCheckCrossings(
        @Body request: CheckedCrossingTransactionsRequestModel?
    ): Response<CheckedCrossingRecentTransactionsResponseModel?>?

    @GET(ACCOUNT_DETAILS)
    suspend fun getAccountDetailsData(): Response<ProfileDetailModel?>?

    @POST(Heart_Beat)
    suspend fun getHeartBeat(
        @Query("agencyId") agencyId: String? = AGENCY_ID,
        @Query("referenceId") referenceId: String
    ): Response<EmptyApiResponse?>?

    @GET(VIEW_ACCOUNT_BALANCE)
    suspend fun getThresholdValue(): Response<ThresholdAmountApiResponse?>?

    @GET(SAVED_CARD_LIST)
    suspend fun savedCard(
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<PaymentMethodResponseModel?>?

    @HTTP(method = "DELETE", path = SAVED_CARD_LIST, hasBody = true)
    suspend fun deleteCard(
        @Query("agencyId") agencyId: String? = AGENCY_ID,
        @Body model: PaymentMethodDeleteModel?
    ): Response<PaymentMethodDeleteResponseModel?>?

    @POST(EDIT_CARD)
    suspend fun editDefaultCard(
        @Query("agencyId") agencyId: String? = AGENCY_ID,
        @Body model: PaymentMethodEditModel?
    ): Response<PaymentMethodEditResponse?>?

    @POST(SAVED_CARD_LIST)
    suspend fun savedNewCard(
        @Query("agencyId") agencyId: String? = AGENCY_ID,
        @Body model: AddCardModel?
    ): Response<PaymentMethodDeleteResponseModel?>?

    @GET(DELETE_PRIMARY_CARD)
    suspend fun deletePrimaryCard(): Response<PaymentMethodDeleteResponseModel>

    @POST(SAVED_CARD_LIST)
    suspend fun saveDirectDebitNewCard(
        @Body model: SaveNewCardRequest?
    ): Response<PaymentMethodDeleteResponseModel?>?


    @POST(PAYMENT_WITH_NEW_CARD)
    suspend fun paymentWithNewCard(
        @Body model: PaymentWithNewCardModel?
    ): Response<PaymentMethodDeleteResponseModel?>?

    @POST(PAYMENT_WITH_EXISTING_CARD)
    suspend fun paymentWithExistingCard(
        @Body model: PaymentWithExistingCardModel?
    ): Response<PaymentMethodDeleteResponseModel?>?

    @GET(VIEW_ACCOUNT_BALANCE)
    suspend fun getThresholdValuePayment(): Response<AccountGetThresholdResponse?>?

    @PUT(UPDATE_ACCOUNT_BALANCE)
    suspend fun updateThresholdValue(
        @Body request: AccountTopUpUpdateThresholdRequest?
    ): Response<AccountTopUpUpdateThresholdResponse?>?

    @POST(WHERE_TO_RECEIVE_PAYMENT_RECEIPT)
    suspend fun whereToReceivePaymentReceipt(
        @Body request: PaymentReceiptDeliveryTypeSelectionRequest?
    ): Response<ResponseBody?>?

    @GET(ACCOUNT_DETAILS)
    suspend fun getUserProfileData(): Response<ProfileDetailModel?>?

    @PUT(UPDATE_ACCOUNT_SETTINGS)
    suspend fun updateProfileData(
        @Body request: UpdateProfileRequest?
    ): Response<EmptyApiResponse?>?

    @PUT(ACCOUNT_PIN)
    suspend fun updateAccountPin(
        @Body request: AccountPinChangeModel?
    ): Response<EmptyApiResponse?>?

    @POST(VALID_VEHICLE_CHECK)
    suspend fun validVehicleCheck(
        @Body request: ValidVehicleCheckRequest?,
        @Query("agencyId") agencyId: Int?
    ): Response<String?>?

    @POST(LRDS_ELIGIBILITY_CHECK)
    suspend fun lrdsEligibilityCheck(@Body request: LrdsEligibiltyRequest): Response<LrdsEligibilityResponse?>

    @PUT(UPDATE_ACCOUNT_SETTINGS)
    suspend fun updateAccountSettingPrefs(
        @Body request: UpdateProfileRequest?
    ): Response<EmptyApiResponse?>?

    @GET(VEHICLE_GROUP)
    suspend fun getVehicleGroupList(): Response<List<VehicleGroupResponse?>?>?

    @POST(VEHICLE_GROUP)
    suspend fun addVehicleGroup(
        @Body request: AddDeleteVehicleGroup?
    ): Response<VehicleGroupMngmtResponse?>?

    @PUT(VEHICLE_GROUP)
    suspend fun renameVehicleGroup(
        @Body request: RenameVehicleGroup?
    ): Response<VehicleGroupMngmtResponse?>?

    @POST(VEHICLE_GROUP_DELETE)
    suspend fun deleteVehicleGroup(
        @Body request: AddDeleteVehicleGroup?
    ): Response<VehicleGroupMngmtResponse?>?

    @GET(VEHICLE_GROUP_VEHICLE_LIST)
    suspend fun getVehiclesListOfGroup(
        @Path("vehicleGroup") vehicleGroup: String?
    ): Response<List<VehicleResponse?>?>?

    @GET(VEHICLE_GROUP_VEHICLE_LIST_SEARCH)
    suspend fun getSearchVehiclesForGroup(
        @Path("vehicleGroup") vehicleGroup: String?,
        @Path("plateNumber") plateNumber: String?
    ): Response<List<VehicleResponse?>?>?

    @GET(ACCOUNT_SETTINGS)
    suspend fun getAccountSettings(): Response<ProfileDetailModel?>?

    @PUT(UPDATE_COMMUNICATION_PREFS)
    suspend fun updateCommunicationPrefs(
        @Body model: CommunicationPrefsRequestModel?
    ): Response<CommunicationPrefsResp?>?

    @GET(SECONDARY_ACCOUNT)
    suspend fun getNominatedUserList(): Response<NominatedContactRes?>?

    @POST(GET_CROSSING_DETAILS)
    suspend fun getCrossingDetails(
        @Body model: CrossingDetailsModelsRequest?
    ): Response<CrossingDetailsModelsResponse?>?

    @POST(ONE_OF_PAYMENTS_PAY)
    suspend fun oneOfPaymentsPay(
        @Body model: OneOfPaymentModelRequest?,
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<OneOfPaymentModelResponse?>?

    @GET(DOWNLOAD_VRM_VEHICLE_LIST)
    suspend fun getDownloadVehicleList(
        @Query("type") type: String?
    ): Response<ResponseBody?>?

    @PUT(VEHICLE_VRM_EDIT)
    suspend fun updateVehicleListManagement(
        @Body request: VehicleListManagementEditRequest?
    ): Response<String?>?

    @POST(VIEW_STATEMENTS)
    suspend fun viewStatements(@Body request: ViewStatementsReqModel): Response<EmptyApiResponse?>?

    @GET(ACCOUNT_STATEMENT)
    suspend fun getAccountStatements(): Response<List<StatementListModel?>?>?

    @POST(BALANCE_TRANSFER)
    suspend fun balanceTransfer(
        @Body request: BalanceTransferRequest?
    ): Response<BalanceTransferResponse?>?

    @POST(SEARCH_PROCESS_PARAMETERS)
    suspend fun searchProcessParameters(
        @Body request: SearchProcessParamsModelReq?,
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<SearchProcessParamsModelResp?>?

    @POST(LOGIN_WITH_REFERENCE_AND_PLATE_NUMBER)
    suspend fun loginWithRefAndPlateNumber(
        @Body request: CheckPaidCrossingsRequest?,
        @Query("returnReferenceInformation") value: Boolean? = true,
    ): Response<LoginWithPlateAndReferenceNumberResponseModel?>?

    @POST(GET_TOLL_TRANSACTIONS)
    suspend fun getTollTransactions(
        @Body request: UsedTollTransactionsRequest?
    ): Response<List<UsedTollTransactionResponse?>?>?


    @POST(USER_NAME_AVAILABILITY_CHECK)
    suspend fun userNameAvailabilityCheck(
        @Body request: UserNameCheckReq?,
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<Boolean?>?

    @GET(COUNTRIES_LIST)
    suspend fun getCountriesList(
        @Query("agencyId") agencyId: String? = AGENCY_ID
    ): Response<List<CountriesModel?>?>?

    @GET(COUNTRY_CODES)
    suspend fun getCountryCodes(
        @Query("agencyId") agencyId: String? = AGENCY_ID,
        @Query("codeType") request: String? = PHONE_COUNTRY_CODE
    ): Response<List<CountryCodes?>?>?

    @POST(PUSH_NOTIFICATION)
    suspend fun allowPushNotification(
        @Body request: PushNotificationRequest
    ): Response<EmptyApiResponse?>?


    @GET(CATEGORY_LIST)
    suspend fun getCategoryList(): Response<List<CaseCategoriesModel?>?>?


    @GET(SUB_CATEGORY_LIST)
    suspend fun getSubCategory(
        @Path("Category") Category: String?
    ): Response<List<CaseCategoriesModel?>?>?

    @POST(RAISE_ENQUIRY)
    suspend fun raiseEnquiry(
        @Body request: EnquiryRequest
    ): Response<EnquiryResponseModel?>?

    @POST(GET_ACCOUNT_SR_LIST)
    suspend fun GET_ACCOUNT_SR_LIST(
        @Body body: JSONObject
    ): Response<EnquiryListResponseModel?>?

    @POST(GET_GENERAL_ACCOUNT_SR_DETAILS)
    suspend fun GET_GENERAL_ACCOUNT_SR_DETAILS(
        @Body request: EnquiryStatusRequest
    ): Response<EnquiryListResponseModel?>?

    @GET(LRDS_VERIFICATION_SR)
    suspend fun getLrdsStatus(
    ): Response<LRDSResponse?>?


}