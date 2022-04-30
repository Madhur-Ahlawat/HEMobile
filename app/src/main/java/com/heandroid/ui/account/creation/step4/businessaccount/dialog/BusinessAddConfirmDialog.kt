package com.heandroid.ui.account.creation.step4.businessaccount.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.heandroid.R
import com.heandroid.databinding.VehicleAddConformBinding
import com.heandroid.ui.base.BaseDialog

class BusinessAddConfirmDialog: BaseDialog<VehicleAddConformBinding>() {
    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )=  VehicleAddConformBinding.inflate(inflater, container, false)

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
      //  binding.subTitle.text = resources.getString(R.string.str_do_you_want_the_below)
    }

    override fun initCtrl() {
        binding.ivClose.setOnClickListener{
            dismiss()
        }

        binding.yesBtn.setOnClickListener {
            dismiss()
            mListener?.onAddClick()
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

    }

    override fun observer() {
    }

    companion object {

        private var mListener: AddBusinessVehicleListener? = null

        fun newInstance(listener: AddBusinessVehicleListener): BusinessAddConfirmDialog {
            val args = Bundle()
            mListener = listener
            val fragment = BusinessAddConfirmDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background))
    }
}