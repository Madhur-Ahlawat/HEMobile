package com.conduent.nationalhighways.ui.vehicle.vehiclegroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.AddDeleteVehicleGroup
import com.conduent.nationalhighways.data.model.vehicle.RenameVehicleGroup
import com.conduent.nationalhighways.data.model.vehicle.VehicleGroupMngmtResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleGroupResponse
import com.conduent.nationalhighways.databinding.FragmentCreateRenameVehicleGroupBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.openKeyboard
import com.conduent.nationalhighways.utils.extn.showToast
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAndRenameVehicleGroupFragment : BaseFragment<FragmentCreateRenameVehicleGroupBinding>(),
    View.OnClickListener {

    private var loader: LoaderDialog? = null
    private val vehicleGroupMgmtViewModel: VehicleGroupMgmtViewModel by viewModels()
    private var isCreate: Boolean = false
    private var vehicleGroup: VehicleGroupResponse? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCreateRenameVehicleGroupBinding.inflate(inflater, container, false)

    override fun onResume() {
        super.onResume()
        binding.edVehicleGroup.openKeyboard()
    }

    override fun init() {
        binding.btnModel = false
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        isCreate = arguments?.getBoolean(Constants.IS_CREATE_VEHICLE_GROUP) == true
        arguments?.let {
            if (it.containsKey(Constants.DATA)) {
                vehicleGroup = arguments?.getParcelable(Constants.DATA)
            }
        }

        if (isCreate) {
            binding.tvVehicleGroup.text = getString(R.string.create_vehicle_group)
            binding.continueBtn.text = getString(R.string.create_vehicle_group)
        } else {
            binding.edVehicleGroup.setText(vehicleGroup?.groupName)
        }
    }

    override fun initCtrl() {
        binding.apply {
            edVehicleGroup.onTextChanged {
                binding.btnModel = edVehicleGroup.text.toString().trim()
                    .isNotEmpty() && edVehicleGroup.text.toString()
                    .trim() != vehicleGroup?.groupName
            }
            cancelBtn.setOnClickListener(this@CreateAndRenameVehicleGroupFragment)
            continueBtn.setOnClickListener(this@CreateAndRenameVehicleGroupFragment)
        }
    }

    override fun observer() {
        observe(vehicleGroupMgmtViewModel.renameVehicleGroupApiVal, ::handleRenameGroup)
        observe(vehicleGroupMgmtViewModel.addVehicleGroupApiVal, ::handleAddNewGroup)
    }

    private fun handleRenameGroup(resource: Resource<VehicleGroupMngmtResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                requireActivity().showToast("group renamed successfully")
                findNavController().navigate(R.id.action_createAndRenameVehicleGroupFragment_to_vehicleGroupMngmtFragment)
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }
    }

    private fun handleAddNewGroup(resource: Resource<VehicleGroupMngmtResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                requireActivity().showToast("group created successfully")
                findNavController().navigate(R.id.action_createAndRenameVehicleGroupFragment_to_vehicleGroupMngmtFragment)
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.continueBtn -> {
                clickNext()
            }
            R.id.cancelBtn -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun clickNext() {
        if (isCreate) {
            val request =
                AddDeleteVehicleGroup(binding.edVehicleGroup.text.toString().trim())
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            vehicleGroupMgmtViewModel.addVehicleGroupApi(request)
        } else {
            vehicleGroup?.let {
                val request = RenameVehicleGroup(
                    it.groupId,
                    binding.edVehicleGroup.text.toString().trim()
                )
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                vehicleGroupMgmtViewModel.renameVehicleGroupApi(request)
            }

        }
    }
}