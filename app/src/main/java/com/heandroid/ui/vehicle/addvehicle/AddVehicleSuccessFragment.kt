package com.heandroid.ui.vehicle.addvehicle

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
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentAddVehicleSuccessBinding
import com.heandroid.ui.account.creation.step4.businessaccount.dialog.AddBusinessVehicleListener
import com.heandroid.ui.account.creation.step4.businessaccount.dialog.BusinessAddConfirmDialog
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener
import com.heandroid.ui.vehicle.vehiclelist.VehicleListAdapter
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleSuccessFragment : BaseFragment<FragmentAddVehicleSuccessBinding>(),
    ItemClickListener, View.OnClickListener, AddBusinessVehicleListener {

    private var mScreeType = 0
    private lateinit var mAdapter: VehicleListAdapter
    private val mList = ArrayList<VehicleResponse?>()
    private var vehicleList = VehicleHelper.list
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAddVehicleSuccessBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        binding.tickLayout.visible()
        binding.tvYourVehicle.gone()
        binding.tickTxt.text = getString(R.string.str_new_vehicles_added_success)
       // binding.continueBtn.text = getString(R.string.str_back_to_vehicles_list)

        setAdapter(vehicleList)
    }

    override fun initCtrl() {
        binding.continueBtn.setOnClickListener(this@AddVehicleSuccessFragment)
        binding.notVehicle.setOnClickListener(this@AddVehicleSuccessFragment)
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.addVehicleApiVal, ::addVehicleApiCall)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.continueBtn -> {

              BusinessAddConfirmDialog.newInstance(
                  resources.getString(R.string.add_below_vehicle),
                  "", this@AddVehicleSuccessFragment)
                  .show(childFragmentManager, VehicleAddConfirmDialog.TAG)

           //     val bundle = Bundle()
           //     bundle.putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
           //     findNavController().navigate(R.id.action_addVehicleDoneFragment_to_vehicleListFragment, bundle)
            }

            R.id.notVehicle -> {

            }
        }
    }

    private fun setAdapter(vehicleList: MutableList<VehicleResponse?>?) {
        for(item in vehicleList?.indices!!){

            val plateInfo = vehicleList[item]?.plateInfo
            val vehicleInfo = vehicleList[item]?.vehicleInfo

            val plateInfoResp = PlateInfoResponse(plateInfo?.number ?: "",
                plateInfo?.country ?: "", "HE", plateInfo?.type ?:"", "",
                "", plateInfo?.planName ?: "")

            val vehicleInfoResp = VehicleInfoResponse(
                vehicleInfo?.make ?: "",
                vehicleInfo?.model ?: "", "2022", vehicleInfo?.typeId?:"",
                vehicleInfo?.rowId, vehicleInfo?.typeDescription?:"",
                vehicleInfo?.color ?: "",
                VehicleClassTypeConverter.toClassName(vehicleInfo?.vehicleClassDesc!!) ?: "",
                vehicleInfo.effectiveStartDate ?: ""
            )

            val mVehicleResponse = VehicleResponse(plateInfoResp, plateInfoResp,
                vehicleInfoResp, true)
            mList.add(mVehicleResponse)
        }

        if (mList.size > 0) {
            mAdapter = VehicleListAdapter(requireContext(), this)
            mAdapter.setList(mList)
            binding.recyclerViewHeader.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewHeader.setHasFixedSize(true)
            binding.recyclerViewHeader.adapter = mAdapter
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse?, pos: Int) {
    }

    override fun onItemClick(details: VehicleResponse?, pos: Int) {
        details?.isExpanded = details?.isExpanded != true
        mList[pos]?.isExpanded = details?.isExpanded
        mAdapter.notifyItemChanged(pos)
    }

    override fun onAddClick() {
        for (element in mList.indices) {
            // loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            vehicleMgmtViewModel.addVehicleApi(mList[element])
        }
    }

    private fun addVehicleApiCall(status: Resource<EmptyApiResponse?>?) {
      //  loader?.dismiss()
        when (status) {
            is Resource.Success -> {
             //   navigateToAddVehicleDoneScreen()
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }
    }
}
