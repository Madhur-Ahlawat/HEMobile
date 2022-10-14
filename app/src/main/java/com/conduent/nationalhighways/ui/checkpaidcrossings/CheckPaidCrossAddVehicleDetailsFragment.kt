package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.RetrievePlateInfoDetails
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.databinding.FragmentAddVehicleDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckPaidCrossAddVehicleDetailsFragment : BaseFragment<FragmentAddVehicleDetailsBinding>() {

    private var exists: Boolean? = null
    private var vrm = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {}

    override fun init() {
        binding.model = false
        val country =
            arguments?.getString(Constants.COUNTRY_TYPE)
        vrm = arguments?.getString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED).toString()
        exists = arguments?.getBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, false)

        binding.title.text = getString(
            R.string.vehicle_reg_num,
            vrm
        )
        binding.subTitle.text = getString(
            R.string.country_reg,
            country
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
            if (binding.makeInputEditText.text.toString().trim().isNotEmpty()
                && binding.modelInputEditText.text.toString().trim().isNotEmpty()
                && binding.colorInputEditText.text.toString().trim().isNotEmpty()
            ) {

                val mPlateInfo = RetrievePlateInfoDetails(
                    vrm,
                    "",
                    binding.makeInputEditText.text.toString().trim(),
                    binding.modelInputEditText.text.toString().trim(),
                    binding.colorInputEditText.text.toString().trim()
                )
                val mVrmDetailsDvla = VehicleInfoDetails(mPlateInfo)
                arguments?.putParcelable(
                    Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS,
                    mVrmDetailsDvla
                )

                findNavController().navigate(R.id.addVehicleClassesFragment, arguments)
            }
        }
    }

    private fun checkButton() {
        binding.model = (binding.makeInputEditText.text.toString().trim().isNotEmpty()
                && binding.modelInputEditText.text.toString().trim().isNotEmpty()
                && binding.colorInputEditText.text.toString().trim().isNotEmpty())
    }

}