package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.RetrievePlateInfoDetails
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
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
    private var exists: Boolean? = null
    private var vrm = ""


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {
    }

    override fun init() {
        binding.model = false

        val mData =
            arguments?.getParcelable<CheckPaidCrossingsResponse?>(Constants.CHECK_PAID_CHARGE_DATA_KEY)!!
        val mDataVrmRef =
            arguments?.getParcelable<CheckPaidCrossingsOptionsModel?>(Constants.CHECK_PAID_REF_VRM_DATA_KEY)!!
        val index =
            arguments?.getInt("Index")
        val country =
            arguments?.getString(Constants.COUNTRY_TYPE)
        vrm = arguments?.getString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED)!!
        exists = arguments?.getBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, false)!!


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

                val mPlateInfo = RetrievePlateInfoDetails(vrm,"",binding.makeInputEditText.text.toString().trim(),binding.modelInputEditText.text.toString().trim(),binding.colorInputEditText.text.toString().trim())
                val mVrmDetailsDvla =VehicleInfoDetails(mPlateInfo)
                    arguments?.putParcelable(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS,mVrmDetailsDvla)

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