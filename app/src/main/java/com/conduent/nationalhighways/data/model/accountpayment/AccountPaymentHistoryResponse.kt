package com.conduent.nationalhighways.data.model.accountpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class AccountPaymentHistoryResponse(
    val transactionList: TransactionList?,
    val statusCode: String?,
    val message: String?
    )

class TransactionList (
 var transaction: MutableList<TransactionData> = mutableListOf(),
 val count: String?
)

@Parcelize
class TransactionData (
 var showDateHeader:Boolean=false,
 val postingDate: String?="",
 val transactionDate: String?="",
 val tagOrPlateNumber: String?="",
 val agency: String?="",
 val activity: String?="",
 val entryTime: String?="",
 val entryPlaza: String?="",
 val entryLane: String?="",
 val exitTime: String?="",
 val exitPlaza: String?="",
 val exitLane: String?="",
 val vehicleTypeCode: String?="",
 val amount: String?="",
 val prepaid: String?="",
 val planOrRate: String?="",
 val fareType: String?="",
 val balance: String?="",
 val index: String?="",
 val exitPlazaName: String?="",
 val transactionNumber: String?="",
 val entryDirection: String?="",
 val exitDirection: String?="",
 val plateNumber: String?="",
 val rebillPaymentType: String?="",
 val tranSettleStatus: String?="",
 val paymentSource: String?=""
 ) : Parcelable

