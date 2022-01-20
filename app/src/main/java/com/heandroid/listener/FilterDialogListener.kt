package com.heandroid.listener

import androidx.fragment.app.DialogFragment
import com.heandroid.dialog.UpdatePhoneNumberDialog

interface FilterDialogListener {

    fun onApplyCLickListener(cat:String)
    fun onCancelClickedListener()
}