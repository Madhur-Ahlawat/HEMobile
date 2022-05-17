package com.heandroid.ui.vehicle.vehiclegroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.vehicle.AddDeleteVehicleGroup
import com.heandroid.data.model.vehicle.RenameVehicleGroup
import com.heandroid.data.model.vehicle.VehicleGroupMngmtResponse
import com.heandroid.data.model.vehicle.VehicleGroupResponse
import com.heandroid.databinding.FragmentCreateRenameVehicleGroupBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.onTextChanged
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
        }
    }

    override fun initCtrl() {
        binding.apply {
            edVehicleGroup.onTextChanged {
                binding.btnModel = edVehicleGroup.text.toString().trim().isNotEmpty()
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
                if (isCreate) {
                    val request =
                        AddDeleteVehicleGroup(binding.edVehicleGroup.text.toString().trim())
                    loader?.show(requireActivity().supportFragmentManager, "")
                    vehicleGroupMgmtViewModel.addVehicleGroupApi(request)
                } else {
                    vehicleGroup?.let {
                        val request = RenameVehicleGroup(
                            it.groupId,
                            binding.edVehicleGroup.text.toString().trim()
                        )
                        loader?.show(requireActivity().supportFragmentManager, "")
                        vehicleGroupMgmtViewModel.renameVehicleGroupApi(request)
                    }

                }
            }
            R.id.cancelBtn -> {
                findNavController().popBackStack()
            }
        }
    }
}