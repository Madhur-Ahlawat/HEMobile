package com.heandroid.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.heandroid.R
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.DialogAddVehicleBinding
import com.heandroid.databinding.DialogDeleteVehicleGroupBinding
import com.heandroid.ui.base.BaseDialog
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.openKeyboardForced
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.onTextChanged
import kotlinx.coroutines.launch

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