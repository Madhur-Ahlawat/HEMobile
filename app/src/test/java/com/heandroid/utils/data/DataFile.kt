package com.heandroid.utils.data

import com.heandroid.data.model.account.*
import com.heandroid.data.model.accountpayment.TransactionData
import com.heandroid.data.model.address.DataAddress
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.data.model.checkpaidcrossings.UsedTollTransactionResponse
import com.heandroid.data.model.contactdartcharge.ServiceRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryItem
import com.heandroid.data.model.makeoneofpayment.*
import com.heandroid.data.model.manualtopup.PaymentWithExistingCardModel
import com.heandroid.data.model.manualtopup.PaymentWithNewCardModel
import com.heandroid.data.model.nominatedcontacts.SecondaryAccountData
import com.heandroid.data.model.notification.AlertMessage

object DataFile {

    fun getSecondaryAccountData(): SecondaryAccountData {
        return SecondaryAccountData(
            "", "", "", "",
            "", "", "", "", ""
        )
    }

    fun getUsedTollTransaction(id: String = "1234"): UsedTollTransactionResponse {
        return UsedTollTransactionResponse(
            id, "", "", "",
            "", "", "", "", "", "", "",
            "", "", "", "", "", "", "",
            "", "", "", "", "", "", "",
            "", "", "", "", "", "", "",
            "", "", "", "", "", "", "",
            "", "", "", "", ""
        )
    }

    fun getDataAddress(): DataAddress {
        return DataAddress(
            "", "", "", "",
            "", "", ""
        )
    }

    fun getOneOfPaymentModelRequest(): OneOfPaymentModelRequest {
        val list = FtVehicleList(arrayListOf())
        val paymentInfo = PaymentTypeInfo(
            "", "", "", "",
            "", "", "", "", "",
            "", "", "", "", "", "", "", ""
        )
        return OneOfPaymentModelRequest(list, paymentInfo)
    }

    fun getCrossingDetailsResponse() = CrossingDetailsModelsResponse(
        "", "", "", "",
        "", "", "", "", "",
        "", "", ""
    )

    fun getAccountResponse(): AccountResponse {
        val accountInformation = AccountInformation(
            "ACTIVE", "100313942", "personal pre-pay account", 0,
            "", "", "", "",
            "", "", "", "",
            "", "", "", "", "",
            "", "", "",
            "", "", "", "",
            "", "", "", "",
            "auto top-up", "", "", "",
            "", "", null, "",
            null, "", "", "", "", "",
            ""
        )
        val financialInformation = FinancialInformation(
            "", "", "", 0.0,
            0.0, 0.0, "", "", "",
            "", "", 0,
            0, "", "", null
        )
        val replenishmentInformation = ReplenishmentInformation(
            "", 0, "", 0.0, "",
            "", "", "2500",
            "", "", "", "",
            ""
        )
        val personalInformation = PersonalInformation(
            "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", "", "", "", "",
            "", "", "",
            "",
            vrAlexaTc = false,
            vrGoogleTc = false,
            vrSmsTc = false,
            pushNotifications = false,
            isNixeAddress = "",
            reEmailAddress = "",
            secondaryEmailAddress = "",
            emailCount = "",
            pemailUniqueCode = "",
            primaryEmailStatus = "",
            phoneLandLine = "",
            phoneNumber = "",
            stateType = "",
            countryType = null,
            language = "",
            password = "",
            securityPin = "",
            challengeQuestion = "",
            challengeAnswer = "",
            challengeQuestionTwo = "",
            challengeAnswerTwo = "",
            challengeQuestionThree = "",
            challengeAnswerThree = "",
            postLoginChallengeQuestionChange = "",
            accountDetail = "",
            accountNumber = "",
            customerName = "",
            zipcode = "",
            phoneDay = "",
            tempPwdValid = "",
            epseligible = "",
            ""
        )
        return AccountResponse(
            accountInformation,
            financialInformation,
            replenishmentInformation,
            personalInformation
        )

    }

    fun getServiceRequest(id: String): ServiceRequest {
        return ServiceRequest(
            id, "", "",
            "", "", "", null,
            null, "", ""
        )
    }

    fun getPaymentWithNewCardRequest(): PaymentWithNewCardModel {
        return PaymentWithNewCardModel(
            "", "", "",
            "", "", "", "",
            "", "", "",
            "", "", "", "",
            "", "", "",
            "", "", "", "",
            "", ""
        )
    }

    fun getPaymentWithExistingCardRequest(): PaymentWithExistingCardModel {
        return PaymentWithExistingCardModel(
            "", "", "",
            "", "", "", "",
            "", "", "",
            "", "", "", "",
            "", "", "",
            "", "", "", ""
        )
    }

    fun getPaymentHistoryTransactionData(): TransactionData {
        return TransactionData(
            "", "", "",
            "", "", "", "",
            "", "", "",
            "", "", "100", "",
            "", "", "500",
            "", "exitPlazaName", "12345678", "",
            "", "Lo62 NRO", "VISA", ""
        )
    }

    fun getLoginResponse(): LoginResponse {
        return LoginResponse(
            0, "", "",
            "", "", 0, "",
            0, 0, 0,
            "", "", "", "",
            false, 1, false,
            isPasswordExpired = false, Permission = "", require2FA = false
        )
    }

    fun getCrossingHistoryItem(number: String, status: String = "Y"): CrossingHistoryItem? {
        return CrossingHistoryItem(
            "",
            "01012022",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            status,
            "",
            "",
            "",
            "",
            "",
            "",
            number,
            "",
            "N",
            "L062 1234",
            "",
            "",
            ""
        )
    }

    fun getAlertMessage() = AlertMessage(
        1, "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        "",
        isRead = false,
        iSel = false,
        isSelectListItem = false
    )

    fun getCreateAccountRequestModel(): CreateAccountRequestModel {
        return CreateAccountRequestModel(
            0L, 0L, "",
            "", "", "", "",
            "", "", "",
            "", "", "", "",
            "", "", "",
            "", "", "", null,
            "", "", "",
            null, null, "", "",
            "", "", "",
            "", "", "", "",
            "", "", "",
            "", "", "",
            "", "", false,
            "", "10", "100", ""
        )
    }
}