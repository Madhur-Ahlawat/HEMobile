package com.heandroid.ui.vehicle.addvehicle.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.VehicleAddConformBinding
import com.heandroid.ui.base.BaseDialog
import com.heandroid.utils.common.Constants

class VehicleAddConfirmDialog : BaseDialog<VehicleAddConformBinding>() {

    private var mVehicleDetails: VehicleResponse? = null

    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = VehicleAddConformBinding.inflate(inflater, container, false)

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
        arguments?.getParcelable<VehicleResponse>(Constants.DATA)?.let {
            mVehicleDetails = it
        }
        binding.subTitle.text = mVehicleDetails?.plateInfo?.number
    }

    override fun initCtrl() {
        binding.ivClose.setOnClickListener{
            dismiss()
        }
        binding.yesBtn.setOnClickListener {
            dismiss()
            mVehicleDetails?.let { it1 -> mListener?.onAddClick(it1) }
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun observer() { }

    companion object {
        const val TAG = "VehicleAddConfirm"
        private var mListener: AddVehicleListener? = null

        fun newInstance(
            vehicleDetails: VehicleResponse?,
            listener: AddVehicleListener
        ): VehicleAddConfirmDialog {
            val args = Bundle()
            mListener = listener
            args.putParcelable(Constants.DATA, vehicleDetails)
            val fragment = VehicleAddConfirmDialog()
            fragment.arguments = args
            return fragment
        }

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