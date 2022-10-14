package com.conduent.nationalhighways.data.model.communicationspref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommunicationPrefsRequestModel(
    val categoryList: ArrayList<CommunicationPrefsRequestModelList?>?
) : Parcelable
