package com.conduent.nationalhighways.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * BroadcastReceiver to wait for SMS messages. This can be registered either
 * in the AndroidManifest or at runtime.  Should filter Intents on
 * SmsRetriever.SMS_RETRIEVED_ACTION.
 */
class OTPReceiver : BroadcastReceiver() {
    private var otpReceiveListener: OTPReceiveListener? = null

    fun init(otpReceiveListener: OTPReceiveListener?) {
        this.otpReceiveListener = otpReceiveListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("OTPReceiver", "onReceive: action "+intent?.action )
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras: Bundle? = intent!!.extras
            val status: Status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            Log.e("OTPReceiver", "onReceive: statusCode "+status.statusCode )

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    var msg: String? = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                    // extract the 6-digit code from the SMS
                    val smsCode = msg?.let { "[0-9]{6}".toRegex().find(it) }
                    Log.e("OTPReceiver", "onReceive: smsCode "+smsCode )
                    smsCode?.value?.let { otpReceiveListener?.onOTPReceived(it) }
                }
                CommonStatusCodes.TIMEOUT -> {
                    Log.e("OTPReceiver", "onReceive: smsCode timeout " )
                    otpReceiveListener?.onOTPTimeOut()
                }
            }
        }
    }

    interface OTPReceiveListener {
        fun onOTPReceived(otp: String?)
        fun onOTPTimeOut()
    }
}

fun startSMSRetrieverClient(context: Context) {
    val client: SmsRetrieverClient = SmsRetriever.getClient(context)
    val task = client.startSmsRetriever()
    task.addOnSuccessListener { aVoid ->
        Log.e("Atiar OTP Receiver", "startSMSRetrieverClient addOnSuccessListener")
    }
    task.addOnFailureListener { e ->
        Log.e("Atiar OTP Receiver", "startSMSRetrieverClient addOnFailureListener" + e.stackTrace)
    }
}