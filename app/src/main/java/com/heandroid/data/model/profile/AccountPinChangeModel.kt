package com.heandroid.data.model.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountPinChangeModel(
    val pin : String?
) : Parcelable