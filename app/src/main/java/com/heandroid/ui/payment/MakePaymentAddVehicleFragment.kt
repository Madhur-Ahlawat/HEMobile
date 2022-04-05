package com.heandroid.ui.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountNonVehicleModel
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.base.BaseFragment
import com.heandroid.databinding.FragmentMakePaymentAddVehicleBinding
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleListener
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MakePaymentAddVehicleFragment : BaseFragment<FragmentMakePaymentAddVehicleBinding>(),
    View.OnClickListener, AddVehicleListener, ItemClickListener {

    private lateinit var mAdapter: AddedVehicleListAdapter
    private var addDialog: AddVehicleDialog? = null
    private var isAccountVehicle: Boolean? = false
    private var loader: LoaderDialog? = null
    private var vehicleList = VehicleHelper.list
    private var createAccountNonVehicleModel: CreateAccountNonVehicleModel? = null
    private var isFromOneOfPayment: Boolean? = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMakePaymentAddVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        mAdapter = AddedVehicleListAdapter(this)
        mAdapter.setList(vehicleList)
        binding.rvVehiclesList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVehiclesList.setHasFixedSize(true)
        binding.rvVehiclesList.adapter = mAdapter
        if (vehicleList?.isEmpty() == true || vehicleList?.size == 0) {
            binding.apply {
                rvVehiclesList.visibility = View.GONE
                noVehiclesAdded.visibility = View.VISIBLE
                addVehiclesTxt.text = getString(R.string.str_add_vehicle_to_account)
            }
            setBtnDisabled()
            setAddBtnActivated()
        }
        //else {
        //    setAdapter()
        //}

        isAccountVehicle = arguments?.getBoolean("IsAccountVehicle")
        createAccountNonVehicleModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_NON_UK)
        isFromOneOfPayment = arguments?.getBoolean(Constants.PAYMENT_ONE_OFF)

        when {
            isAccountVehicle == true -> {
                setAdapter(true)
            }
            createAccountNonVehicleModel?.isFromCreateNonVehicleAccount == true -> {
                setAdapter(true, createAccountNonVehicleModel?.plateCountry)
            }
            else -> {
                setAdapter(false)
            }
        }
    }

    override fun initCtrl() {
        binding.addVehicleBtn.setOnClickListener(this)
        binding.findVehicle.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addVehicleBtn -> {
                addDialog = AddVehicleDialog.newInstance(
                    getString(R.string.str_title),
                    getString(R.string.str_sub_title),
                    this
                )
                addDialog?.show(childFragmentManager, AddVehicleDialog.TAG)
            }
            R.id.findVehicle -> {
                if(isAccountVehicle == true){
                   // loader?.show(requireActivity().supportFragmentManager, "")
                    val bundle =  Bundle()
                    bundle.putBoolean("IsAccountVehicle", true)
                    bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA,arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA))

                    findNavController().navigate(R.id.action_makePaymentAddVehicleFragment_to_CreateAccountVehicleDetailsFragment, bundle)
                } else if(createAccountNonVehicleModel?.isFromCreateNonVehicleAccount == true){
                    val bundle =  Bundle()
                    bundle.putBoolean("isNonUKVehicleUpdating", true)
                    bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA,arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA))
                    findNavController().navigate(R.id.action_ukAndNonUkVehicleListFragment_to_NonUkDropDownVehicleListFragment, bundle)
                }
            }
        }
    }

    private fun setAdapter(isAccountVehicle: Boolean? = false, vehicle: String? = "UK") {
        binding.rvVehiclesList.visibility = View.VISIBLE
        binding.noVehiclesAdded.visibility = View.GONE
        binding.addVehiclesTxt.text = requireContext().getString(R.string.txt_your_vehicle)

        if (isAccountVehicle == true && vehicle == "UK") {
            val vehicleNo = arguments?.getString("VehicleNo")
            val plateRes = PlateInfoResponse()
            plateRes.number = vehicleNo.toString()
            plateRes.country = "UK"
            val vehicleRes = VehicleResponse(PlateInfoResponse(),plateRes, VehicleInfoResponse(), false, 0, 0.0 )
            if(vehicleList?.contains(vehicleRes)==false)
            vehicleList?.add(vehicleRes)
            mAdapter.setList(vehicleList)
            mAdapter.notifyDataSetChanged()
        }
        else if(isAccountVehicle == true && vehicle == "Non-UK"){
            val plateResponse = PlateInfoResponse()
            plateResponse.number = createAccountNonVehicleModel?.vehiclePlate.toString()
            plateResponse.country = vehicle

            val vehicleInfo = VehicleInfoResponse()
            vehicleInfo.color = createAccountNonVehicleModel?.vehicleColor
            vehicleInfo.make = createAccountNonVehicleModel?.vehicleMake
            vehicleInfo.model = createAccountNonVehicleModel?.vehicleModel
            vehicleInfo.vehicleClassDesc = createAccountNonVehicleModel?.plateTypeDesc

            val vehicleRes = VehicleResponse(PlateInfoResponse(),plateResponse, vehicleInfo, false, 0, 0.0 )
            if(vehicleList?.contains(vehicleRes)==false)
            vehicleList?.add(vehicleRes)
            mAdapter.setList(vehicleList)
            mAdapter.notifyDataSetChanged()
        }
        else {
            mAdapter.setList(vehicleList)
            mAdapter.notifyDataSetChanged()
        }
        if (vehicleList?.isNotEmpty() == true) {
            setBtnActivated()
        }
        if (vehicleList?.size?:0 < Constants.MAX_VEHICLE_SIZE) {
            setAddBtnActivated()
        } else {
            setAddBtnDisabled()
        }
    }

    override fun onAddClick(details: VehicleResponse) {
        addDialog?.dismiss()
        when {
            details.plateInfo?.country == Constants.UK -> {
                vehicleList?.add(details)
                setAdapter(true)
            }

            isFromOneOfPayment == true -> {
                val bundle = Bundle().apply {
                    putParcelable(Constants.DATA, details)
                    putBoolean(Constants.PAYMENT_PAGE, true)
                }
                findNavController().navigate(
                    R.id.action_makePaymentAddVehicleFragment_to_addVehicleDetailsFragment,
                    bundle
                )
            }
            else -> {
                val bundle = Bundle().apply {
                    putBoolean("isSecondNonUkVehicle", true)
                    putString("VehicleNo", details?.plateInfo?.number)
                    putString("Country", "Non-UK")
                }
                findNavController().navigate(R.id.action_makePaymentAddVehicleFragment_to_callNonUkVehicleAdd, bundle)
            }
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse?, pos: Int) {
        setAddBtnActivated()
        if (vehicleList?.size?: 0 > 0) {
            vehicleList?.removeAt(pos)
            if (::mAdapter.isInitialized) {
                mAdapter.setList(vehicleList)
                mAdapter.notifyItemRemoved(pos)
            }
        }
        if (vehicleList?.size == 0) {
            setBtnDisabled()
            binding.apply {
                rvVehiclesList.visibility = View.GONE
                noVehiclesAdded.visibility = View.VISIBLE
                addVehiclesTxt.text = getString(R.string.str_add_vehicle_to_account)
            }
        }
    }

    override fun onItemClick(details: VehicleResponse?, pos: Int) { }

    private fun setBtnActivated() {
        binding.findButton = true
    }

    private fun setBtnDisabled() {
        binding.findButton = false
    }

    private fun setAddBtnActivated() {
        binding.addButton = true
    }

    private fun setAddBtnDisabled() {
        binding.addButton = false
    }
}