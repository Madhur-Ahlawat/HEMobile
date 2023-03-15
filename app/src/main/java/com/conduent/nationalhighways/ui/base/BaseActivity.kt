package com.conduent.nationalhighways.ui.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.CustomDialogBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener

abstract class BaseActivity<T> : AppCompatActivity() {

    abstract fun observeViewModel()
    protected abstract fun initViewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        observeViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun displayMessage(
        fTitle: String?,
        message: String,
        positiveBtnTxt: String,
        negativeBtnTxt: String,
        pListener: DialogPositiveBtnListener?,
        nListener: DialogNegativeBtnListener?
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)

        val alertDialog = builder.create()

        val customTitleView = LayoutInflater.from(this).inflate(
            R.layout.alert_dialog_custom_title_layout, LinearLayout(this)
        )

        val titleText = customTitleView.findViewById<TextView>(R.id.alert_dialog_title_text)

        titleText.text = fTitle?.let { it }


        alertDialog.setCustomTitle(customTitleView)
        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            positiveBtnTxt
        ) { dialog, which -> pListener?.positiveBtnClick(dialog) }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            negativeBtnTxt
        ) { dialog, which -> nListener?.negativeBtnClick(dialog) }
        alertDialog.setOnShowListener {
            val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
            val nbutton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            nbutton.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
        }

        alertDialog.show()
    }

    fun displayCustomMessage(
        fTitle: String?,
        message: String,
        positiveBtnTxt: String,
        negativeBtnTxt: String,
        pListener: DialogPositiveBtnListener?,
        nListener: DialogNegativeBtnListener?
    ) {

        val dialog = Dialog(this)
        dialog.setCancelable(false)

        val binding: CustomDialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(this))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(binding.root)

        binding.title.text = fTitle
        binding.message.text = message
        binding.cancelBtn.text = negativeBtnTxt
        binding.okBtn.text = positiveBtnTxt
        binding.cancelBtn.setOnClickListener {
            nListener?.negativeBtnClick(dialog)
            dialog.dismiss()
        }

        binding.okBtn.setOnClickListener {
            pListener?.positiveBtnClick(dialog)
            dialog.dismiss()
        }
        dialog.show()


    }


}