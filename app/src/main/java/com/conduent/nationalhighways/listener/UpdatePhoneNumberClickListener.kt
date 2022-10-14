package com.conduent.nationalhighways.listener

import com.conduent.nationalhighways.ui.account.communication.UpdatePhoneNumberDialog

interface UpdatePhoneNumberClickListener {

    fun onSaveClickedListener(number: String , d: UpdatePhoneNumberDialog)
    fun onCancelClickedListener(d: UpdatePhoneNumberDialog)
    fun onCrossImageClickedListener(d: UpdatePhoneNumberDialog)
}