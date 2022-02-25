package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.VehicleResponse
import com.heandroid.databinding.DialogLogoutBinding
import com.heandroid.databinding.VehicleAddConformBinding
import com.heandroid.ui.base.BaseDialog

class VehicleAddConfirmDialog : BaseDialog<VehicleAddConformBinding>() {

    private lateinit var mVehicleDetails: VehicleResponse

    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = VehicleAddConformBinding.inflate(inflater, container, false)

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
        mVehicleDetails = arguments?.getSerializable(KEY_TITLE) as VehicleResponse
        binding.subTitle.text = mVehicleDetails.plateInfo.number
        setBtnActivated()
    }

    override fun initCtrl() {
        binding.yesBtn.setOnClickListener {
//            dismiss()
            mListener?.onAddClick(mVehicleDetails)
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun observer() { }

    companion object {
        const val TAG = "VehicleAddConfirm"
        private const val KEY_TITLE = "list"
        private var mListener: AddVehicleListener? = null

        fun newInstance(
            vehicleDetails: VehicleResponse,
            listener: AddVehicleListener
        ): VehicleAddConfirmDialog {
            val args = Bundle()
            mListener = listener
            args.putSerializable(KEY_TITLE, vehicleDetails)
            val fragment = VehicleAddConfirmDialog()
            fragment.arguments = args
            return fragment
        }

    }

    private fun setBtnActivated() {
        binding.yesBtn.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.btn_color
            )
        )

        binding.yesBtn.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.yesBtn.isEnabled = true
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