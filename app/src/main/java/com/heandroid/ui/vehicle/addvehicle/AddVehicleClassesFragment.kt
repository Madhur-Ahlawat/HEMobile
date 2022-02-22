package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.*
import com.heandroid.databinding.FragmentAddVehicleClassesBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.AddVehicleListener
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.Constants
import com.heandroid.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleClassesFragment : BaseFragment(), AddVehicleListener {

    private lateinit var dataBinding: FragmentAddVehicleClassesBinding
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private lateinit var mVehicleDetails: VehicleResponse
    private var mClassType = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAddVehicleClassesBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
    }

    private fun init() {

        mVehicleDetails =
            arguments?.getSerializable(Constants.DATA) as VehicleResponse

        dataBinding.title.text = "Vehicle registration number: ${mVehicleDetails.plateInfo.number}"

        dataBinding.classBRadioButton.isChecked = true
        mClassType = "Class B"

        dataBinding.classARadioButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                dataBinding.classBRadioButton.isChecked = false
                dataBinding.classCRadioButton.isChecked = false
                dataBinding.classDRadioButton.isChecked = false
                mClassType = "Class A"
            }

        }

        dataBinding.classBRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                dataBinding.classARadioButton.isChecked = false
                dataBinding.classCRadioButton.isChecked = false
                dataBinding.classDRadioButton.isChecked = false
                mClassType = "Class B"

            }


        }
        dataBinding.classCRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                dataBinding.classARadioButton.isChecked = false
                dataBinding.classBRadioButton.isChecked = false
                dataBinding.classDRadioButton.isChecked = false
                mClassType = "Class C"

            }


        }
        dataBinding.classDRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                dataBinding.classARadioButton.isChecked = false
                dataBinding.classBRadioButton.isChecked = false
                dataBinding.classCRadioButton.isChecked = false
                mClassType = "Class D"

            }

        }

        dataBinding.cancelButton.setOnClickListener {

        }

        dataBinding.continueButton.setOnClickListener {
            if (dataBinding.classVehicleCheckbox.isChecked && mClassType.isNotEmpty()) {
                mVehicleDetails.vehicleInfo.vehicleClassDesc = mClassType

                var dialog = VehicleAddConfirmDialog.newInstance(
                    mVehicleDetails,
                    this
                ).show(childFragmentManager, VehicleAddConfirmDialog.TAG)
            } else if (!dataBinding.classVehicleCheckbox.isChecked && mClassType.isNotEmpty()) {
                Snackbar.make(
                    dataBinding.classAView,
                    "Please select the checkbox",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (dataBinding.classVehicleCheckbox.isChecked && !mClassType.isNotEmpty()) {
                Snackbar.make(
                    dataBinding.classAView,
                    "Please select the class",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                Snackbar.make(
                    dataBinding.classAView,
                    "Please select the class and checkbox",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initCtrl() {

    }

    override fun onAddClick(details: VehicleResponse) {
        navigateToVehicleDetails()
//        addVehicleApiCall()
    }

    private fun addVehicleApiCall() {
        val request = mVehicleDetails.apply {
            plateInfo.state = "HE"
            plateInfo.type = "STANDARD"
            plateInfo.vehicleGroup = ""
            plateInfo.vehicleComments = "new Vehicle"
            plateInfo.planName = ""
            vehicleInfo.year = "2022"
            vehicleInfo.typeId = null
            vehicleInfo.typeDescription = "REGULAR"
        }

        mVehicleDetails = request;
        vehicleMgmtViewModel.addVehicleApi(request);
        vehicleMgmtViewModel.addVehicleApiVal.observe(viewLifecycleOwner, {  resource ->

                when (resource) {
                    is Resource.Success -> {
                        dataBinding.progressLayout.visibility = View.GONE
                        resource.data!!.body()?.let {
                            navigateToVehicleDetails()
                        }

                    }

                    is Resource.DataError -> {
                        //todo we need to update this
                        dataBinding.progressLayout.visibility = View.GONE
                        showToast(resource.errorMsg)
                        navigateToVehicleDetails()
                    }

                    is Resource.Loading -> {
                        dataBinding.progressLayout.visibility = View.VISIBLE
                    }
                }
            })
    }

    private fun navigateToVehicleDetails() {
        val bundle = Bundle().apply {
            putSerializable(Constants.DATA, mVehicleDetails)
            putInt(Constants.VEHICLE_SCREEN_KEY, Constants.VEHICLE_SCREEN_TYPE_ADD)
        }
        findNavController().navigate(R.id.addVehicleDoneFragment, bundle)
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

}