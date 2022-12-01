package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.databinding.FragmentPaidCrossAddVehicleClassesBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.VehicleClassTypeConverter
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckPaidCrossAddVehicleClassesFragment : BaseFragment<FragmentPaidCrossAddVehicleClassesBinding>() {

    private var loader: LoaderDialog? = null
    private var mClassType = ""
    private var exists: Boolean? = null
    private var vrm = ""

    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaidCrossAddVehicleClassesBinding.inflate(inflater, container, false)

    override fun init() {
        AdobeAnalytics.setScreenTrack(
            "check crossings:vehicle class declaration",
            "vehicle",
            "english",
            "check crossings",
            "home",
            "check crossings:vehicle class declaration",
            sessionManager.getLoggedInUser()
        )

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        vrm = arguments?.getString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED).toString()
        exists = arguments?.getBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, false)
        binding.title.text = getString(R.string.vehicle_reg_num, vrm)

        binding.classBRadioButton.isChecked = true
        mClassType = "2"
        binding.classBDesc.visible()

    }

    override fun onResume() {
        super.onResume()
        binding.classVehicleCheckbox.isChecked = false
        isEnabled()
    }


    override fun initCtrl() {
        binding.classBDesc.visible()
        binding.classBRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.apply {
                    classARadioButton.isChecked = false
                    classCRadioButton.isChecked = false
                    classDRadioButton.isChecked = false
                    classADesc.gone()
                    classBDesc.visible()
                    classCDesc.gone()
                    classDDesc.gone()
                }
                mClassType = "2"
            }
        }
        binding.classCRadioButton.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                binding.apply {
                    classARadioButton.isChecked = false
                    classBRadioButton.isChecked = false
                    classDRadioButton.isChecked = false
                    classADesc.gone()
                    classBDesc.gone()
                    classCDesc.visible()
                    classDDesc.gone()
                }
                mClassType = "3"
            }
        }
        binding.classDRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.apply {
                    classARadioButton.isChecked = false
                    classBRadioButton.isChecked = false
                    classCRadioButton.isChecked = false
                    classADesc.gone()
                    classBDesc.gone()
                    classCDesc.gone()
                    classDDesc.visible()
                }
                mClassType = "4"
            }
        }
        binding.classVehicleCheckbox.setOnClickListener{
            isEnabled()
        }
        binding.cancelButton.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.continueButton.setOnClickListener {
            arguments?.putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, true)
            val mVrmDetailsDvla =
                arguments?.getParcelable<VehicleInfoDetails?>(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS)
            mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleClass =
                VehicleClassTypeConverter.toClassName(mClassType)
            arguments?.putParcelable(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS, mVrmDetailsDvla)
            findNavController().navigate(
                R.id.action_addVehicleClassesFragment_to_checkPaidCrossingChangeVrm,
                arguments
            )
        }
    }

    override fun observer() {}

    private fun isEnabled() {
        binding.model = binding.classVehicleCheckbox.isChecked
    }

}



