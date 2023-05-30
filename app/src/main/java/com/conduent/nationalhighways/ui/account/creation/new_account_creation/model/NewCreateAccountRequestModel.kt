package com.conduent.nationalhighways.ui.account.creation.new_account_creation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewCreateAccountRequestModel(
     var referenceId: Long?,
     var communicationTextMessage:String,
     var isTwoStepVerificationRequired: Boolean


    ): Parcelable