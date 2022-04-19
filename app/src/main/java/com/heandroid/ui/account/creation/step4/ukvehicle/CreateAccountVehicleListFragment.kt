package com.heandroid.ui.account.creation.step4.ukvehicle

import android.os.Bundle
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
import com.heandroid.databinding.FragmentCreateAccountVehicleListBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.payment.AddedVehicleListAdapter
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleListener
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import com.heandroid.utils.common.VehicleHelper
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible

class CreateAccountVehicleListFragment: BaseFragment<FragmentCreateAccountVehicleListBinding>(),
    View.OnClickListener, AddVehicleListener, ItemClickListener {

    private lateinit var mAdapter: AddedVehicleListAdapter
    private var addDialog: AddVehicleDialog? = null
    private var isAccountVehicle: Boolean? = false
    private var loader: LoaderDialog? = null
    private var vehicleList = VehicleHelper.createAccountList
    private var createAccountNonVehicleModel: CreateAccountNonVehicleModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =  FragmentCreateAccountVehicleListBinding.inflate(inflater,container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        mAdapter = AddedVehicleListAdapter(this)
        mAdapter.setList(vehicleList)
        binding.rvVehiclesList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVehiclesList.setHasFixedSize(true)
        binding.rvVehiclesList.adapter = mAdapter

        isAccountVehicle = arguments?.getBoolean("IsAccountVehicle")
        createAccountNonVehicleModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_NON_UK)

            if(isAccountVehicle == true){
                setAdapter(true)
            }else {
                setAdapter(true, createAccountNonVehicleModel?.plateCountry)
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
                if (isAccountVehicle == true) {
                    // loader?.show(requireActivity().supportFragmentManager, "")
                    val bundle = Bundle()
                    bundle.putBoolean("IsAccountVehicle", true)
                    bundle.putParcelable(
                        Constants.CREATE_ACCOUNT_DATA,
                        arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
                    )
                    bundle.putParcelable(
                        Constants.CREATE_ACCOUNT_DATA,
                        arguments?.getParcelable(Constants.DATA)
                    )

                    findNavController().navigate(
                        R.id.action_makePaymentAddVehicleFragment_to_CreateAccountVehicleDetailsFragment,
                        bundle
                    )
                } else if (createAccountNonVehicleModel?.isFromCreateNonVehicleAccount == true) {
                    val bundle = Bundle()
                    bundle.putBoolean("isNonUKVehicleUpdating", true)
                    bundle.putParcelable(
                        Constants.CREATE_ACCOUNT_DATA,
                        arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
                    )
                    findNavController().navigate(
                        R.id.action_ukAndNonUkVehicleListFragment_to_NonUkDropDownVehicleListFragment,
                        bundle
                    )
                }
            }
        }
    }

        private fun setAdapter(isAccountVehicle: Boolean? = false, vehicle: String? = "UK") {

            hideAndShowRecyclerView()

            if (isAccountVehicle == true && vehicle == "UK") {
                val vehicleNo = arguments?.getString("VehicleNo")
                val plateRes = PlateInfoResponse()
                plateRes.number = vehicleNo.toString()
                plateRes.country = "UK"
                val vehicleRes = VehicleResponse(PlateInfoResponse(),plateRes, VehicleInfoResponse(), false, 0, 0.0 )
                if(vehicleList?.contains(vehicleRes)==false)
                    vehicleList?.add(vehicleRes)
                hideAndShowRecyclerView()
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

            if (vehicleList?.isNotEmpty() == true) {
                setBtnActivated()
            }
            if (vehicleList?.size?:0 < Constants.MAX_VEHICLE_SIZE) {
                setAddBtnActivated()
            } else {
                setAddBtnDisabled()
            }
        }

        private fun hideAndShowRecyclerView() {
            if (vehicleList?.isEmpty()!! || vehicleList?.size == 0) {
                binding.apply {
                    Logg.logging("MakePayMent", " calling inside ")

                    rvVehiclesList.gone()
                    noVehiclesAdded.visible()
                    addVehicle.text = getString(R.string.str_add_vehicle_to_account)
                }
                setBtnDisabled()
                setAddBtnActivated()
            } else {
                binding.rvVehiclesList.visibility = View.VISIBLE
                binding.noVehiclesAdded.visibility = View.GONE
                binding.addVehicle.text = requireContext().getString(R.string.txt_your_vehicle)

            }
        }

        override fun onAddClick(details: VehicleResponse) {
            addDialog?.dismiss()
            when {
                details.plateInfo?.country == Constants.UK -> {
                    vehicleList?.add(details)

                        setAdapter(true)
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
                    addVehicle.text = getString(R.string.str_add_vehicle_to_account)
                }
            }
        }

        override fun onItemClick(details: VehicleResponse?, pos: Int) {
            val bundle = Bundle()
            bundle.putBoolean("IsAccountVehicle", false)
            bundle.putInt(Constants.VEHICLE_SCREEN_KEY,Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT)
            bundle.putParcelable(
                Constants.DATA,
                details
            )

            findNavController().navigate(
                R.id.action_makePaymentAddVehicleFragment_to_addVehicleDoneFragment,
                bundle
            )
        }

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
