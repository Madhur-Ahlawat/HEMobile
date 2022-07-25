package com.heandroid.utils.common

import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.heandroid.R
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.ui.loader.OnRetryClickListener
import com.heandroid.ui.loader.RetryDialog

object ErrorUtil {

    fun showError(view: View?, message: String?) {
        try {
            val dialog = ErrorDialog()
            val bundle = Bundle()
            bundle.putString(Constants.DATA, message)
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

            when (view?.context) {
                is AppCompatActivity -> dialog.show(
                    (view.context as AppCompatActivity).supportFragmentManager,
                    Constants.ERROR_DIALOG
                )
                is ContextWrapper -> dialog.show(
                    (((view.context as ContextWrapper).baseContext)
                            as AppCompatActivity).supportFragmentManager,
                    Constants.ERROR_DIALOG
                )
            }

        } catch (e: Exception) {

        }
    }

    fun showRetry(view: Fragment?) {
        try {
            val dialog = RetryDialog()
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
            dialog.listener = view as OnRetryClickListener

            view.childFragmentManager.let {
                dialog.show(
                    it,
                    Constants.RETRY_DIALOG
                )
            }
        } catch (e: Exception) {

        }
    }
}