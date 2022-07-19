package com.heandroid.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.databinding.FragmentCheckPaidCrossingChangeVrmBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckPaidCrossFragmentChangeVrm : BaseFragment<FragmentCheckPaidCrossingChangeVrmBinding>(),
    View.OnClickListener {

    private var exists: Boolean = false
    private var vrm : String = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCheckPaidCrossingChangeVrmBinding.inflate(inflater, container, false)

    override fun init() {

        val country = arguments?.getString(Constants.COUNTRY_TYPE)
        vrm = arguments?.getString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED).toString()
        exists = arguments?.getBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS) == true

        binding.apply {
            if (exists) {
                val mVrmDetailsDvla =
                    arguments?.getParcelable<VehicleInfoDetails?>(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS)

                regNum.text = vrm
                countryMarker.text = country
                vehicleClass.text =
                    VehicleClassTypeConverter.toClassCode(mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleClass)
                make.text = mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleMake
                model.text = mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleModel
                color.text = mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleColor

                changeVehicle.text = getString(R.string.str_continue)
                removeVehicle.gone()
            } else {
                regNum.text = vrm
                countryMarker.text = country
                vehicleClass.text = "-"
                make.text = "-"
                model.text = "-"
                color.text = "-"
                changeVehicle.text = getString(R.string.str_change)
                removeVehicle.visible()
            }
        }
    }

    override fun initCtrl() {
        binding.removeVehicle.setOnClickListener(this)
        binding.changeVehicle.setOnClickListener(this)
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.removeVehicle -> {
            }
            R.id.changeVehicle -> {
                if (!exists) {
                    findNavController().navigate(
                        R.id.action_checkPaidCrossingChangeVrm_to_addVehicleDetailsFragment,
                        arguments
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_checkPaidCrossingChangeVrm_to_checkPaidCrossingChangeVrmConform,
                        arguments
                    )
                }
            }
        }
    }

}