package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentAddVehicleDetailsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import java.nio.BufferUnderflowException

@AndroidEntryPoint
class AddVehicleDetailsFragment : BaseFragment<FragmentAddVehicleDetailsBinding>() {

    private var mVehicleDetails: VehicleResponse? = null
    private var isFromPaymentScreen = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {
    }

    private var mScreeType = 0
    override fun init() {
        binding.model = false
        mVehicleDetails = arguments?.getParcelable(Constants.DATA) as? VehicleResponse?
        isFromPaymentScreen = arguments?.getBoolean(Constants.PAYMENT_PAGE, false) == true

        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        binding.title.text = getString(
            R.string.vehicle_reg_num,
            mVehicleDetails?.plateInfo?.number
        )//"Vehicle registration number: ${mVehicleDetails?.plateInfo?.number}"
        binding.subTitle.text = getString(
            R.string.country_reg,
            mVehicleDetails?.plateInfo?.country
        )//"Country of registration ${mVehicleDetails?.plateInfo?.country}"
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

                mVehicleDetails?.vehicleInfo?.color =
                    binding.colorInputEditText.text.toString().trim()
                mVehicleDetails?.vehicleInfo?.make =
                    binding.makeInputEditText.text.toString().trim()
                mVehicleDetails?.vehicleInfo?.model =
                    binding.modelInputEditText.text.toString().trim()

                val bundle = Bundle().apply {

                    putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                    putParcelable(Constants.DATA, mVehicleDetails)
                    putBoolean(Constants.PAYMENT_PAGE, isFromPaymentScreen)
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
        binding.model = true
    }

    private fun setBtnDisabled() {
        binding.model = false
    }

}