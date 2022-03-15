package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateAccountVehicleListModel (val vehicle: MutableList<CreateAccountVehicleModel?>?) : Parcelable