package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateAccountVehicleListModel(
    var vehicle: MutableList<CreateAccountVehicleModel?>?
) : Parcelable