package com.heandroid.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.VehicleGroupResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleGroupAddVehicleBinding
import com.heandroid.databinding.FragmentVehicleGroupBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.account.payments.history.AccountPaymentHistoryPaginationAdapter
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.visible
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
        vehicleMgmtViewModel.getVehicleInformationApi()

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
        binding.paginationLayout.visible()
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
        observe(vehicleMgmtViewModel.updateVehicleApiVal, ::handleUpdatedVehicle)
        observe(vehicleMgmtViewModel.vehicleListVal, ::handleVehicleListData)
        observe(
            vehicleGroupMgmtViewModel.searchVehicleVal,
            ::handleVehicleListData
        )
    }

    private fun handleUpdatedVehicle(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                requireActivity().showToast("vehicle added successfully")
                vehicleGroup?.let {
                    val bundle = Bundle().apply {
                        putParcelable(Constants.DATA, it)
                    }
                    findNavController().navigate(
                        R.id.action_vehicleGroupAddVehicleFragment_to_vehicleGroupFragment,
                        bundle
                    )
                }
            }
            is Resource.DataError -> {
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
                        it.forEach { vehicle ->
                            if (vehicle?.plateInfo?.vehicleGroup.isNullOrEmpty()){
                                vehicleResponseList.add(vehicle)
                            }
                        }
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
                paginationLayout.gone()
                tvNoVehicles.visible()
                tvNoVehicles.text = getString(R.string.no_vehicles_found, it)
            }
        } ?: run {
            binding.apply {
                rvVehicleList.gone()
                paginationLayout.gone()
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
//                ).show(childFragmentManager, "")
            }
            R.id.addVehicleBtn -> {
                if (checkedVehicleList.size > 1) {
                    requireActivity().showToast("multiple vehicles cant be added, it is in dev")
                } else if (checkedVehicleList.size == 1) {
                    checkedVehicleList[0]?.let {
                        val request = it.apply {
                            newPlateInfo = plateInfo
                            vehicleInfo?.vehicleClassDesc = VehicleClassTypeConverter.toClassCode(vehicleInfo?.vehicleClassDesc)
                            newPlateInfo?.vehicleGroup = vehicleGroup?.groupName.toString()
                        }
                        loader?.show(requireActivity().supportFragmentManager, "")
                        vehicleMgmtViewModel.updateVehicleApi(request)
                    }
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
                    it.groupName,
                    plateNumber
                )
            }
        }
    }
}
