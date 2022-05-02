package com.heandroid.listener

import com.heandroid.ui.account.communication.UpdatePhoneNumberDialog

interface UpdatePhoneNumberClickListener {

    fun onSaveClickedListener(number: String , d: UpdatePhoneNumberDialog)
    fun onCancelClickedListener(d: UpdatePhoneNumberDialog)
    fun onCrossImageClickedListener(d: UpdatePhoneNumberDialog)
}