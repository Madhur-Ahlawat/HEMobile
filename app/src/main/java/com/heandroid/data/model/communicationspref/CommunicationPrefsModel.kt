package com.heandroid.data.model.communicationspref

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CommunicationPrefsModel(val id:String?,val category: String?,val oneMandatory:String?,
                                   val defEmail:String?,val mailFlag:String?,val defSms:String?,val smsFlag:String?,
                                   val defVoice:String?,val voiceFlag:String?,val defPushNot:String?,val pushNotFlag:String?):Parcelable
