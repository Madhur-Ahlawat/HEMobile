package com.heandroid.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.*
import com.heandroid.databinding.FragmentVehicleGroupMngmtBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleGroupMngmtFragment : BaseFragment<FragmentVehicleGroupMngmtBinding>(),
    View.OnClickListener, DeleteVehicleGroupListener {

    private var vehicleGroupResponseList: ArrayList<VehicleGroupResponse?> = ArrayList()
    private lateinit var groupsAdapter: VehicleGroupNamesAdapter
    private val vehicleGroupMgmtViewModel: VehicleGroupMgmtViewModel by viewModels()
    private var checkedGroupsList: ArrayList<VehicleGroupResponse> = ArrayList()
    private var loader: LoaderDialog? = null
    private var isDelete: Boolean = false
    private var isGetList: Boolean = false


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentVehicleGroupMngmtBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        groupsAdapter = VehicleGroupNamesAdapter(this, vehicleGroupResponseList)

        loader?.show(requireActivity().supportFragmentManager, "")
        isGetList = true
        vehicleGroupMgmtViewModel.getVehicleGroupListApi()
    }

    override fun init() {
        checkedGroupsList.clear()
        checkButtons()
        binding.rvVehicleGroupList.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = groupsAdapter
        }
    }

    override fun initCtrl() {
        binding.apply {
            createVehicleGroupBtn.setOnClickListener(this@VehicleGroupMngmtFragment)
            renameVehicleGroupBtn.setOnClickListener(this@VehicleGroupMngmtFragment)
            deleteVehicleGroupBtn.setOnClickListener(this@VehicleGroupMngmtFragment)
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.createVehicleGroupBtn -> {
                val bundle = Bundle().apply {
                    putBoolean(Constants.IS_CREATE_VEHICLE_GROUP, true)
                }
                findNavController().navigate(
                    R.id.action_vehicleGroupMngmtFragment_to_createAndRenameVehicleGroupFragment,
                    bundle
                )
            }
            R.id.renameVehicleGroupBtn -> {
                if (checkedGroupsList.size == 1) {
                    val bundle = Bundle().apply {
                        putParcelable(Constants.DATA, checkedGroupsList[0])
                        putBoolean(Constants.IS_CREATE_VEHICLE_GROUP, false)
                    }
                    findNavController().navigate(
                        R.id.action_vehicleGroupMngmtFragment_to_createAndRenameVehicleGroupFragment,
                        bundle
                    )
                }
            }

            R.id.deleteVehicleGroupBtn -> {
                if (checkedGroupsList.size > 1) {
                    requireActivity().showToast("multiple groups delete api not implemented")
                } else {
                    DeleteVehicleGroupDialog.newInstance(
                        getString(R.string.str_title),
                        getString(R.string.str_sub_title),
                        this
                    ).show(childFragmentManager, "")
                }
            }
        }
    }

    override fun observer() {
        observe(vehicleGroupMgmtViewModel.getVehicleGroupListApiVal, ::handleVehicleGroupListData)
        observe(vehicleGroupMgmtViewModel.deleteVehicleGroupApiVal, ::handleDeleteVehicle)
    }


    private fun handleDeleteVehicle(resource: Resource<VehicleGroupMngmtResponse?>?) {
        loader?.dismiss()
        if (isDelete) {
            when (resource) {
                is Resource.Success -> {
                    checkedGroupsList.clear()
                    vehicleGroupResponseList.clear()
                    groupsAdapter.notifyDataSetChanged()
                    checkButtons()
                    requireContext().showToast("vehicle Group deleted successfully")
                    getVehicleGroupList()
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {

                }
            }
            isDelete = false
        }
    }

    private fun handleVehicleGroupListData(resource: Resource<List<VehicleGroupResponse?>?>?) {
        loader?.dismiss()
        if (isGetList) {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        if (!it.isNullOrEmpty()) {
                            setVehicleListAdapter(it)
                        } else {
                            binding.tvNoGroups.visible()
                            binding.rvVehicleGroupList.gone()
                        }
                    }
                }
                is Resource.DataError -> {
                    binding.tvNoGroups.visible()
                    binding.rvVehicleGroupList.gone()
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {
                }
            }
            isGetList = false
        }

    }

    private fun setVehicleListAdapter(list: List<VehicleGroupResponse?>) {
        groupsAdapter = VehicleGroupNamesAdapter(this, list)
        binding.rvVehicleGroupList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = groupsAdapter
        }
    }

    private fun checkButtons() {
        when {
            checkedGroupsList.size > 1 -> {
                binding.createNewGroup = false
                binding.deleteGroup = true
                binding.renameGroup = false

            }
            checkedGroupsList.size == 1 -> {
                binding.createNewGroup = false
                binding.deleteGroup = true
                binding.renameGroup = true
            }
            checkedGroupsList.size == 0 -> {
                binding.createNewGroup = true
                binding.deleteGroup = false
                binding.renameGroup = false
            }
        }
    }

    fun setSelectedVehicleGroupId(group: VehicleGroupResponse?) {
        group?.let {
            if (!checkedGroupsList.contains(it)) {
                checkedGroupsList.add(it)
            } else {
                checkedGroupsList.remove(it)
            }
            checkButtons()
        }
    }

    override fun onDeleteClick() {
        if (checkedGroupsList.size == 1) {
            isDelete = true
            loader?.show(requireActivity().supportFragmentManager, "")
            val request = AddDeleteVehicleGroup(checkedGroupsList[0].groupName)
            vehicleGroupMgmtViewModel.deleteVehicleGroupApi(request)
        }
    }

    private fun getVehicleGroupList() {
        loader?.show(requireActivity().supportFragmentManager, "")
        isGetList = true
        binding.tvNoGroups.gone()
        binding.rvVehicleGroupList.visible()
        vehicleGroupMgmtViewModel.getVehicleGroupListApi()
    }
}