package com.conduent.nationalhighways.data.model.account

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

