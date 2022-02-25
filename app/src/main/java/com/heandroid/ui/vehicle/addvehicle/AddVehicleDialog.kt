package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.heandroid.R
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.AddVehicleBinding
import com.heandroid.ui.base.BaseDialog
import com.heandroid.utils.common.Utils
import kotlinx.coroutines.launch

class AddVehicleDialog : BaseDialog<AddVehicleBinding>() {


    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        AddVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        setBtnNormal()
        binding.addVrmInput.addTextChangedListener {
            if (binding.addVrmInput.text.toString().isNotEmpty()) {
                setBtnActivated()
            } else {
                setBtnNormal()
            }
        }
    }

    override fun initCtrl() {
        binding.addVehicleBtn.setOnClickListener {
            var country = "UK"
            if (binding.addVrmInput.text.toString().isNotEmpty()) {
                country = if (!binding.switchView.isChecked) {
                    "Non-UK"
                } else {
                    "UK"
                }
                val plateInfoResp = PlateInfoResponse(
                    binding.addVrmInput.text.toString(),
                    country,
                    "",
                    "",
                    "",
                    "",
                    ""
                )
                val vehicleInfoResp =
                    VehicleInfoResponse("", "", "", "", "", "", "", "", Utils.currentDateAndTime())

                //todo we have to check for this
                val mVehicleResponse =
                    VehicleResponse(plateInfoResp, plateInfoResp, vehicleInfoResp)
                mListener?.onAddClick(mVehicleResponse)
                dismiss()

            } else {
                Snackbar.make(
                    binding.root,
                    "Please enter your vrn number",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.cancelBtn.setOnClickListener {
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
            subTitle: String,
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
        binding.addVehicleBtn.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.btn_color
            )
        )

        binding.addVehicleBtn.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.addVehicleBtn.isEnabled = true
    }

    private fun setBtnNormal() {
        binding.addVehicleBtn.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.hint_color
            )
        )
        binding.addVehicleBtn.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )

        binding.addVehicleBtn.isEnabled = false

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