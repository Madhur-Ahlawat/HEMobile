package com.conduent.nationalhighways.utils


import android.app.Activity
import android.util.Log
import com.conduent.apollo.security.cryptography.Hashing
import com.conduent.nationalhighways.receiver.SmsBroadcastReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import java.lang.Exception


object Utility {


    val REQ_USER_CONSENT = 100
    val TAG = "SMS_USER_CONSENT"
    fun getSHA256HashedValue(text: String): String {
        return Hashing.hash(text, Hashing.TYPE_SHA256)
    }
     fun fetchVerificationCode(message: String): String? {
         var six_digit_code = Regex("(\\d{6})").find(message)?.value
         var four_digit_code = Regex("(\\d{4})").find(message)?.value
        var returnableCode:String?=null
        if(!four_digit_code.isNullOrEmpty()) returnableCode=six_digit_code else returnableCode=four_digit_code
        return returnableCode
    }
    fun startSmsUserConsent(activity:Activity?) {
        SmsRetriever.getClient(activity!!).also {
            it.startSmsUserConsent(null)
                .addOnSuccessListener {
                    Log.d(TAG, "LISTENING_SUCCESS")
                }
                .addOnFailureListener {
                    Log.d(TAG, "LISTENING_FAILURE")
                }
        }
    }
    fun isNumber(s: String?): Boolean {
        try{
            return if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) }
        }
        catch(e:Exception){
            return false
        }
    }
    inline fun <T> ArrayDeque<T>.push(element: T) = addLast(element) // returns Unit

    inline fun <T> ArrayDeque<T>.pop() = removeLastOrNull()
}
