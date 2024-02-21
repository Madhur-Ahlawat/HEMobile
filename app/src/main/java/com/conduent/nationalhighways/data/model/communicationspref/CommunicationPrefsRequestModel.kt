package com.conduent.nationalhighways.data.model.communicationspref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommunicationPrefsRequestModel(
    var categoryList: ArrayList<CommunicationPrefsRequestModelList>
) : Parcelable
