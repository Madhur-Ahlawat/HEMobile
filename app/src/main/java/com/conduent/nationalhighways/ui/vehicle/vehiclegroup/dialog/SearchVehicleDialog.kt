package com.conduent.nationalhighways.ui.vehicle.vehiclegroup.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.DialogSearchVehicleBinding
import com.conduent.nationalhighways.ui.base.BaseDialog
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.openKeyboardForced
import com.conduent.nationalhighways.utils.extn.showToast
import com.conduent.nationalhighways.utils.onTextChanged

class SearchVehicleDialog : BaseDialog<DialogSearchVehicleBinding>() {

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogSearchVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
        setBtnDisabled()
        binding.addVrmInput.onTextChanged {
            if (binding.addVrmInput.getText().toString().trim().isNotEmpty()) {
                setBtnActivated()
            } else {
                setBtnDisabled()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
        binding.addVrmInput.post {
            binding.addVrmInput.openKeyboardForced()
        }
    }

    override fun initCtrl() {
        binding.ivClear.setOnClickListener {

        }
        binding.applyBtn.setOnClickListener {
            binding.addVrmInput.hideKeyboard()
            if (binding.addVrmInput.getText().toString().trim().isNotEmpty()) {
                dismiss()
                mListener?.onClick(binding.addVrmInput.getText().toString().trim())
            } else {
                requireContext().showToast("Please enter your vehicle number")
            }
        }

        binding.cancelBtn.setOnClickListener {
            binding.addVrmInput.hideKeyboard()
            dismiss()
        }
    }

    override fun observer() {}

    companion object {
        const val TAG = "AddVehicle"
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        private var mListener: SearchVehicleListener? = null

        fun newInstance(
            title: String,
            subTitle: String,
            listener: SearchVehicleListener
        ): SearchVehicleDialog {
            val args = Bundle()
            mListener = listener
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            val fragment = SearchVehicleDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private fun setBtnActivated() {
        binding.model = true
    }

    private fun setBtnDisabled() {
        binding.model = false
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.dialog_background
            )
        )
    }
}