package com.conduent.nationalhighways.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentAddVehicleDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.DATA
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddVehicleDetailsFragment : BaseFragment<FragmentAddVehicleDetailsBinding>() {

    private var mScreeType = 0
    private var mVehicleDetails: VehicleResponse? = null

    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {}

    override fun init() {
        binding.model = false
        mVehicleDetails = arguments?.getParcelable(DATA) as? VehicleResponse?

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

        AdobeAnalytics.setScreenTrack(
            "one of  payment:vehicle details manual entry",
            "vehicle",
            "english",
            "one of payment",
            "home",
            "one of  payment:vehicle details manual entry",
            sessionManager.getLoggedInUser()
        )
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

            AdobeAnalytics.setActionTrack(
                "next",
                "one of  payment:vehicle details manual entry",
                "vehicle",
                "english",
                "one of payment",
                "home",
                sessionManager.getLoggedInUser()
            )

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
                    putParcelable(DATA, mVehicleDetails)
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