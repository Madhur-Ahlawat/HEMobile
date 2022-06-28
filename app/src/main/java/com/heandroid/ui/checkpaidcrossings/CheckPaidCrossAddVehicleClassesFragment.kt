package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.account.ValidVehicleCheckRequest
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentAddVehicleClassesBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckPaidCrossAddVehicleClassesFragment : BaseFragment<FragmentAddVehicleClassesBinding>(){

    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var mVehicleDetails: VehicleResponse? = null
    private var loader: LoaderDialog? = null
    private var mClassType = ""
    private var mScreeType = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleClassesBinding.inflate(inflater, container, false)


    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        Logg.logging("testing", " AddVehicleClassesFragment mScreeType  $mScreeType")

        binding.title.text = "Vehicle registration number: ${mVehicleDetails?.plateInfo?.number}"

        binding.classARadioButton.isChecked = true
        mClassType = "1"
        binding.classADesc.visible()
    }

    override fun initCtrl() {
        binding.classADesc.visible()

/*
        binding.classARadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.apply {
                    classBRadioButton.isChecked = false
                    classCRadioButton.isChecked = false
                    classDRadioButton.isChecked = false
                    classADesc.visible()
                    classBDesc.gone()
                    classCDesc.gone()
                    classDDesc.gone()
                }
                mClassType = "1"
            }
        }
*/

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


    }

    override fun observer() {
    }




}



