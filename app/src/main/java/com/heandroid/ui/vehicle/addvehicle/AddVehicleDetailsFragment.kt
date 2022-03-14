package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentAddVehicleDetailsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleDetailsFragment : BaseFragment<FragmentAddVehicleDetailsBinding>() {

    private lateinit var mVehicleDetails: VehicleResponse

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {

    }

    override fun init() {
        mVehicleDetails = arguments?.getSerializable(Constants.DATA) as VehicleResponse
        binding.title.text = "Vehicle registration number: ${mVehicleDetails.plateInfo.number}"
        binding.subTitle.text = "Country of registration ${mVehicleDetails.plateInfo.country}"
        setBtnDisabled()
    }

    override fun initCtrl() {
        binding.makeInputEditText.onTextChanged {
            checkButton()
        }
        binding.modelInputEditText.onTextChanged {
            checkButton()
        }
        binding.colorInputEditText.onTextChanged {
            checkButton()
        }

        binding.nextBtn.setOnClickListener {
            if (binding.makeInputEditText.text.toString().trim().isNotEmpty()
                && binding.modelInputEditText.text.toString().trim().isNotEmpty()
                && binding.colorInputEditText.text.toString().trim().isNotEmpty()
            ) {
                mVehicleDetails.vehicleInfo.color = binding.colorInputEditText.text.toString().trim()
                mVehicleDetails.vehicleInfo.make = binding.makeInputEditText.text.toString().trim()
                mVehicleDetails.vehicleInfo.model = binding.modelInputEditText.text.toString().trim()

                val bundle = Bundle().apply {
                    putSerializable(Constants.DATA, mVehicleDetails)
                    putBoolean(Constants.PAYMENT_PAGE, true)
                }
                findNavController().navigate(R.id.addVehicleClassesFragment, bundle)
            }
        }
    }

    private fun checkButton() {
        if (binding.makeInputEditText.text.toString().trim().isNotEmpty()
            && binding.modelInputEditText.text.toString().trim().isNotEmpty()
            && binding.colorInputEditText.text.toString().trim().isNotEmpty()
        ) {
            setBtnActivated()
        } else {
            setBtnDisabled()
        }

    }

    private fun setBtnActivated() {
        binding.nextBtn.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.btn_color
                )
            )
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            isClickable = true
        }
    }

    private fun setBtnDisabled() {
        binding.nextBtn.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_C9C9C9
                )
            )
            setTextColor(ContextCompat.getColor(requireContext(), R.color.color_7D7D7D))
            isClickable = false
        }
    }

}