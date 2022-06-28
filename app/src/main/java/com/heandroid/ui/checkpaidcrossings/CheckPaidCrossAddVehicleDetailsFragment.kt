package com.heandroid.ui.checkpaidcrossings

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
import com.heandroid.utils.common.Logg
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import java.nio.BufferUnderflowException

@AndroidEntryPoint
class CheckPaidCrossAddVehicleDetailsFragment : BaseFragment<FragmentAddVehicleDetailsBinding>() {

    private var mVehicleDetails: VehicleResponse? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {
    }

    override fun init() {
        binding.model = false


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

                findNavController().navigate(R.id.addVehicleClassesFragment, arguments)

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