package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateAccountVehicleListModel(
    var vehicle: MutableList<CreateAccountVehicleModel?>?
) : Parcelable