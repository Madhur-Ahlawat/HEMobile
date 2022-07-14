package com.heandroid.data.model.contactdartcharge

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CaseProvideDetailsModel(
    val fName: String? = "",
    val lName: String? = "",
    val emailId: String? = "",
    val telephoneNo: String? = ""
) : Parcelable
