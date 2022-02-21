package com.heandroid.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.heandroid.listener.AddVehicleListener
import com.heandroid.R
import com.heandroid.databinding.AddVehicleBinding
import com.heandroid.model.PlateInfoResponse
import com.heandroid.model.VehicleDetailsModel
import com.heandroid.model.VehicleInfoResponse
import com.heandroid.model.VehicleResponse
import com.heandroid.utils.Logg
import com.heandroid.utils.Utils

class AddVehicle : DialogFragment() {


    private lateinit var dataBinding: AddVehicleBinding

    companion object {

        const val TAG = "AddVehicle"

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        private var mListener: AddVehicleListener? = null

        fun newInstance(title: String, subTitle: String, listener: AddVehicleListener): AddVehicle {
            val args = Bundle()
            mListener = listener
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            val fragment = AddVehicle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.add_vehicle, container, false)
        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        setupView(view)
        setupClickListeners(view)
    }

    private fun setupClickListeners(view: View) {


        dataBinding.addVehicleBtn.setOnClickListener {
            var country = "UK"
            if (dataBinding.addVrmInput.text.toString().isNotEmpty()) {

                country = if (!dataBinding.switchView.isChecked) {
                    "Non UK"
                } else {
                    "UK"

                }

                val plateInfoResp = PlateInfoResponse(dataBinding.addVrmInput.text.toString(),country,"","","","","")
                val vehicleInfoResp = VehicleInfoResponse("","","","","","","","",Utils.currentDateAndTime())

                Logg.logging("AddVehicle"," date and time ${Utils.currentDateAndTime()}")
                //todo we have to check for this
                val mVehicleResponse = VehicleResponse(plateInfoResp , plateInfoResp, vehicleInfoResp)
                mListener?.onAddClick(mVehicleResponse)
                dismiss()

            } else {
                Snackbar.make(view, "Please enter your vrn number", Snackbar.LENGTH_LONG).show()
            }
        }

        dataBinding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setupView(view: View) {
        setBtnNormal()

        dataBinding.addVrmInput.addTextChangedListener {
            if (dataBinding.addVrmInput.text.toString().length > 0) {
                setBtnActivated()

            } else {
                setBtnNormal()

            }
        }
    }

    private fun setBtnActivated() {
        dataBinding.addVehicleBtn.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.btn_color
            )
        )

        dataBinding.addVehicleBtn.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        dataBinding.addVehicleBtn.isEnabled = true
    }

    private fun setBtnNormal() {
        dataBinding.addVehicleBtn.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.hint_color
            )
        )
        dataBinding.addVehicleBtn.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )

        dataBinding.addVehicleBtn.isEnabled = false

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.dialog_background))
    }


}