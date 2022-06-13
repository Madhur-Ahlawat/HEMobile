package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountStatementResponse(
    var statementList: MutableList<StatementListModel?>?
) : Parcelable

@Parcelize
data class StatementListModel(
    var pdfName: String?,
    var period: String?,
    var statementDate: String?,
    var statementAvailable: String?
) : Parcelable

