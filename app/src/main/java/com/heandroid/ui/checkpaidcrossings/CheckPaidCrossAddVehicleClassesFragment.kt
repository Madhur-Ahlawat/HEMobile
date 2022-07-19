package com.heandroid.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.databinding.FragmentAddVehicleClassesBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckPaidCrossAddVehicleClassesFragment : BaseFragment<FragmentAddVehicleClassesBinding>() {

    private var loader: LoaderDialog? = null
    private var mClassType = ""
    private var exists: Boolean? = null
    private var vrm = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleClassesBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        vrm = arguments?.getString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED).toString()
        exists = arguments?.getBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, false)
        binding.title.text = "Vehicle registration number: $vrm"
        binding.classARadioButton.isChecked = true
        mClassType = "1"
        binding.classADesc.visible()
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

}



