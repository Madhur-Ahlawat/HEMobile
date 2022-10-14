package com.conduent.nationalhighways.ui.vehicle.vehiclegroup.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.DialogDeleteVehicleGroupBinding
import com.conduent.nationalhighways.ui.base.BaseDialog

class DeleteVehicleGroupDialog : BaseDialog<DialogDeleteVehicleGroupBinding>() {

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogDeleteVehicleGroupBinding.inflate(inflater, container, false)

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun initCtrl() {
        binding.noBtn.setOnClickListener {
            dismiss()
        }
        binding.yesBtn.setOnClickListener {
            dismiss()
            mListener?.onDeleteClick()
        }
    }

    override fun observer() {}

    companion object {
        const val TAG = "DeleteVehicleGroup"
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        private var mListener: DeleteVehicleGroupListener? = null

        fun newInstance(
            title: String,
            subTitle: String,
            listener: DeleteVehicleGroupListener
        ): DeleteVehicleGroupDialog {
            val args = Bundle()
            mListener = listener
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            val fragment = DeleteVehicleGroupDialog()
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