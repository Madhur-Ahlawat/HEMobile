package com.conduent.nationalhighways.data.model.checkpaidcrossings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckPaidCrossingsRequest(
    val referenceNumber: String?,
    val plateNumber: String?
) : Parcelable

@Parcelize
data class UsedTollTransactionsRequest(
    val startIndex: String? = "0",
    val transactionType: String? = "TOLL",
    val count: String? = "5",
    val sortColumn: String? = "POSTED_DATE"
) : Parcelable


@Parcelize
data class UsedTollTransactionResponse(
    val accountid: String?,
    val postingDate: String?,
    val deviceNo: String?,
    val agencyName: String?,
    val agencyId: String?,
    val entryDate: String?,
    val entryDateDummy: String?,
    val entryTime: String?,
    val entryplazaName: String?,
    val entrylaneName: String?,
    val txDate: String?,
    val txTime: String?,
    val plazaName: String?,
    val laneName: String?,
    val vehicleClass: String?,
    val amount: String?,
    val plantype: String?,
    val activity: String?,
    val exitTime: String?,
    val exitDate: String?,
    val fareType: String?,
    val balance: String?,
    val lookupKey: String?,
    val plazaGroup: String?,
    val vehicleSpeed: String?,
    val occupancy: String?,
    val entryPlazaName: String?,
    val exitPlazaName: String?,
    val editable: String?,
    val editAllowed: String?,
    val editAllowedCheck: String?,
    val isDisputed: String?,
    val plazaGroupOptions: String?,
    val etcAccntId: String?,
    val laneTxId: String?,
    val plazaAgencyId: String?,
    val txSubType: String?,
    val txTypeInd: String?,
    val selectedPlazaId: String?,
    val exitPlazaID: String?,
    val vehicleClassName: String?,
    val agencyClassName: String?,
    val plateNumber: String?,
    val exitDirection: String?
) : Parcelable

@Parcelize
data class BalanceTransferRequest(
    val accountNumber: String?,
    val plateNumber: String?,
    val plateCountry: String?,
    val transferInfo: TransferInfo?) : Parcelable


@Parcelize
data class TransferInfo(
    val tripCount: String?,
    val plateNumber: String?,
    val plateState: String?,
    val plateCountry: String?,
    val vehicleClass: String?,
    val vehicleMake: String?,
    val vehicleModel: String?,
    val vehicleYear: String?
) : Parcelable


data class BalanceTransferResponse(var success: Boolean?)


@Parcelize
data class CheckPaidCrossingsOptionsModel(
    var ref: String?,
    var vrm: String?,
    var enable: Boolean?
) : Parcelable

@Parcelize
data class EnterVrmOptionsModel(
    var vrm: String?,
    var enable: Boolean?
) : Parcelable

@Parcelize
data class UnUsedChargesModel(
    val trip: Int?,
    val vrm: String?,
    val expiryDate: String? = "24 June 2023"
) : Parcelable

@Parcelize
data class UsedChargesModel(
    val crossingId: String?,
    val vrm: String?,
    val crossingDate: String? = "1 April 2023",
    val time: String?,
    val direction: String?
) : Parcelable