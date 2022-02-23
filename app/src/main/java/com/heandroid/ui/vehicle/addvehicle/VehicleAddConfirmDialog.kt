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
import com.heandroid.databinding.VehicleAddConformBinding
import com.heandroid.ui.vehicle.AddVehicleListener

class VehicleAddConfirmDialog : DialogFragment() {


    private lateinit var dataBinding: VehicleAddConformBinding
    private lateinit var mVehicleDetails: VehicleResponse

    companion   object {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.vehicle_add_conform, container, false)
        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        mVehicleDetails = arguments?.getSerializable(KEY_TITLE) as VehicleResponse
        setupView(view)
        setupClickListeners(view)
    }

    private fun setupClickListeners(view: View) {

        dataBinding.subTitle.text = mVehicleDetails.plateInfo.number
        dataBinding.yesBtn.setOnClickListener {
//            dismiss()
            mListener?.onAddClick(mVehicleDetails)
        }

        dataBinding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setupView(view: View) {
        setBtnActivated()

    }

    private fun setBtnActivated() {
        dataBinding.yesBtn.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.btn_color
            )
        )

        dataBinding.yesBtn.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        dataBinding.yesBtn.isEnabled = true
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