package com.heandroid.listener

import androidx.fragment.app.DialogFragment
import com.heandroid.dialog.UpdatePhoneNumberDialog

interface UpdatePhoneNumberClickListener {

    fun onSaveClickedListener(number: String , d: UpdatePhoneNumberDialog)
    fun onCancelClickedListener(d: UpdatePhoneNumberDialog)
    fun onCrossImageClickedListener(d: UpdatePhoneNumberDialog)
}