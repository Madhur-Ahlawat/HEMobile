package com.heandroid.ui.account.creation.step5.businessaccount.dialog

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
        binding.subTitle.text = keySubTitle
        binding.title.text = keyTitle
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
        var keyTitle = "KEY_TITLE"
        var keySubTitle = "KEY_SUBTITLE"

        fun newInstance(title: String, subTitle: String,  listener: AddBusinessVehicleListener): BusinessAddConfirmDialog {

            val args = Bundle()
            mListener = listener
            val fragment = BusinessAddConfirmDialog()
            keyTitle = title
            keySubTitle = subTitle
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