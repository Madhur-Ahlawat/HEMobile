package com.heandroid.data.model.communicationspref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommunicationPrefsRequestModelList(
    val id: String?,
    val category: String?,
    val oneMandatory: String?,
    val defEmail: String?,
    val emailFlag: String?,
    val mailFlag: String?,
    val defSms: String?,
    val smsFlag: String?,
    val defVoice: String?,
    val voiceFlag: String?,
    val pushNotFlag: String?
):Parcelable
