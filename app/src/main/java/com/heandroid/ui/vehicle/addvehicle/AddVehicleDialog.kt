package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.heandroid.R
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.DialogAddVehicleBinding
import com.heandroid.ui.base.BaseDialog
import com.heandroid.utils.DateUtils
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.openKeyboardForced
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.onTextChanged
import kotlinx.coroutines.launch

class AddVehicleDialog : BaseDialog<DialogAddVehicleBinding>() {

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogAddVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
        setBtnDisabled()
        binding.addVrmInput.onTextChanged {
            if (binding.addVrmInput.text.toString().isNotEmpty()) {
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
        binding.ivClose.setOnClickListener {
            binding.addVrmInput.hideKeyboard()
            dismiss()
        }
        binding.addVehicleBtn.setOnClickListener {
            binding.addVrmInput.hideKeyboard()
            var country = "UK"
            if (binding.addVrmInput.text.toString().isNotEmpty()) {
                country = if (!binding.switchView.isChecked) {
                    "Non-UK"
                } else {
                    "UK"
                }
                val plateInfoResp = PlateInfoResponse(
                    binding.addVrmInput.text.toString().trim(),
                    country, "", "",
                    "", "", ""
                )
                val vehicleInfoResp =
                    VehicleInfoResponse(
                        "", "", "",
                        "", "", "",
                        "", "", DateUtils.convertDateFormat(DateUtils.currentDate(),0)
                    )

                val mVehicleResponse =
                    VehicleResponse(plateInfoResp, plateInfoResp, vehicleInfoResp)
                mListener?.onAddClick(mVehicleResponse)
            } else {
                requireContext().showToast(   "Please enter your vehicle number")
            }
        }

        binding.cancelBtn.setOnClickListener {
            binding.addVrmInput.hideKeyboard()
            dismiss()
        }
    }

    override fun observer() {
        lifecycleScope.launch {
//            observe(viewModel.logout,::handleLogout)
        }
    }

    companion object {
        const val TAG = "AddVehicle"
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        private var mListener: AddVehicleListener? = null

        fun newInstance(
            title: String,
            subTitle: String?,
            listener: AddVehicleListener
        ): AddVehicleDialog {
            val args = Bundle()
            mListener = listener
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            val fragment = AddVehicleDialog()
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