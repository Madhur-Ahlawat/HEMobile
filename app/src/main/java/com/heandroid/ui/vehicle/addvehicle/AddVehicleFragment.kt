package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentAddVehicleBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.payment.adapter.AddedVehicleListAdapter
import com.heandroid.ui.vehicle.addvehicle.dialog.AddVehicleDialog
import com.heandroid.ui.vehicle.addvehicle.dialog.AddVehicleListener
import com.heandroid.ui.vehicle.addvehicle.dialog.AddVehicleVRMDialog
import com.heandroid.ui.vehicle.vehiclelist.dialog.ItemClickListener
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import com.heandroid.utils.common.VehicleHelper
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleFragment : BaseFragment<FragmentAddVehicleBinding>(),
    View.OnClickListener, AddVehicleListener, ItemClickListener {

    private lateinit var mAdapter: AddedVehicleListAdapter
    private var addDialog: AddVehicleVRMDialog? = null
    private var loader: LoaderDialog? = null
    private var vehicleList = VehicleHelper.list
    private var mScreeType = 0
    private var mVDetails: VehicleResponse? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleBinding.inflate(inflater, container, false)

    override fun observer() {
    }

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        mVDetails = arguments?.getParcelable(Constants.DATA) as? VehicleResponse?

        if(mVDetails!= null){
            vehicleList?.add(mVDetails)
        }

        mAdapter = AddedVehicleListAdapter(this)
        mAdapter.setList(vehicleList)
        binding.rvVehiclesList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVehiclesList.setHasFixedSize(true)
        binding.rvVehiclesList.adapter = mAdapter

        setAdapter()
    }

    override fun initCtrl() {
        binding.addVehicleBtn.setOnClickListener(this)
        binding.findVehicle.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addVehicleBtn -> {
                addDialog = AddVehicleVRMDialog.newInstance(
                    getString(R.string.str_title),
                    getString(R.string.str_sub_title),
                    false,
                    this
                )
                addDialog?.show(childFragmentManager, AddVehicleDialog.TAG)
            }
            R.id.findVehicle -> {
                val bundle = Bundle()
                bundle.putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                bundle.putParcelable(Constants.DATA, mVDetails)

                if(mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD){
                    findNavController().navigate(R.id.action_addVehicleFragment_to_addVehicleSuccessFragment)
                }else {
                    findNavController().navigate(R.id.action_makePaymentAddVehicleFragment_to_addVehicleDoneFragment, bundle)
                }

            }
        }
    }

    override fun onAddClick(details: VehicleResponse) {
        addDialog?.dismiss()
        mVDetails = details
        if (details.plateInfo?.country == "UK") {
            vehicleList?.add(details)
            setAdapter()
        } else {
            val bundle = Bundle().apply {
                putParcelable(Constants.DATA, details)
                putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                putBoolean(Constants.PAYMENT_PAGE, true)
            }
          //  findNavController().navigate(R.id. , bundle)
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse?, pos: Int) {
        setAddBtnActivated()
        if (vehicleList?.size ?: 0 > 0) {
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

    override fun onItemClick(details: VehicleResponse?, pos: Int) {
        val bundle = Bundle()
        bundle.putInt(
            Constants.VEHICLE_SCREEN_KEY,
            Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT
        )
        bundle.putParcelable(
            Constants.DATA,
            details
        )

        findNavController().navigate(
            R.id.action_makePaymentAddVehicleFragment_to_addVehicleDoneFragment,
            bundle
        )
    }

    private fun setAdapter() {

        hideAndShowRecyclerView()

        mAdapter.setList(vehicleList)
        mAdapter.notifyDataSetChanged()

        if (vehicleList?.isNotEmpty() == true) {
            setBtnActivated()
        }
        if (vehicleList?.size ?: 0 < Constants.MAX_VEHICLE_SIZE) {
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
                addVehiclesTxt.text = getString(R.string.str_add_vehicle_to_account)
            }
            setBtnDisabled()
            setAddBtnActivated()
        } else {
            binding.rvVehiclesList.visibility = View.VISIBLE
            binding.noVehiclesAdded.visibility = View.GONE
            binding.addVehiclesTxt.text = requireContext().getString(R.string.txt_your_vehicle)

        }
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