package com.conduent.nationalhighways.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver : BroadcastReceiver() {

    lateinit var smsBroadcastReceiverListener: SmsBroadcastReceiverListener

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("SmsBroadcastReceiver", "onReceive: action "+intent?.action )

        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {

            val extras = intent.extras
            val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            Log.e("SmsBroadcastReceiver", "onReceive: statusCode "+smsRetrieverStatus.statusCode )

            when (smsRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    if(extras.containsKey(SmsRetriever.EXTRA_CONSENT_INTENT)) {
                        extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT).also {
                            Log.e("SmsBroadcastReceiver", "onReceive: statusCode ->$it")
                            smsBroadcastReceiverListener.onSuccess(it)
                        }
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    Log.e("SmsBroadcastReceiver", "onReceive: statusCode timeout")
                    smsBroadcastReceiverListener.onFailure()
                }
            }
        }
    }

    interface SmsBroadcastReceiverListener {
        fun onSuccess(intent: Intent?)
        fun onFailure()
    }
}