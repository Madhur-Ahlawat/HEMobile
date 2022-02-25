package com.heandroid.ui.vehicle.addvehicle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.*
import com.heandroid.databinding.FragmentAddVehicleDoneBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener
import com.heandroid.ui.vehicle.vehiclelist.VehicleListAdapter
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleDoneFragment : BaseFragment<FragmentAddVehicleDoneBinding>(), ItemClickListener {

    private lateinit var mVehicleDetails: VehicleResponse
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
        mVehicleDetails =
            arguments?.getSerializable(Constants.DATA) as VehicleResponse
        mScreeType = arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)!!

        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD) {
            binding.tickLayout.visibility = View.VISIBLE
            binding.tickTxt.text = getString(R.string.str_new_vehicles_added_success)
            binding.conformBtn.text = getString(R.string.str_back_to_vehicles_list)
            binding.addVehiclesTxt.visibility = View.GONE
        }

        setAdapter()
    }

    override fun initCtrl() {
        binding.conformBtn.setOnClickListener {
            if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {
//                val intent = Intent(this, ActivityFutureCrossing::class.java)
//                intent.putExtra("list", mVehicleDetails)
//                startActivity(intent)
            } else {
//                onBackPressed()
            }
        }
    }

    private fun setAdapter() {
        mList.clear()
        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {

            val plateInfoResp = PlateInfoResponse(
                mVehicleDetails.plateInfo.number,
                mVehicleDetails.plateInfo.country,
                "HE",
                "-",
                "",
                "",
                ""
            )

            val vehicleInfoResp = VehicleInfoResponse(
                mVehicleDetails.vehicleInfo.make,
                mVehicleDetails.vehicleInfo.model,
                "",
                "",
                "",
                "",
                mVehicleDetails.vehicleInfo.color,
                "",
                ""
            )

            val mVehicleResponse1 =
                VehicleResponse(plateInfoResp, plateInfoResp, vehicleInfoResp, true)
            mList.add(mVehicleResponse1)
        } else {


            val plateInfoResp = PlateInfoResponse(
                mVehicleDetails.plateInfo.number,
                mVehicleDetails.plateInfo.country,
                "HE",
                "-",
                "",
                "",
                ""
            )

            val vehicleInfoResp = VehicleInfoResponse(
                mVehicleDetails.vehicleInfo.make,
                mVehicleDetails.vehicleInfo.model,
                "",
                "",
                "",
                "",
                mVehicleDetails.vehicleInfo.color,
                mVehicleDetails.vehicleInfo.vehicleClassDesc,
                mVehicleDetails.vehicleInfo.effectiveStartDate
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

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(details: VehicleResponse, pos: Int) {
        TODO("Not yet implemented")
    }

}