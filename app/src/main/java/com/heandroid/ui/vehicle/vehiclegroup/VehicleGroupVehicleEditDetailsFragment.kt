package com.heandroid.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.VehicleGroupResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentGroupVehicleEditDetailBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.DateUtils
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleGroupVehicleEditDetailsFragment :
    BaseFragment<FragmentGroupVehicleEditDetailBinding>(), View.OnClickListener {

    private var mVehicleDetails: VehicleResponse? = null
    private var vehicleGroup: VehicleGroupResponse? = null
    private var vehicleGroupResponseList: ArrayList<VehicleGroupResponse?> = ArrayList()
    private val vehicleGroupMgmtViewModel: VehicleGroupMgmtViewModel by viewModels()
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var selectedGroupName: String? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentGroupVehicleEditDetailBinding.inflate(inflater, container, false)

    override fun init() {
        mVehicleDetails = arguments?.getParcelable(Constants.DATA)
        vehicleGroup = arguments?.getParcelable(Constants.VEHICLE_GROUP)
        setDataToView()
        binding.saveBtnModel = false
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, "")
        vehicleGroupMgmtViewModel.getVehicleGroupListApi()
    }

    override fun initCtrl() {
        binding.apply {
            saveBtn.setOnClickListener(this@VehicleGroupVehicleEditDetailsFragment)
            spinner.setOnItemClickListener { adapterView, _, position, _ ->
                checkButton(adapterView.getItemAtPosition(position).toString())
            }
            createNewGroupBtn.setOnClickListener(this@VehicleGroupVehicleEditDetailsFragment)
        }
    }

    private fun checkButton(name: String) {
        mVehicleDetails?.vehicleInfo?.groupName?.let {
            binding.saveBtnModel = it != name
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.saveBtn -> {
                mVehicleDetails?.let {
                    val request = it.apply {
                        newPlateInfo = plateInfo
                        vehicleInfo?.vehicleClassDesc =
                            VehicleClassTypeConverter.toClassCode(vehicleInfo?.vehicleClassDesc)
                        newPlateInfo?.vehicleGroup = binding.spinner.text.toString()
                    }
                    loader?.show(requireActivity().supportFragmentManager, "")
                    vehicleMgmtViewModel.updateVehicleApi(request)
                }

            }
            R.id.createNewGroupBtn -> {
                val bundle = Bundle().apply {
                    putBoolean(Constants.IS_CREATE_VEHICLE_GROUP, true)
                }
                findNavController().navigate(R.id.action_vehicleGroupVehicleEditDetailsFragment_to_createAndRenameVehicleGroupFragment, bundle)
            }
        }
    }

    override fun observer() {
        observe(vehicleGroupMgmtViewModel.getVehicleGroupListApiVal, ::handleVehicleGroupListData)
        observe(vehicleMgmtViewModel.updateVehicleApiVal, ::handleUpdatedVehicle)
    }

    private fun handleUpdatedVehicle(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                requireActivity().showToast("vehicle added successfully")
                vehicleGroup?.let { it ->
                    val bundle = Bundle().apply {
                        putParcelable(Constants.DATA, it)
                    }
                    findNavController().navigate(
                        R.id.action_vehicleGroupVehicleEditDetailsFragment_to_vehicleGroupFragment,
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

    private fun handleVehicleGroupListData(resource: Resource<List<VehicleGroupResponse?>?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (!it.isNullOrEmpty()) {
                        val list = arrayListOf<String>()
                        it.forEach { resp ->
                            resp?.groupName?.let { it1 -> list.add(it1) }
                        }
                        setSpinner(list)
                    } else {
                        setSpinner(null)
                    }
                }
            }
            is Resource.DataError -> {
                setSpinner(null)
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
                setSpinner(null)
            }
        }
    }


    private fun setDataToView() {
        mVehicleDetails?.let { response ->
            binding.vehicleData = response
            binding.tvAddedDate.text =
                DateUtils.convertDateFormat(response.vehicleInfo?.effectiveStartDate, 1)
            if (response.plateInfo?.vehicleGroup?.isEmpty() == true) {
                binding.groupLayout.gone()
            }
        }
    }

    private fun setSpinner(list: List<String>?) {
        val arr = arrayListOf<String>()
        list?.let {
            arr.addAll(list)
        }
        val arrayAdapter = ArrayAdapter(this.requireContext(), R.layout.item_spinner, arr)

        if (mVehicleDetails?.plateInfo?.vehicleGroup?.isEmpty() == true) {
            binding.spinner.setText(getString(R.string.select_group))
        } else {
            if (arr.contains(mVehicleDetails?.vehicleInfo?.groupName)) {
                binding.spinner.setText(mVehicleDetails?.vehicleInfo?.groupName)
            }
        }
        binding.spinner.setAdapter(arrayAdapter)
    }

}
