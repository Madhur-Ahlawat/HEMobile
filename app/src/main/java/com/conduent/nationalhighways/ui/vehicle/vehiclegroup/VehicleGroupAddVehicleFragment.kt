package com.conduent.nationalhighways.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleGroupResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentVehicleGroupAddVehicleBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.history.adapter.AccountPaymentHistoryPaginationAdapter
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.ui.vehicle.vehiclegroup.adapter.VehicleGroupVehiclesAdapter
import com.conduent.nationalhighways.ui.vehicle.vehiclegroup.dialog.SearchVehicleListener
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.showToast
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleGroupAddVehicleFragment : BaseFragment<FragmentVehicleGroupAddVehicleBinding>(),
    View.OnClickListener, SearchVehicleListener {

    private var vehicleResponseList: ArrayList<VehicleResponse?> = ArrayList()
    private lateinit var vehiclesAdapter: VehicleGroupVehiclesAdapter
    private val vehicleGroupMgmtViewModel: VehicleGroupMgmtViewModel by viewModels()
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var checkedVehicleList: ArrayList<VehicleResponse?> = ArrayList()
    private var paginationNumberAdapter: AccountPaymentHistoryPaginationAdapter? = null
    private var paginationLinearLayoutManager: LinearLayoutManager? = null
    private var vehicleGroup: VehicleGroupResponse? = null
    private var loader: LoaderDialog? = null
    private val countPerPage = 10
    private var searchVehicleNumber: String? = null
    private var startIndex = 1
    private var noOfPages = 1
    private var selectedPosition = 1


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentVehicleGroupAddVehicleBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        vehicleGroup = arguments?.getParcelable(Constants.DATA)

        paginationNumberAdapter =
            AccountPaymentHistoryPaginationAdapter(this, noOfPages, selectedPosition)

        vehiclesAdapter = VehicleGroupVehiclesAdapter(this, vehicleResponseList)
        vehicleMgmtViewModel.getUnAllocatedVehiclesApi()

    }

    override fun init() {
        checkedVehicleList.clear()
        binding.addVehicleModel = true
        binding.bulkUploadModel = false

        binding.prevBtnModel = false
        binding.nextBtnModel = false
        vehicleGroup?.let {
            binding.addVehicleToGroup.text = getString(R.string.add_vehicle_to_group, it.groupName)
        }
//        binding.paginationLayout.visible()
        binding.progressBar.visible()
        binding.tvNoVehicles.gone()
        binding.rvVehicleList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = vehiclesAdapter
        }
        paginationLinearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.paginationNumberRecyclerView.apply {
            layoutManager = paginationLinearLayoutManager
            adapter = paginationNumberAdapter
        }

        checkButtons()
    }

    override fun initCtrl() {
        binding.apply {
            tvFilter.setOnClickListener(this@VehicleGroupAddVehicleFragment)
            addVehicleBtn.setOnClickListener(this@VehicleGroupAddVehicleFragment)
            cancelBtn.setOnClickListener(this@VehicleGroupAddVehicleFragment)
            bulkUploadBtn.setOnClickListener(this@VehicleGroupAddVehicleFragment)
        }
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.addVehiclesToGroupApiVal, ::handleUpdatedVehicle)
        observe(vehicleMgmtViewModel.unAllocatedVehicleListVal, ::handleVehicleListData)
        observe(
            vehicleGroupMgmtViewModel.searchVehicleVal,
            ::handleVehicleListData
        )
    }

    private fun handleUpdatedVehicle(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                requireActivity().showToast("vehicle(s) added successfully")
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                vehicleMgmtViewModel.getUnAllocatedVehiclesApi()
//                vehicleGroup?.let {
//                    val bundle = Bundle().apply {
//                        putParcelable(Constants.DATA, it)
//                    }
//                    findNavController().navigate(
//                        R.id.action_vehicleGroupAddVehicleFragment_to_vehicleGroupFragment,
//                        bundle
//                    )
//                }
            }
            is Resource.DataError -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                vehicleMgmtViewModel.getUnAllocatedVehiclesApi()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun handleVehicleListData(resource: Resource<List<VehicleResponse?>?>?) {
        binding.progressBar.gone()
        binding.tvNoVehicles.gone()
        binding.rvVehicleList.visible()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (!it.isNullOrEmpty()) {
                        checkedVehicleList.clear()
                        vehicleResponseList.clear()
                        vehicleResponseList.addAll(it)
                        if (vehicleResponseList.isEmpty()) {
                            handleVehicleData()
                        } else {
                            setVehicleListAdapter()
                        }
                    } else {
                        handleVehicleData()
                    }
                }
            }
            is Resource.DataError -> {
                handleVehicleData()
                if (resource.errorModel?.errorCode != Constants.NO_DATA_FOR_GIVEN_INDEX)
                    ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
        searchVehicleNumber = null
    }

    private fun handleVehicleData() {
        searchVehicleNumber?.let {
            binding.apply {
                rvVehicleList.gone()
//                paginationLayout.gone()
                tvNoVehicles.visible()
                tvNoVehicles.text = getString(R.string.no_vehicles_found, it)
            }
        } ?: run {
            binding.apply {
                rvVehicleList.gone()
//                paginationLayout.gone()
                tvNoVehicles.visible()
                tvNoVehicles.text = getString(R.string.str_no_vehicles)
            }
        }
    }

    private fun setVehicleListAdapter() {
        vehiclesAdapter = VehicleGroupVehiclesAdapter(this, vehicleResponseList)
        binding.rvVehicleList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vehiclesAdapter
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvFilter -> {
//                SearchVehicleDialog.newInstance(
//                    getString(R.string.str_title),
//                    getString(R.string.str_sub_title),
//                    this
//                ).show(childFragmentManager, Constants.SEARCH_VEHICLE_DIALOG)
            }
            R.id.addVehicleBtn -> {
                if (checkedVehicleList.size >= 1) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    vehicleMgmtViewModel.addVehiclesToGroup(checkedVehicleList, vehicleGroup)
                }
            }
            R.id.cancelBtn -> {
                findNavController().popBackStack()
            }
            R.id.bulkUploadBtn -> {

            }
        }
    }

    fun setSelectedVehicle(vehicle: VehicleResponse?) {
        vehicle?.let {
            if (!checkedVehicleList.contains(it)) {
                checkedVehicleList.add(it)
            } else {
                checkedVehicleList.remove(it)
            }
            checkButtons()
        }
    }

    private fun checkButtons() {
        when {
            checkedVehicleList.size > 0 -> {
                binding.addVehicleModel = true
            }
            else -> {
                binding.addVehicleModel = false
            }
        }
    }

    override fun onClick(plateNumber: String) {
        vehicleGroup?.let {
            binding.progressBar.visible()
            binding.rvVehicleList.gone()
            searchVehicleNumber = plateNumber
            vehicleGroup?.let {
                vehicleGroupMgmtViewModel.getSearchVehiclesForGroup(
                    it.groupName!!,
                    plateNumber
                )
            }
        }
    }
}
