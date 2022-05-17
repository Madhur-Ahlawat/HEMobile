package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentAddVehicleDoneBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener
import com.heandroid.ui.vehicle.vehiclelist.VehicleListAdapter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleDoneFragment : BaseFragment<FragmentAddVehicleDoneBinding>(), ItemClickListener {

    private var mVehicleDetails: VehicleResponse?=null
    private var mScreeType = 0
    private lateinit var mAdapter: VehicleListAdapter
    private val mList = ArrayList<VehicleResponse?>()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleDoneBinding.inflate(inflater, container, false)

    override fun observer() {

    }

    override fun init() {
        mVehicleDetails = arguments?.getParcelable(Constants.DATA)
        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD) {
            binding.tickLayout.visible()
            binding.tvYourVehicle.gone()
            binding.tickTxt.text = getString(R.string.str_new_vehicles_added_success)
            binding.conformBtn.text = getString(R.string.str_back_to_vehicles_list)
        } else if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT){
            binding.tickLayout.gone()
            binding.tvYourVehicle.visible()
        }
        setAdapter()
    }

    override fun initCtrl() {
        binding.conformBtn.setOnClickListener {
            if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD) {
                findNavController().navigate(R.id.action_addVehicleDoneFragment_to_vehicleListFragment)
            } else if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT){
                val bundle=Bundle()
                val list = mutableListOf<VehicleResponse?>()
                list.add(mVehicleDetails)
                bundle.putParcelableArrayList(Constants.DATA,ArrayList(list))
                findNavController().navigate(R.id.action_addVehicleDoneFragment_to_makeOneOffPaymentCrossingFragment,bundle)
            }
        }
    }

    private fun setAdapter() {
        mList.clear()
        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {
            val plateInfoResp = PlateInfoResponse(
                mVehicleDetails?.plateInfo?.number?:"",
                mVehicleDetails?.plateInfo?.country?:"",
                "HE",
                "-",
                "",
                "",
                ""
            )

            val vehicleInfoResp = VehicleInfoResponse(
                mVehicleDetails?.vehicleInfo?.make?:"",
                mVehicleDetails?.vehicleInfo?.model?:"",
                "",
                "",
                "",
                "",
                mVehicleDetails?.vehicleInfo?.color?:"",
                mVehicleDetails?.vehicleInfo?.vehicleClassDesc?:"",
                mVehicleDetails?.vehicleInfo?.effectiveStartDate?:""
            )

            val mVehicleResponse1 =
                VehicleResponse(plateInfoResp, plateInfoResp, vehicleInfoResp, true)
            mList.add(mVehicleResponse1)
        } else if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD) {
            val plateInfoResp = PlateInfoResponse(
                mVehicleDetails?.plateInfo?.number?:"",
                mVehicleDetails?.plateInfo?.country?:"",
                "HE",
                "-",
                "",
                "",
                ""
            )

            val vehicleInfoResp = VehicleInfoResponse(
                mVehicleDetails?.vehicleInfo?.make?:"",
                mVehicleDetails?.vehicleInfo?.model?:"",
                "",
                "",
                "",
                "",
                mVehicleDetails?.vehicleInfo?.color?:"",
                mVehicleDetails?.vehicleInfo?.vehicleClassDesc?:"",
                mVehicleDetails?.vehicleInfo?.effectiveStartDate?:""
            )

            val mVehicleResponse1 =
                VehicleResponse(plateInfoResp, plateInfoResp, vehicleInfoResp, true)
            mList.add(mVehicleResponse1)
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

}